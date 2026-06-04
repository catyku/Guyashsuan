package com.law.admin.service;

import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

@Service
public class FileUploadService {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);

    // SVG 不在允許清單：SVG 可內嵌 JavaScript，屬於 CWE-434 風險，故移除
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp");

    // CWE-434：各格式 magic bytes，用於驗證實際檔案內容（防止偽造副檔名攻擊）
    private static final byte[] MAGIC_JPEG        = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    private static final byte[] MAGIC_PNG         = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static final byte[] MAGIC_GIF87       = {0x47, 0x49, 0x46, 0x38, 0x37, 0x61};
    private static final byte[] MAGIC_GIF89       = {0x47, 0x49, 0x46, 0x38, 0x39, 0x61};
    private static final byte[] MAGIC_BMP         = {0x42, 0x4D};
    private static final byte[] MAGIC_RIFF        = {0x52, 0x49, 0x46, 0x46}; // RIFF (WebP 容器)
    private static final byte[] MAGIC_WEBP_MARKER = {0x57, 0x45, 0x42, 0x50}; // "WEBP" 位於 offset 8

    @Value("${app.upload.img-width:1024}")
    private int imgWidth;

    @Value("${app.upload.img-height:768}")
    private int imgHeight;

    @Value("${app.upload.thumbnail-threshold:5242880}")
    private long thumbnailThreshold;

    /**
     * 上傳圖片到指定子目錄，回傳相對路徑。
     * 防護：CWE-22/23/36（路徑穿越）、CWE-434（不受限制檔案上傳）、CWE-772/775（資源洩漏）、CWE-778（日誌）。
     */
    public String uploadImage(MultipartFile file, String subDir, String rootPath) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("檔案不可為空");
        }

        // CWE-434：驗證副檔名（只取最後一個 . 以防雙重副檔名攻擊）
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            logger.warn("[SECURITY] 拒絕不支援的副檔名 (CWE-434): ext={}", ext);
            throw new IllegalArgumentException("不支援的檔案格式，僅允許: " + ALLOWED_EXTENSIONS);
        }

        // CWE-434：驗證 magic bytes（確認實際檔案類型與副檔名相符）
        // CWE-772/775：使用 try-with-resources 確保 InputStream 一定被關閉
        byte[] header = readFileHeader(file);
        if (!isValidImageMagicBytes(ext, header)) {
            logger.warn("[SECURITY] 檔案 magic bytes 與副檔名不符，拒絕上傳 (CWE-434): ext={}", ext);
            throw new IllegalArgumentException("檔案內容與格式不符，拒絕上傳");
        }

        // CWE-22/23/36：正規化路徑並確認 subDir 解析後仍在 rootPath 之內
        Path resolvedRoot = Paths.get(rootPath).toAbsolutePath().normalize();
        Path uploadDir = resolvedRoot.resolve(subDir).normalize();
        if (!uploadDir.startsWith(resolvedRoot)) {
            logger.warn("[SECURITY] 路徑穿越攻擊嘗試 (CWE-22): subDir={}", subDir);
            throw new IllegalArgumentException("非法的上傳子目錄");
        }

        Files.createDirectories(uploadDir);

        String newFilename = UUID.randomUUID().toString() + ext;
        Path filePath = uploadDir.resolve(newFilename).normalize();

        // 再次確認目標路徑在 uploadDir 之內
        if (!filePath.startsWith(uploadDir)) {
            throw new IllegalArgumentException("非法的目標路徑");
        }

        // CWE-772/775：縮圖時以 try-with-resources 確保 InputStream 關閉
        if (file.getSize() > thumbnailThreshold) {
            logger.info("檔案大小 {} 超過閾值 {}，進行縮圖", file.getSize(), thumbnailThreshold);
            try (InputStream is = file.getInputStream()) {
                Thumbnails.of(is)
                        .size(imgWidth, imgHeight)
                        .outputQuality(0.85)
                        .toFile(filePath.toFile());
            }
        } else {
            file.transferTo(filePath.toFile());
        }

        logger.info("[UPLOAD] 圖片上傳成功: subDir={}, filename={}", subDir, newFilename);
        return "/" + subDir + "/" + newFilename;
    }

    /**
     * 刪除圖片。
     * 防護：CWE-22/23/36（路徑穿越）、CWE-778（日誌）。
     */
    public boolean deleteImage(String relativePath, String rootPath) {
        if (relativePath == null || relativePath.isBlank()) {
            return false;
        }
        try {
            // CWE-22/23/36：正規化路徑並驗證在 rootPath 範圍內
            Path resolvedRoot = Paths.get(rootPath).toAbsolutePath().normalize();
            String stripped = relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;
            Path filePath = resolvedRoot.resolve(stripped).normalize();

            if (!filePath.startsWith(resolvedRoot)) {
                logger.warn("[SECURITY] 路徑穿越攻擊嘗試，刪除請求被拒 (CWE-22/36): relativePath={}", relativePath);
                return false;
            }

            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                logger.info("[DELETE] 圖片刪除成功: path={}", relativePath);
            }
            return deleted;
        } catch (IOException e) {
            logger.error("刪除圖片失敗: {}", relativePath, e);
            return false;
        }
    }

    /**
     * 讀取檔案前 12 bytes，用於 magic bytes 驗證（CWE-434）。
     * 使用 try-with-resources 防止資源洩漏（CWE-772/775）。
     */
    private byte[] readFileHeader(MultipartFile file) throws IOException {
        int headerSize = 12;
        byte[] header = new byte[headerSize];
        try (InputStream is = file.getInputStream()) {
            int read = is.read(header, 0, headerSize);
            if (read < headerSize) {
                Arrays.fill(header, read, headerSize, (byte) 0);
            }
        }
        return header;
    }

    /**
     * 根據副檔名驗證檔案 magic bytes（CWE-434）。
     */
    private boolean isValidImageMagicBytes(String ext, byte[] header) {
        return switch (ext) {
            case ".jpg", ".jpeg" -> startsWith(header, MAGIC_JPEG);
            case ".png"          -> startsWith(header, MAGIC_PNG);
            case ".gif"          -> startsWith(header, MAGIC_GIF87) || startsWith(header, MAGIC_GIF89);
            case ".bmp"          -> startsWith(header, MAGIC_BMP);
            case ".webp"         -> startsWith(header, MAGIC_RIFF)
                                    && header.length >= 12
                                    && Arrays.equals(Arrays.copyOfRange(header, 8, 12), MAGIC_WEBP_MARKER);
            default -> false;
        };
    }

    private boolean startsWith(byte[] data, byte[] prefix) {
        if (data.length < prefix.length) return false;
        for (int i = 0; i < prefix.length; i++) {
            if (data[i] != prefix[i]) return false;
        }
        return true;
    }
}