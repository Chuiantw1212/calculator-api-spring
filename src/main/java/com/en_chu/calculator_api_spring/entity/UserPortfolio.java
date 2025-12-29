package com.en_chu.calculator_api_spring.entity;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true) // 2. 比較時包含父類別
public class UserPortfolio extends UserBaseEntity {
	private String countryCode;
	private String currency;

	private BigDecimal exchangeRate; // 匯率
	private BigDecimal marketValue; // 庫存市值
	private BigDecimal realizedPnl; // 已實現損益
}