package com.en_chu.calculator_api_spring.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSecurity {

	private Long id;
	private String firebaseUid; // 關聯欄位

	private String countryCode;
	private String currency;

	private BigDecimal exchangeRate; // 匯率
	private BigDecimal marketValue; // 庫存市值
	private BigDecimal realizedPnl; // 已實現損益

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}