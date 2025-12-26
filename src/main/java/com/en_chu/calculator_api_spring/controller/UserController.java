package com.en_chu.calculator_api_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.en_chu.calculator_api_spring.model.UserCareerReq;
import com.en_chu.calculator_api_spring.model.UserFullDataRes;
import com.en_chu.calculator_api_spring.model.UserProfileReq;
import com.en_chu.calculator_api_spring.service.UserService;
import com.en_chu.calculator_api_spring.util.SecurityUtils; // ✅ 記得 import 剛剛寫的工具

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User", description = "使用者整合資訊管理")
@SecurityRequirement(name = "bearer-key") // Swagger 用
public class UserController {

	@Autowired
	private UserService userService;

	// ==========================================
	// 1. 總覽 (初始化用)
	// ==========================================

	@Operation(summary = "取得完整使用者資料", description = "包含 User + Profile + Career，用於頁面初始化。")
	@GetMapping("/me")
	public ResponseEntity<UserFullDataRes> getMe() {
		// 1. 識別身分 (若沒登入會直接噴 401 Exception)
		String uid = SecurityUtils.getCurrentUserUid();

		// 2. 傳入 uid 查詢
		UserFullDataRes response = userService.getFullUserData(uid);

		if (response == null) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(response);
	}

	// ==========================================
	// 2. Profile 卡片更新
	// ==========================================
	@Operation(summary = "更新個人資料")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "更新成功") })
	@PutMapping("/profile")
	public ResponseEntity<String> updateProfile(@RequestBody @Valid UserProfileReq req) {
		// 1. 識別身分
		String uid = SecurityUtils.getCurrentUserUid();

		// 2. 傳入 uid 與請求資料 (Service 負責將 uid 填入 Entity)
		// 注意：這裡不需要檢查 req.getId()，因為我們是根據 uid 更新
		userService.updateProfile(uid, req);

		return ResponseEntity.ok("更新成功");
	}

	// ==========================================
	// 3. Career 卡片更新
	// ==========================================

	@Operation(summary = "更新職涯與收入資料")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "更新成功") })
	@PutMapping("/career")
	public ResponseEntity<String> updateCareer(@RequestBody @Valid UserCareerReq req) {
		// 1. 識別身分
		String uid = SecurityUtils.getCurrentUserUid();

		// 2. 傳入 uid 與請求資料
		userService.updateCareer(uid, req);

		return ResponseEntity.ok("更新成功");
	}
}