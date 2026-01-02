package com.en_chu.calculator_api_spring.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;
	
@Data
@EqualsAndHashCode(callSuper = true)
public class UserCreditCard extends UserBaseEntity {
	private String firebaseUid;

	private String name;
	private String bankName;

	/**
	 * 用途分類代碼 對應 opt_credit_card_usage_type
	 */
	private String usageType;

	/**
	 * 每月試算支出 使用者輸入的 5 個月帳單平均值
	 */
	private BigDecimal estimatedMonthlyExpense;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}