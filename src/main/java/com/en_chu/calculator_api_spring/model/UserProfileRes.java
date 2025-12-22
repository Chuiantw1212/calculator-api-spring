package com.en_chu.calculator_api_spring.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileRes {

	// ✅ 新增：資料庫的 Primary Key (Long 對應 BigInt)
	// 前端拿到這個 id 後，下次要更新 (PUT) 時必須帶上它
	private Long id;

	// ❌ 移除：birthYear
	// 因為資料庫欄位已經 Drop 掉了，Entity 也移除了，這裡留著只會永遠是 null
	// private Integer birthYear;

	private LocalDate birthDate;
	private String gender;
	private Integer currentAge;
	private Integer lifeExpectancy;
	private Integer marriageYear;
	private String careerInsuranceType;
	private String biography;
}