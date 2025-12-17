package com.en_chu.calculator_api_spring.service;

import com.en_chu.calculator_api_spring.model.CompoundInterestRequest;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CalculatorService {

	public BigDecimal calculateCompoundInterest(CompoundInterestRequest request) {
		// 公式：本金 * (1 + 利率)^年數
		// 在 Java 裡，BigDecimal 的運算不能用 +, -, *, /，要用方法呼叫

		BigDecimal one = BigDecimal.ONE;
		BigDecimal ratePlusOne = request.getRate().add(one); // 1 + rate

		// pow 是次方
		BigDecimal finalAmount = request.getPrincipal().multiply(ratePlusOne.pow(request.getYears()));

		// 金融業鐵律：最後一定要設小數點位數與捨入模式 (這裡設 2 位，四捨五入)
		return finalAmount.setScale(2, RoundingMode.HALF_UP);
	}
}