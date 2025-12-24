package com.en_chu.calculator_api_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.en_chu.calculator_api_spring.model.UserCareerReq;
import com.en_chu.calculator_api_spring.model.UserFullDataRes;
import com.en_chu.calculator_api_spring.model.UserProfileReq;
import com.en_chu.calculator_api_spring.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User", description = "使用者整合資訊管理")
@SecurityRequirement(name = "bearer-key")
public class UserController {

	@Autowired
	private UserService userService;

	// ==========================================
	// 1. 總覽 (初始化用)
	// ==========================================

	@Operation(summary = "取得完整使用者資料", description = "包含 User + Profile + Career，用於頁面初始化。")
	@GetMapping("/me")
	public ResponseEntity<UserFullDataRes> getMe() {
		UserFullDataRes response = userService.getFullUserData();
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
		if (req.getId() == null) {
			return ResponseEntity.badRequest().body("更新失敗：必須提供 ID");
		}
		userService.updateProfile(req);
		return ResponseEntity.ok("更新成功");
	}

	// ==========================================
	// 3. Career 卡片更新
	// ==========================================

	@Operation(summary = "更新職涯與收入資料")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "更新成功") })
	@PutMapping("/career")
	public ResponseEntity<String> updateCareer(@RequestBody @Valid UserCareerReq req) {
		userService.updateCareer(req);
		return ResponseEntity.ok("更新成功");
	}
}