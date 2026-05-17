package com.law.admin.config;

import com.law.admin.model.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.http.fileupload.impl.IOFileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.multipart.MultipartException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 處理 multipart 請求解析失敗（例如客戶端中斷連線、Cloudflare 截斷請求）
     * 這類錯誤通常是客戶端問題，不應回傳 500
     */
    @ExceptionHandler({MultipartException.class, IOFileUploadException.class})
    public ResponseEntity<ErrorResponse> handleMultipartException(Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("BAD_REQUEST", "請求資料不完整，請重新嘗試"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(this::toFieldMessage)
            .toList();
        return ResponseEntity.badRequest().body(new ErrorResponse("INVALID", errors));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException ex) {
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(this::toFieldMessage)
            .toList();
        return ResponseEntity.badRequest().body(new ErrorResponse("INVALID", errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations()
            .stream()
            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
            .toList();
        return ResponseEntity.badRequest().body(new ErrorResponse("INVALID", errors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse("INVALID", "請求資料格式錯誤"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("SERVER_ERROR", "系統錯誤，請稍後再試"));
    }

    private String toFieldMessage(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }
}