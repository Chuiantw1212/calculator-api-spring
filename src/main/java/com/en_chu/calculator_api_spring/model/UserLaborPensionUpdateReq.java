package com.en_chu.calculator_api_spring.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 專門用於更新使用者勞工退休金 (新制) 資料的請求 DTO。
 */
@Data
public class UserLaborPensionUpdateReq {

    private BigDecimal personalContributionRate;
    private BigDecimal employerContributionRate;
    private Integer totalContributionYears;
    private BigDecimal avgAnnualReturnRate;
    private Integer currentBalance;

}
