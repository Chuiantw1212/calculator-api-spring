package com.en_chu.calculator_api_spring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.en_chu.calculator_api_spring.entity.UserPortfolio;
import com.en_chu.calculator_api_spring.model.UserPortfolioUpdateReq;
import com.en_chu.calculator_api_spring.service.UserPortfolioService;
import com.en_chu.calculator_api_spring.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user/portfolio")
@Tag(name = "User Portfolio", description = "投資組合管理 (Stocks/ETFs/Funds)")
public class UserPortfolioController {

	@Autowired
	private UserPortfolioService userPortfolioService; // ✅ 變數名稱修正：不再叫 securityService

	// ==========================================
	// 1. 取得列表 (GET)
	// ==========================================
	@Operation(summary = "取得投資組合列表")
	@GetMapping
	public ResponseEntity<List<UserPortfolio>> getList() {
		// ✅ 這裡呼叫，如果沒登入，直接回傳 401
		String uid = SecurityUtils.getCurrentUserUid();

		// Service 方法已改名為 getUserPortfolio
		return ResponseEntity.ok(userPortfolioService.getUserPortfolio(uid));
	}

	// ==========================================
	// 2. 新增 (POST)
	// ==========================================
	@Operation(summary = "新增一筆持倉 (預設)")
	@PostMapping
	public ResponseEntity<UserPortfolio> create() {
		String uid = SecurityUtils.getCurrentUserUid();

		// Service 方法已改名為 createDefaultPosition
		UserPortfolio newRecord = userPortfolioService.createDefaultPosition(uid);
		return ResponseEntity.ok(newRecord);
	}

	// ==========================================
	// 3. 刪除 (DELETE)
	// ==========================================
	@Operation(summary = "刪除持倉")
	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		String uid = SecurityUtils.getCurrentUserUid();

		// Service 方法已改名為 deletePosition
		userPortfolioService.deletePosition(uid, id);
		return ResponseEntity.ok().body("Deleted successfully");
	}

	// ==========================================
	// 4. 更新 (PUT)
	// ==========================================
	@Operation(summary = "更新持倉數值")
	@PutMapping("/{id}")
	public ResponseEntity<String> update(@PathVariable Long id, @RequestBody @Valid UserPortfolioUpdateReq req) { // 建議加上
																													// @Valid

		String uid = SecurityUtils.getCurrentUserUid();

		// Service 方法已改名為 updatePosition
		userPortfolioService.updatePosition(uid, id, req);
		return ResponseEntity.ok().body("Updated successfully");
	}
}