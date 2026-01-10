package com.en_chu.calculator_api_spring.model;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "使用者退休開支與照護規劃參數")
public class UserRetirementExpenseDto extends BaseDto {

	// --- 高齡醫療 ---
	@Schema(description = "醫療等級代碼 (basic/enhanced/premium)", example = "basic")
	private String medicalCode;

	@Schema(description = "每月醫療預備金", example = "2000")
	@Min(0)
	private BigDecimal medicalExpense;

	// --- 退休生活 ---
	@Schema(description = "活躍模式代碼 (active/moderate/basic)", example = "active")
	private String lifestyleCode;

	@Schema(description = "夢想係數 (生活費乘數)", example = "1.2")
	private BigDecimal dreamCoefficient;

	// --- 長照規劃 ---
	@Schema(description = "預估失能/需照護年齡", example = "75")
	@NotNull
	private Integer disabilityAge;

	@Schema(description = "照護模式代碼 (day_care/foreign_helper...)", example = "day_care")
	private String careModeCode;

	@Schema(description = "每月照護費用預估", example = "45000")
	@Min(0)
	private BigDecimal disabilityExpense;

	@Schema(description = "照護期間生活費調整係數", example = "1.0")
	private BigDecimal livingExpenseAdjustment;

	// --- 兩階段預估結果 ---
	@Schema(description = "階段一(活躍期) 預估每月總支出", example = "55000")
	private BigDecimal phase1MonthlyExpense;

	@Schema(description = "階段二(照護期) 預估每月總支出", example = "85000")
	private BigDecimal phase2MonthlyExpense;

	@Schema(description = "階段二(照護期) 啟動年齡", example = "75")
	private Integer phase2StartAge;
}