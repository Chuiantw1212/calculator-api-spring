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

import com.en_chu.calculator_api_spring.entity.UserPortfolio; // ✅ Entity 改名
import com.en_chu.calculator_api_spring.model.UserPortfolioUpdateReq; // ✅ DTO 改名
import com.en_chu.calculator_api_spring.service.UserPortfolioService; // ✅ Service 改名
import com.en_chu.calculator_api_spring.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user/portfolio") // ✅ URL 語意：使用者的投資組合
@Tag(name = "User Portfolio", description = "投資組合管理 (Stocks/ETFs/Funds)")
public class UserPortfolioController {

	@Autowired
	private UserPortfolioService userPortfolioService;

	// ==========================================
	// 1. 查詢投資組合 (所有持倉)
	// ==========================================
	@Operation(summary = "取得投資組合列表", description = "列出目前持有的所有證券部位")
	@GetMapping
	public ResponseEntity<List<UserPortfolio>> getMyPortfolio() {
		String uid = SecurityUtils.getCurrentUserUid();
		return ResponseEntity.ok(userPortfolioService.getUserPortfolio(uid));
	}

	// ==========================================
	// 2. 新增部位 (預設空殼)
	// ==========================================
	@Operation(summary = "新增一筆持倉", description = "建立預設的投資部位 (空資料)")
	@PostMapping
	public ResponseEntity<UserPortfolio> createDefaultPosition() {
		String uid = SecurityUtils.getCurrentUserUid();
		return ResponseEntity.ok(userPortfolioService.createDefaultPosition(uid));
	}

	// ==========================================
	// 3. 更新部位
	// ==========================================
	@Operation(summary = "更新持倉數值")
	@PutMapping("/{id}")
	public ResponseEntity<String> updatePosition(@PathVariable Long id,
			@RequestBody @Valid UserPortfolioUpdateReq req) {

		String uid = SecurityUtils.getCurrentUserUid();
		// 這裡 Service 方法名稱建議也從 updateSecurity 改成 updatePosition
		userPortfolioService.updatePosition(uid, id, req);

		return ResponseEntity.ok("部位更新成功");
	}

	// ==========================================
	// 4. 刪除部位
	// ==========================================
	@Operation(summary = "刪除持倉")
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deletePosition(@PathVariable Long id) {
		String uid = SecurityUtils.getCurrentUserUid();
		userPortfolioService.deletePosition(uid, id);
		return ResponseEntity.ok("部位已刪除");
	}
}