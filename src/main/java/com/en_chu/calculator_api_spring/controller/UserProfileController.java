package com.en_chu.calculator_api_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.en_chu.calculator_api_spring.model.UserProfileReq;
import com.en_chu.calculator_api_spring.model.UserProfileRes;
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
	public ResponseEntity<String> upsertProfile(@RequestBody @Valid UserProfileReq req) {
		// 1. 直接從 Token 拿 UID (不用前端傳，資安滿分)
		SecurityUtils.getCurrentUserUid();

		// 業務邏輯
		userProfileService.saveProfile(req);

		return ResponseEntity.ok("更新成功");
	}

	/**
	 * 取得當前登入使用者的個人資料 HTTP Method: GET URL: /api/user/profile
	 */
	@Operation(summary = "取得個人資料", description = "回傳null或是個人資料")
	@GetMapping
	public ResponseEntity<UserProfileRes> getMyProfile() {
		// 1. 呼叫 Service
		// Service 內部會自己去 SecurityUtils 拿 UID，並透過 MyBatis 直接回傳 DTO
		UserProfileRes response = userProfileService.getProfile();

		// 2. 處理資料不存在的情況 (看你的業務邏輯)
		// 如果使用者剛註冊還沒填資料，response 可能是 null
		if (response == null) {
			// 選擇 A: 回傳 204 No Content (代表請求成功但沒資料)
			return ResponseEntity.noContent().build();

			// 選擇 B: 回傳 200 OK 帶空物件 (如果前端不喜歡處理 204)
			// return ResponseEntity.ok(new UserProfileRes());
		}

		// 3. 回傳 200 OK 與資料
		return ResponseEntity.ok(response);
	}
}