package com.en_chu.calculator_api_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.en_chu.calculator_api_spring.model.UserProfileReq;
import com.en_chu.calculator_api_spring.model.UserProfileRes;
import com.en_chu.calculator_api_spring.service.UserProfileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User", description = "使用者個人理財檔案管理")
@SecurityRequirement(name = "bearer-key") // 告訴 Swagger 這支 API 需要 Token
public class UserProfileController {

	@Autowired
	private UserProfileService userProfileService;

	/**
	 * 新增個人資料 (Create) HTTP Method: POST
	 */
	@Operation(summary = "新增個人資料", description = "第一次建立個人資料時使用。如果不慎重複呼叫，後端會攔截並報錯 (或是你可以改用 Upsert 邏輯，但在這裡我們嚴格分開)。")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "建立成功"),
			@ApiResponse(responseCode = "400", description = "資料驗證失敗"),
			@ApiResponse(responseCode = "409", description = "資料已存在 (請改用更新)") })
	@PostMapping("/profile")
	public ResponseEntity<String> createProfile(@RequestBody @Valid UserProfileReq req) {
		// 呼叫 Service 的新增方法
		userProfileService.createProfile(req);
		return ResponseEntity.ok("建立成功");
	}

	/**
	 * 更新個人資料 (Update) HTTP Method: PUT
	 */
	@Operation(summary = "更新個人資料", description = "更新既有的資料。必須攜帶 id (PK) 與 Firebase Token。")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "更新成功"),
			@ApiResponse(responseCode = "400", description = "ID 為空或資料格式錯誤"),
			@ApiResponse(responseCode = "404", description = "找不到該 ID 的資料 (或權限不足)") })
	@PutMapping("/profile")
	public ResponseEntity<String> updateProfile(@RequestBody @Valid UserProfileReq req) {
		// 嚴格檢查：更新一定要帶 ID
		if (req.getId() == null) {
			return ResponseEntity.badRequest().body("更新失敗：必須提供 ID");
		}

		// 呼叫 Service 的更新方法
		userProfileService.updateProfile(req);
		return ResponseEntity.ok("更新成功");
	}

	/**
	 * 取得當前登入使用者的個人資料 HTTP Method: GET URL: /api/user/profile
	 */
	@Operation(summary = "取得個人資料", description = "回傳null或是個人資料")
	@GetMapping("/profile")
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