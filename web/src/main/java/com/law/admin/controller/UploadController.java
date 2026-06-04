package com.law.admin.controller;

import com.law.admin.service.FileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Set;

/**
 * 編輯器圖片上傳 API（WangEditor 使用）
 */
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

    // CWE-22/23/36：允許清單，限制 subDir 只能是已知安全目錄，防止路徑穿越攻擊
    private static final Set<String> ALLOWED_SUB_DIRS = Set.of("editor", "banner", "case", "share", "attorney");

    private final FileUploadService fileUploadService;

    @Value("${app.upload.root-path:../uploads-law}")
    private String rootPath;

    public UploadController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    /**
     * 通用圖片上傳（WangEditor / 封面圖共用）
     * WangEditor 期望回傳格式: { errno: 0, data: { url: "full_url" } }
     * TinyMCE 格式也同時支援: { location: "full_url" }
     * 可選參數 subDir: 指定子目錄，僅允許白名單目錄（CWE-22/23/36）
     */
    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file,
                                          @RequestParam(value = "subDir", defaultValue = "editor") String subDir,
                                          Authentication auth) throws Exception {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errno", 1, "msg", "檔案不可為空"));
        }

        // CWE-22/23/36：以白名單驗證 subDir，拒絕任何非預期目錄
        if (!ALLOWED_SUB_DIRS.contains(subDir)) {
            logger.warn("[SECURITY] 上傳請求使用非法 subDir (CWE-22): subDir={}, user={}",
                    subDir, auth != null ? auth.getName() : "anonymous");
            return ResponseEntity.badRequest().body(Map.of("errno", 1, "msg", "非法的上傳目錄"));
        }

        // CWE-778：記錄上傳操作（不記錄檔案內容）
        logger.info("[UPLOAD] 圖片上傳請求: subDir={}, user={}, contentType={}",
                subDir, auth != null ? auth.getName() : "anonymous", file.getContentType());

        String relativePath = fileUploadService.uploadImage(file, subDir, rootPath);
        String url = "/uploads" + relativePath;
        // WangEditor 格式
        return ResponseEntity.ok(Map.of(
                "errno", 0,
                "data", Map.of("url", url, "alt", "")
        ));
    }
}