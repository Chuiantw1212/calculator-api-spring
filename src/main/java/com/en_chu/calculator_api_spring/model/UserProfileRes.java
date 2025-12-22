package com.en_chu.calculator_api_spring.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor // ❗重要：BeanUtils 需要這個才能 new 空物件
@AllArgsConstructor // 配合 Builder 使用
public class UserProfileRes {
	private Integer birthYear;
	private LocalDate birthDate;
	private String gender;
	private Integer currentAge;
	private Integer lifeExpectancy;
	private Integer marriageYear;
	private String careerInsuranceType;
	private String biography;
}