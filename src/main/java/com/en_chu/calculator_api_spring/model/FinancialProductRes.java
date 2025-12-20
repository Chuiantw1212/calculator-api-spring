package com.en_chu.calculator_api_spring.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FinancialProductRes {
	@JsonProperty("@context")
	private String context = "https://schema.org";

	@JsonProperty("@type")
	private String type = "FinancialProduct";

	private String name;
	private String description;
	// 複利計算相關
	private Double annualPercentageRate;
}