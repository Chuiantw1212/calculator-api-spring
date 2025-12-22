package com.en_chu.calculator_api_spring.model;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
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