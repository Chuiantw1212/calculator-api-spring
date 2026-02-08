package com.en_chu.calculator_api_spring.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 專門用於更新使用者職涯資料的請求 DTO。
 */
@Data
public class UserCareerUpdateReq {

    private String industry;
    private String companyName;
    private String jobTitle;
    private Boolean isWorking;
    private Integer monthlySalary;
    private BigDecimal annualBonusMonths;
    private BigDecimal avgWorkingHoursPerDay;
    private Integer retirementAge;
    
}
