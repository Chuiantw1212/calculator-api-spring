package com.en_chu.calculator_api_spring.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.en_chu.calculator_api_spring.model.CompoundInterestRequest;
import com.en_chu.calculator_api_spring.service.CalculatorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

// 1. 標示這是一個 REST 控制器
@RestController
// 2. 設定這個 Controller 的統一入口路徑
@RequestMapping("/api/calculate")
// 3. Swagger 分類標籤 (讓文件更好看)
@Tag(name = "財務計算機 API", description = "提供複利計算與相關金融工具")
public class CalculatorApi {

	@Autowired
	private CalculatorService calculatorService;

	// 4. 定義 POST 請求的路徑: /api/calculate/compound
	@PostMapping("/compound")
	// 5. Swagger 操作說明 (顯示在 API 文件上)
	@Operation(summary = "複利計算功能", description = "輸入本金、利率、年分，回傳複利計算後的總金額")
	public ResponseEntity<BigDecimal> calculateCompoundInterest(@RequestBody CompoundInterestRequest request) { // @RequestBody
																												// 負責把
																												// JSON
																												// 轉成
																												// Java
																												// 物件

		// 呼叫 Service 層進行商業邏輯運算
		BigDecimal result = calculatorService.calculateCompoundInterest(request);

		// 回傳 200 OK 狀態碼以及計算結果
		return ResponseEntity.ok(result);
	}
}