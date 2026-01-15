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

    // ==========================================
    // Phase 2: 放緩期 (Slow-Go Years)
    // ==========================================

    @Schema(description = "[時間軸] Slow-Go 開始年齡 (Default: 75)", example = "75")
    private Integer slowGoStartAge;

    @Schema(description = "防禦策略代碼 (D1-D10)", example = "D2")
    private String defenseTierCode;

    @Schema(description = "醫療月預算 (保費+自費預備)", example = "5000")
    private BigDecimal monthlyMedicalCost;
    
    @Schema(description = "重大傷病計算代碼", example = "CI_High_Coverage")
    private String criticalIllnessCode;

    @Schema(description = "重大傷病一次性準備金", example = "1000000")
    private BigDecimal criticalIllnessReserve;

    // ==========================================
    // Phase 3: 長照期 (No-Go Years)
    // ==========================================

    @Schema(description = "[時間軸] 長照啟動年齡", example = "85")
    private Integer ltcStartAge;

    @Schema(description = "照顧模式代碼", example = "HOME_CARE")
    private String ltcCareMode;

    @Schema(description = "每月人力/機構費 (主費用)", example = "45000")
    private BigDecimal ltcMonthlyCost;

    @Schema(description = "每月耗材雜支", example = "5000")
    private BigDecimal ltcMonthlySupplies;
}