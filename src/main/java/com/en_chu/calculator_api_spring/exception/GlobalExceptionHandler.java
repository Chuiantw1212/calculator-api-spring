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

/**
 * 全域例外處理器。
 * 使用 @ControllerAdvice，Spring 會自動將這個類別應用到所有的 @RestController。
 * 它像一個「守護者」，專門捕捉從 Controller 層拋出的、未被處理的例外，
 * 並將它們轉換為統一的、對前端友好的 JSON 錯誤回應。
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 處理資料庫存取相關的例外。
     * 當 MyBatis 無法連接到資料庫，或 SQL 語句執行出錯時，Spring 會拋出 DataAccessException。
     * @param ex Spring 的資料存取例外
     * @return 503 Service Unavailable
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(DataAccessException ex) {
        // 安全日誌：只記錄最根本的原因 (e.g., "relation '...' does not exist")，
        // 避免在日誌中洩漏完整的 SQL 查詢語句或參數。
        log.error("❌ 資料庫存取異常: {}", ex.getMostSpecificCause().getMessage());
        
        String errorMessage = "資料庫服務暫時無法使用，請稍後再試。";
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), errorMessage);
        
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * 處理由 @Valid 註解觸發的 DTO 驗證失敗例外。
     * 當請求 Body 的內容不符合 DTO 中定義的驗證規則 (如 @NotNull, @Size) 時觸發。
     * @param ex DTO 驗證例外
     * @return 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // 安全日誌：只記錄第一個驗證失敗的欄位和訊息，避免記錄整個 DTO 的內容。
        log.warn("⚠️ DTO 驗證失敗: {}", ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());

        String errorMessage = "請求的資料格式不正確或缺少必要欄位。";
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 處理請求 Body 缺失或 JSON 格式錯誤的例外。
     * 當前端發送一個 POST/PUT 請求，但沒有提供 Body，或者 Body 不是一個合法的 JSON 時觸發。
     * @param ex 訊息不可讀例外
     * @return 400 Bad Request
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.warn("🚫 無法讀取請求 Body: {}", ex.getMessage());

        String errorMessage = "請求 Body 缺失或 JSON 格式錯誤。";
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 處理所有其他未被上述 ExceptionHandler 捕獲的例外。
     * 這是最後一道防線，確保任何未預期的錯誤都能被優雅地處理，而不是直接拋出 500 錯誤頁面。
     * @param ex 通用例外
     * @return 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        // 對於完全未知的例外，我們在日誌中記錄完整的堆疊追蹤 (Stack Trace)，
        // 這對於開發者在事後排查問題至關重要。
        log.error("🔥 發生未預期的伺服器內部錯誤: ", ex);
        
        String errorMessage = "伺服器內部發生未預期的錯誤。";
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMessage);
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
