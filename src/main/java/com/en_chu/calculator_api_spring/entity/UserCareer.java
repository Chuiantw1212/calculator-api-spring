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
public class UserCareer { // ✅ 改名

	private Long id;
	private String firebaseUid;

	private BigDecimal monthlyBaseSalary;
	private BigDecimal otherAllowance;
	private BigDecimal laborInsurance;
	private BigDecimal healthInsurance;
	private BigDecimal otherDeduction;

	private BigDecimal pensionRate;
	private BigDecimal pensionAmount;

	private BigDecimal stockDeduction;
	private BigDecimal stockCompanyMatch;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}