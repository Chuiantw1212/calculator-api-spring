package com.en_chu.calculator_api_spring.entity;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserRetirement extends UserBaseEntity {

    /**
     * 家庭型態: 'single' (獨居/單身) | 'couple' (伴侶/雙人)
     */
    private String householdType;

    /**
     * 居住模式代碼 (對應 opt_housing_*)
     */
    private String housingMode;

    /**
     * 居住月費用
     */
    private BigDecimal housingCost;

    /**
     * 健康照護等級代碼 (對應 opt_health_tier)
     */
    private String healthTierCode;

    /**
     * 健康照護月費用
     */
    private BigDecimal healthCost;

    /**
     * 活躍生活等級代碼 (對應 opt_active_living)
     */
    private String activeLivingCode;

    /**
     * 活躍生活月費用 (不含居住與醫療)
     */
    private BigDecimal activeLivingCost;

    // ==========================================
    // Phase 2: 放緩期 (Slow-Go Years)
    // ==========================================

    /**
     * [時間軸] Slow-Go 開始年齡 (即 Go-Go 結束年齡)
     * Default: 75
     */
    private Integer slowGoStartAge;

    /**
     * 防禦策略代碼 (D1-D10)
     */
    private String defenseTierCode;

    /**
     * 醫療月預算 (保費 + 自費醫療儲備)
     * *注意：此階段醫療通膨通常較高 (J-Curve)*
     */
    private BigDecimal monthlyMedicalCost;

    /**
     * 重大傷病一次性準備金 (Critical Illness Reserve)
     * 用於：癌症標靶、達文西手術等大額支出
     */
    private BigDecimal criticalIllnessReserve;

    // ==========================================
    // Phase 3: 長照期 (No-Go Years)
    // ==========================================

    /**
     * [時間軸] 長照啟動年齡 (No-Go Start)
     * 上限值：應小於 UserLaborInsurance.predictedRemainingLife
     */
    private Integer ltcStartAge;

    /**
     * 照顧模式代碼
     */
    private String ltcCareMode;

    /**
     * 每月人力/機構費 (Main Cost)
     */
    private BigDecimal ltcMonthlyCost;

    /**
     * 每月耗材雜支 (Supplies Cost) - 尿布/營養品
     */
    private BigDecimal ltcMonthlySupplies;

}