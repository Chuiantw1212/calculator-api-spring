package com.en_chu.calculator_api_spring.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class UserPortfolioDto {
	// 不需要 id，因為 id 會在 URL Path 裡
	// 不需要 firebase_uid，因為會從 Token 取

	private String countryCode; // e.g., US
	private String currency; // e.g., USD
	private BigDecimal exchangeRate;
	private BigDecimal marketValue;
	private BigDecimal realizedPnl;
}