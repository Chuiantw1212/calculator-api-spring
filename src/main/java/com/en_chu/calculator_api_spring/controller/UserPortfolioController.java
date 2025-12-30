package com.en_chu.calculator_api_spring.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.en_chu.calculator_api_spring.model.UserPortfolioDto;
import com.en_chu.calculator_api_spring.service.UserPortfolioService;
import com.en_chu.calculator_api_spring.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/user/portfolios")
@Tag(name = "User Portfolio", description = "投資組合管理 (Stocks/ETFs/Funds)")
@RequiredArgsConstructor // 1. 改用建構子注入 (Lombok)
public class UserPortfolioController {

	private final UserPortfolioService userPortfolioService;

	// ==========================================
	// 1. 取得列表 (GET)
	// ==========================================
	@Operation(summary = "取得投資組合列表", description = "回傳該使用者的所有持倉，依市值排序")
	@GetMapping
	public ResponseEntity<List<UserPortfolioDto>> getList() { // 2. 回傳 List<Dto>
		String uid = SecurityUtils.getCurrentUserUid();
		return ResponseEntity.ok(userPortfolioService.getList(uid));
	}

	// ==========================================
	// 2. 新增 (POST)
	// ==========================================
	@Operation(summary = "新增一筆持倉", description = "需傳入 countryCode, currency 等必要資訊")
	@PostMapping
	public ResponseEntity<UserPortfolioDto> create(@RequestBody @Valid UserPortfolioDto req) {
		// 3. 注意：現在 create 必須接收 Body，不能是空的了
		String uid = SecurityUtils.getCurrentUserUid();
		return ResponseEntity.ok(userPortfolioService.create(uid, req));
	}

	// ==========================================
	// 3. 更新 (PUT)
	// ==========================================
	@Operation(summary = "更新持倉數值")
	@PutMapping("/{id}")
	public ResponseEntity<UserPortfolioDto> update(@PathVariable Long id, @RequestBody @Valid UserPortfolioDto req) {

		String uid = SecurityUtils.getCurrentUserUid();

		// 4. Update 現在會回傳「更新後的最新資料」，讓前端可以直接更新畫面
		return ResponseEntity.ok(userPortfolioService.update(uid, id, req));
	}

	// ==========================================
	// 4. 刪除 (DELETE)
	// ==========================================
	@Operation(summary = "刪除持倉")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		String uid = SecurityUtils.getCurrentUserUid();

		userPortfolioService.delete(uid, id);

		// 5. Delete 成功標準做法是回傳 204 No Content
		return ResponseEntity.noContent().build();
	}
}