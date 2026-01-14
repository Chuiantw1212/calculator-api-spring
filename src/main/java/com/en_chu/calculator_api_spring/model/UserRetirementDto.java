package com.en_chu.calculator_api_spring.model;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "退休生活型態參數")
public class UserRetirementDto extends BaseDto {

	@Schema(description = "家庭型態", example = "single", allowableValues = { "single", "couple" })
	@NotNull
	@Pattern(regexp = "^(single|couple)$", message = "家庭型態必須為 'single' 或 'couple'")
	private String householdType;

	@Schema(description = "居住模式代碼", example = "SOLO_RENT_SUITE")
	private String housingMode;

	@Schema(description = "居住月費用", example = "16000")
	private BigDecimal housingCost;

	@Schema(description = "健康照護等級代碼", example = "STANDARD")
	private String healthTierCode;

	@Schema(description = "健康照護月費用", example = "6700")
	private BigDecimal healthCost;

	@Schema(description = "活躍生活等級代碼", example = "Q3")
	private String activeLivingCode;

	@Schema(description = "活躍生活月費用", example = "60000")
	private BigDecimal activeLivingCost;
}