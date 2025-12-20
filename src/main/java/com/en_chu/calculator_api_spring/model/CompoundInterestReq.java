package com.en_chu.calculator_api_spring.model;

import lombok.Data;
import java.math.BigDecimal;

@Data // 👈 這個註解會自動幫你產生 setResult() 方法
public class CompoundInterestReq {

	private BigDecimal principal; // 本金
	private BigDecimal rate; // 利率
	private int years; // 年分

	// 👇 請補上這一行！沒有它，你就不能存結果
	private BigDecimal result;
}