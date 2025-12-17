package com.en_chu.calculator_api_spring.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ErrorResponse {
	private int status; // 例如 400
	private String message; // 例如 "本金不能為負數"
	private LocalDateTime timestamp; // 發生時間 (稽核用)

	// 一個方便的建構子
	public ErrorResponse(int status, String message) {
		this.status = status;
		this.message = message;
		this.timestamp = LocalDateTime.now();
	}
}