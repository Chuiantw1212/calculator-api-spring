package com.en_chu.calculator_api_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.en_chu.calculator_api_spring.entity.UserProfile;
import com.en_chu.calculator_api_spring.model.PersonalProfileReq;
import com.en_chu.calculator_api_spring.service.UserProfileService;
import com.en_chu.calculator_api_spring.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User Profile", description = "使用者個人理財檔案管理")
@SecurityRequirement(name = "bearer-key") // 告訴 Swagger 這支 API 需要 Token
public class UserProfileController {

	@Autowired
	private UserProfileService userProfileService;

	/**
	 * 新增或更新個人資料 (Upsert) * @param req 前端傳來的個人資料 DTO
	 * 
	 * @param authentication 從 Spring Security Context 拿到的登入憑證 (Firebase Token)
	 */
	@Operation(summary = "更新個人資料", description = "如果資料不存在則新增，存在則更新 (Upsert)。必須攜帶 Firebase Token。")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "更新成功"),
			@ApiResponse(responseCode = "400", description = "資料格式驗證失敗 (如年份錯誤、必填欄位為空)"),
			@ApiResponse(responseCode = "403", description = "權限不足 (試圖修改他人資料)") })
	@PutMapping("/profile")
	public ResponseEntity<UserProfile> upsertProfile(@RequestBody @Valid PersonalProfileReq req) {
		// 1. 直接從 Token 拿 UID (不用前端傳，資安滿分)
		String uid = SecurityUtils.getCurrentUserUid();

		// 2. 查詢資料
		UserProfile profile = userProfileService.getProfile(uid);

		// 3. 如果沒資料，回傳 404 Not Found
		if (profile == null) {
			return ResponseEntity.notFound().build();
		}

		// 4. 有資料，回傳 200 OK + JSON
		return ResponseEntity.ok(profile);
	}
}