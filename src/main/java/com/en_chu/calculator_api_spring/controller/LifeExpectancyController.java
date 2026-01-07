package com.en_chu.calculator_api_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.en_chu.calculator_api_spring.model.LifeExpectancyRes;
import com.en_chu.calculator_api_spring.service.LifeExpectancyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/tools")
@Tag(name = "Tools API", description = "通用工具類查詢")
public class LifeExpectancyController {

	@Autowired
	private LifeExpectancyService lifeExpectancyService;

	@Operation(summary = "查詢預期壽命", description = "根據年份、性別、年齡，查詢國發會推估的預期總壽命")
	@GetMapping("/life-expectancy")
	public ResponseEntity<LifeExpectancyRes> getLifeExpectancy(
			@Parameter(description = "年份", example = "2025") @RequestParam Integer year,

			@Parameter(description = "性別 (MALE/FEMALE)", example = "MALE") @RequestParam String gender,

			@Parameter(description = "目前年齡", example = "30") @RequestParam Integer age) {
		// 簡單的參數防呆 (可選)
		if (age < 0 || age > 150) {
			return ResponseEntity.badRequest().build();
		}

		LifeExpectancyRes result = lifeExpectancyService.getLifeExpectancy(year, gender, age);

		if (result == null) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(result);
	}
}