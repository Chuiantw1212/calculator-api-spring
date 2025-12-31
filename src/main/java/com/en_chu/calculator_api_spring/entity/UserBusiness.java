package com.en_chu.calculator_api_spring.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserBusiness extends UserBaseEntity {

	private String name;

	private LocalDate startDate;
	
	/**
     * 專案年期 (例如：5年、10年)
     */
    private Integer projectYears;

	// deemed_6 | verified | exempt
	private String taxCategory;

	private BigDecimal acquisitionCost;

	// monthly | total
	private String incomeMode;

	private BigDecimal totalAccumulatedIncome;

	private BigDecimal monthlyIncome;

	private BigDecimal monthlyCost;

	private BigDecimal loanAmount;

	private BigDecimal loanInterestRate;
}