package com.en_chu.calculator_api_spring.entity;

import java.time.LocalDate;

import lombok.Data;

/**
 * 用戶基本資料實體類 MyBatis 不需要 @Entity 或 @Table
 */
@Data
public class UserProfile {

	private String uid; // Firebase uid
	private Integer birthYear;
	private LocalDate birthDate;
	private String gender;
	private Integer currentAge;
	private Integer lifeExpectancy;
	private Integer marriageYear;
	private String careerInsuranceType;
	private String biography;
}