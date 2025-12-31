package com.en_chu.calculator_api_spring.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserBusinessDto extends BaseDto {

	// ==========================================
	// 1. 基本資料 (必填)
	// ==========================================
	@Schema(description = "事業名稱", example = "網拍副業", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "事業名稱不能為空") // ❌ 強制必填
	private String name;
	
	@Schema(description = "開始經營日期 (YYYY-MM-DD)", example = "2023-01-01", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "開始經營日期必填") // ❌ 強制必填
	@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.en_chu.calculator_api_spring.util.FlexibleDateDeserializer.class)
	private LocalDate startDate;
	
	/**
     * 專案年期
     * 用於定義 IRR 計算的現金流陣列長度
     */
	// 移除 @NotNull，只保留 @Min 防止使用者填負數
    @Min(value = 1, message = "若填寫年期，至少需為 1 年")
    private Integer projectYears;	

	@Schema(description = "稅務類別", example = "exempt", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "稅務類別必填")
	@Pattern(regexp = "^(deemed_6|verified|exempt)$", message = "無效的稅務類別")
	private String taxCategory;

	@Schema(description = "初始投入成本 (開辦費)", example = "50000")
	@NotNull(message = "初始成本必填")
	@DecimalMin(value = "0.0", message = "成本不能小於 0")
	private BigDecimal acquisitionCost;

	// ==========================================
	// 2. 收入模式與數值 (必填)
	// ==========================================

	@Schema(description = "收入輸入模式 (monthly/total)", example = "monthly", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "收入模式必填")
	@Pattern(regexp = "^(monthly|total)$", message = "無效的收入模式")
	private String incomeMode;

	@Schema(description = "月平均收入 (系統計算核心)", example = "15000")
	@NotNull(message = "月平均收入必填") // 即使前端算好，後端也要擋 null
	@DecimalMin(value = "0.0", message = "月收入不能小於 0")
	private BigDecimal monthlyIncome;

	@Schema(description = "月平均成本/支出", example = "3000")
	@NotNull(message = "月平均成本必填")
	@DecimalMin(value = "0.0", message = "月成本不能小於 0")
	private BigDecimal monthlyCost;

	// Optional: 累計營收 (因為 TypeScript 定義這欄位是 ? 可選的)
	// 如果前端沒傳，Java 預設為 null，建議初始化為 0 避免計算 NPE
	@Schema(description = "歷史累計總營收 (當 mode=total 時使用)", example = "0")
	@DecimalMin(value = "0.0", message = "累計營收不能小於 0")
	private BigDecimal totalAccumulatedIncome = BigDecimal.ZERO;

	// ==========================================
	// 3. 貸款資訊 (必填)
	// ==========================================

	@Schema(description = "創業貸款餘額", example = "100000")
	@NotNull(message = "貸款餘額必填 (無貸款請傳 0)")
	@DecimalMin(value = "0.0", message = "貸款不能小於 0")
	private BigDecimal loanAmount;

	@Schema(description = "貸款年利率 (%)", example = "2.5")
	@NotNull(message = "貸款利率必填 (無貸款請傳 0)")
	@DecimalMin(value = "0.0", message = "利率不能小於 0")
	private BigDecimal loanInterestRate;
}