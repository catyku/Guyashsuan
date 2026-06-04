package com.law.admin.controller;

import com.law.admin.service.FileUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 編輯器圖片上傳 API（WangEditor 使用）
 */
@RestController
@RequestMapping("/api/upload")
public class UploadController {

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
     * 可選參數 subDir: 指定子目錄，預設 "editor"
     */
    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file,
                                          @RequestParam(value = "subDir", defaultValue = "editor") String subDir,
                                          Authentication auth) throws Exception {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errno", 1, "msg", "檔案不可為空"));
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("image/")) {
            return ResponseEntity.badRequest().body(Map.of("errno", 1, "msg", "僅允許上傳圖片檔案"));
        }

        String relativePath = fileUploadService.uploadImage(file, subDir, rootPath);
        String url = "/uploads/" + relativePath;
        // WangEditor 格式
        return ResponseEntity.ok(Map.of(
                "errno", 0,
                "data", Map.of("url", url, "alt", file.getOriginalFilename() != null ? file.getOriginalFilename() : "")
        ));
    }
}