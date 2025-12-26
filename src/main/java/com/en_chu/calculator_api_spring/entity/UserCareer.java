package com.en_chu.calculator_api_spring.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder; // 注意這裡的 import

@Data
@SuperBuilder // 1. 改用 SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true) // 2. 比較時包含父類別
public class UserCareer extends UserBaseEntity { // 3. 繼承父類別

	private Long id;

	// firebaseUid 已被移除，因為父類別有了

	// 薪資結構
	private BigDecimal baseSalary;
	private BigDecimal otherAllowance;
	private BigDecimal laborInsurance;
	private BigDecimal healthInsurance;
	private BigDecimal otherDeduction;

	// 退休金
	private BigDecimal pensionRate;
	private BigDecimal pensionAmount;

	// 股票/福利
	private BigDecimal stockDeduction;
	private BigDecimal stockCompanyMatch;

	// 系統欄位
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}