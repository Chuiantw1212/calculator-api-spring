package com.en_chu.calculator_api_spring.model;

import lombok.Data;
import java.math.BigDecimal; // 金融業的神，請背下來

@Data
public class CompoundInterestRequest {
	private BigDecimal principal; // 本金 (千萬別用 double)
	private BigDecimal rate; // 年利率 (例如 0.05 代表 5%)
	private int years; // 年數
}