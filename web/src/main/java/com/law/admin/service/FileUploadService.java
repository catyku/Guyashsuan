package com.law.admin.service;

import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

@Service
public class FileUploadService {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp", ".svg");

    @Value("${app.upload.img-width:1024}")
    private int imgWidth;

    @Value("${app.upload.img-height:768}")
    private int imgHeight;

    @Value("${app.upload.thumbnail-threshold:5242880}")
    private long thumbnailThreshold;

    /**
     * 上傳圖片到指定子目錄，回傳相對路徑
     */
    public String uploadImage(MultipartFile file, String subDir, String rootPath) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("檔案不可為空");
        }

        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }

        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new IllegalArgumentException("不支援的檔案格式，僅允許: " + ALLOWED_EXTENSIONS);
        }

        String newFilename = UUID.randomUUID().toString() + ext;

        Path uploadDir = Paths.get(rootPath, subDir);
        Files.createDirectories(uploadDir);

        Path filePath = uploadDir.resolve(newFilename);

        // 超過閾值則自動縮圖
        if (file.getSize() > thumbnailThreshold) {
            logger.info("檔案大小 {} 超過閾值 {}，進行縮圖", file.getSize(), thumbnailThreshold);
            Thumbnails.of(file.getInputStream())
                    .size(imgWidth, imgHeight)
                    .outputQuality(0.85)
                    .toFile(filePath.toFile());
        } else {
            file.transferTo(filePath.toFile());
        }

        return "/" + subDir + "/" + newFilename;
    }

    /**
     * 刪除圖片
     */
    public boolean deleteImage(String relativePath, String rootPath) {
        if (relativePath == null || relativePath.isBlank()) {
            return false;
        }
        try {
            Path filePath = Paths.get(rootPath, relativePath.startsWith("/") ? relativePath.substring(1) : relativePath);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            logger.error("刪除圖片失敗: {}", relativePath, e);
            return false;
        }
    }
}