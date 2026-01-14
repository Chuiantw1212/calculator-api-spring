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
@EqualsAndHashCode(callSuper = true)
public class UserRetirement extends UserBaseEntity {

	/**
	 * 家庭型態: 'single' (獨居/單身) | 'couple' (伴侶/雙人)
	 */
	private String householdType;

	/**
	 * 居住模式代碼 (對應 opt_housing_*)
	 */
	private String housingMode;

	/**
	 * 居住月費用
	 */
	private BigDecimal housingCost;

	/**
	 * 健康照護等級代碼 (對應 opt_health_tier)
	 */
	private String healthTierCode;

	/**
	 * 健康照護月費用
	 */
	private BigDecimal healthCost;

	/**
	 * 活躍生活等級代碼 (對應 opt_active_living)
	 */
	private String activeLivingCode;

	/**
	 * 活躍生活月費用 (不含居住與醫療)
	 */
	private BigDecimal activeLivingCost;
}