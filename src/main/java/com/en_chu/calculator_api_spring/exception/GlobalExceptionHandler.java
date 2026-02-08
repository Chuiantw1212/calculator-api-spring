package com.en_chu.calculator_api_spring.exception;

import com.en_chu.calculator_api_spring.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 處理資料庫存取相關的例外
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(DataAccessException ex) {
        // 在日誌中，只記錄根本原因，避免洩漏過多細節
        log.error("❌ 資料庫存取異常: {}", ex.getMostSpecificCause().getMessage());
        
        String errorMessage = "資料庫服務暫時無法使用，請稍後再試。";
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), errorMessage);
        
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * 處理 @Valid 驗證失敗的例外
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // 我們可以從例外中獲取更詳細的驗證失敗資訊，但為了安全，只回傳一個通用的訊息
        log.warn("⚠️ DTO 驗證失敗: {}", ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());

        String errorMessage = "請求的資料格式不正確或缺少必要欄位。";
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 處理請求 Body 缺失或格式錯誤的例外
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.warn("🚫 無法讀取請求 Body: {}", ex.getMessage());

        String errorMessage = "請求 Body 缺失或 JSON 格式錯誤。";
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 處理所有其他未被捕獲的例外
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        // 對於未知的例外，我們在日誌中記錄完整的堆疊追蹤，以便除錯
        log.error("🔥 發生未預期的伺服器內部錯誤: ", ex);
        
        String errorMessage = "伺服器內部發生未預期的錯誤。";
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMessage);
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
