package com.en_chu.calculator_api_spring.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 專門用於更新使用者稅務資料的請求 DTO。
 */
@Data
public class UserTaxUpdateReq {

    private BigDecimal taxBracketRate;
    private Integer taxDeductionAmount;

}
