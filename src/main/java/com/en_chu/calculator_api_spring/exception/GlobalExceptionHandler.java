package com.en_chu.calculator_api_spring.exception;

import com.en_chu.calculator_api_spring.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(DataAccessException ex) {
        log.error("❌ 資料庫存取異常: {}", ex.getMessage());
        
        // 建立一個更具描述性的錯誤訊息
        String errorMessage = "資料庫服務暫時無法使用，請稍後再試。";
        
        // 回傳 503 Service Unavailable 狀態碼和結構化的 JSON
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), errorMessage);
        
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("🔥 發生未預期的錯誤: {}", ex.getMessage(), ex);
        
        String errorMessage = "伺服器內部發生未預期的錯誤。";
        
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMessage);
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
