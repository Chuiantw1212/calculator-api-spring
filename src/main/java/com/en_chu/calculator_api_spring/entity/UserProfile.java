package com.en_chu.calculator_api_spring.entity;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// ❌ 移除 @Entity, @Table
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
	private Long id; // 內部的 PK

	// 🔴 原本: private String uid;
	// 🟢 改為:
	private String firebaseUid; // 外部的 Auth ID
	private LocalDate birthDate;
	private String gender;
	private Integer currentAge;
	private Integer lifeExpectancy;
	private Integer marriageYear;
	private String careerInsuranceType;
	private String biography;
}