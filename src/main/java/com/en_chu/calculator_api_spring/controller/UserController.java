package com.en_chu.calculator_api_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 引入新的 Model
import com.en_chu.calculator_api_spring.model.UserCareerReq;
import com.en_chu.calculator_api_spring.model.UserCareerRes;
import com.en_chu.calculator_api_spring.model.UserFullDataRes;
import com.en_chu.calculator_api_spring.model.UserProfileReq;
import com.en_chu.calculator_api_spring.model.UserProfileRes;
// 引入總管 Service
import com.en_chu.calculator_api_spring.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User", description = "使用者整合資訊管理 (Profile & Career)")
@SecurityRequirement(name = "bearer-key") // 告訴 Swagger 這支 API 需要 Token
public class UserController {

	// 改用 UserService (Facade 模式) 統一管理
	@Autowired
	private UserService userService;

	// ==========================================
	// 1. 總覽 (App 初始化 / 首頁)
	// ==========================================

	/**
	 * 取得使用者完整資料 (包含 User + Profile + Career) HTTP Method: GET URL: /api/v1/user/me
	 */
	@Operation(summary = "取得完整使用者資料", description = "回傳 User 基本資訊、Profile 以及 Career 資料。用於 APP 初始化或登入後首頁。")
	@GetMapping("/me")
	public ResponseEntity<UserFullDataRes> getMe() {
		UserFullDataRes response = userService.getFullUserData();

		if (response == null) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(response);
	}

	// ==========================================
	// 2. Profile 區塊 (個人資料)
	// ==========================================

	/**
	 * 新增個人資料 (Create)
	 */
	@Operation(summary = "新增個人資料", description = "第一次建立個人資料時使用。")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "建立成功"),
			@ApiResponse(responseCode = "400", description = "資料驗證失敗"),
			@ApiResponse(responseCode = "409", description = "資料已存在") })
	@PostMapping("/profile")
	public ResponseEntity<String> createProfile(@RequestBody @Valid UserProfileReq req) {
		// 委派給 UserService
		userService.createProfile(req);
		return ResponseEntity.ok("建立成功");
	}

	/**
	 * 更新個人資料 (Update)
	 */
	@Operation(summary = "更新個人資料", description = "更新既有的資料。必須攜帶 id (PK)。")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "更新成功"),
			@ApiResponse(responseCode = "400", description = "ID 為空或資料格式錯誤") })
	@PutMapping("/profile")
	public ResponseEntity<String> updateProfile(@RequestBody @Valid UserProfileReq req) {
		if (req.getId() == null) {
			return ResponseEntity.badRequest().body("更新失敗：必須提供 ID");
		}
		userService.updateProfile(req);
		return ResponseEntity.ok("更新成功");
	}

	/**
	 * 取得個人資料 (單獨)
	 */
	@Operation(summary = "取得個人資料", description = "回傳null或是個人資料")
	@GetMapping("/profile")
	public ResponseEntity<UserProfileRes> getMyProfile() {
		// 改呼叫 userService 的方法
		UserProfileRes response = userService.getProfileOnly();

		if (response == null) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(response);
	}

	// ==========================================
	// 3. Career 區塊 (職涯與收入) - 新增部分
	// ==========================================

	/**
	 * 更新職涯與收入資料 HTTP Method: PUT (包含新增與修改邏輯)
	 */
	@Operation(summary = "更新職涯與收入資料", description = "設定薪資、保險、退休金提撥等資訊。")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "更新成功"),
			@ApiResponse(responseCode = "400", description = "資料驗證失敗") })
	@PutMapping("/career")
	public ResponseEntity<String> updateCareer(@RequestBody @Valid UserCareerReq req) {
		// 這裡通常不需要像 Profile 分開 POST/PUT，Service 內部會判斷 ID 是否存在來決定 Insert 或 Update
		userService.updateCareer(req);
		return ResponseEntity.ok("更新成功");
	}

	/**
	 * 取得職涯與收入資料 (單獨)
	 */
	@Operation(summary = "取得職涯與收入資料", description = "回傳薪資結構與財務設定。")
	@GetMapping("/career")
	public ResponseEntity<UserCareerRes> getMyCareer() {
		UserCareerRes response = userService.getCareerOnly();

		if (response == null) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(response);
	}
}