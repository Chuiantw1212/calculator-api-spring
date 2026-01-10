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
public class UserRetirementExpense extends UserBaseEntity {

	// --- 高齡醫療 ---
	private String medicalCode;
	private BigDecimal medicalExpense;

	// --- 退休生活 ---
	private String lifestyleCode;
	private BigDecimal dreamCoefficient;

	// --- 長照規劃 ---
	private Integer disabilityAge;
	private String careModeCode;
	private BigDecimal disabilityExpense;
	private BigDecimal livingExpenseAdjustment;

	// --- 兩階段預估結果 ---
	private BigDecimal phase1MonthlyExpense;
	private BigDecimal phase2MonthlyExpense;
	private Integer phase2StartAge;
}	