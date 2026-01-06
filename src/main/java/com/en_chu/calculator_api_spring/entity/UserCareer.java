package com.en_chu.calculator_api_spring.entity;

import java.math.BigDecimal;

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
	
	// 每個月實領金額
	private BigDecimal monthlyNetIncome;
}