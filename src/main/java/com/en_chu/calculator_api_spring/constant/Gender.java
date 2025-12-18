package com.en_chu.calculator_api_spring.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender implements BaseEnum {
	MALE("M", "男性"), FEMALE("F", "女性");

	private final String code;
	private final String description;
}