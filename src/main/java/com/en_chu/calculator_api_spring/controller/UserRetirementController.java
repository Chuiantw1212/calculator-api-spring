package com.en_chu.calculator_api_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.en_chu.calculator_api_spring.entity.UserRetirement;
import com.en_chu.calculator_api_spring.model.UserRetirementDto;
import com.en_chu.calculator_api_spring.service.UserRetirementService;
import com.en_chu.calculator_api_spring.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user/retirement")
@Tag(name = "User Retirement", description = "使用者退休生活參數設定")
public class UserRetirementController {

	@Autowired
	private UserRetirementService userRetirementService;

	/**
	 * 取得使用者的退休生活設定 API: GET /api/v1/user/retirement
	 */
	@Operation(summary = "取得退休設定", description = "取得目前使用者的所有退休階段設定資料")
	@GetMapping
	public ResponseEntity<UserRetirement> getRetirement() {
		String uid = SecurityUtils.getCurrentUserUid();
		UserRetirement result = userRetirementService.getByUid(uid);
		// 若無資料回傳 null，前端會收到 200 OK 及空白 body
		return ResponseEntity.ok(result);
	}

	/**
	 * [完整更新] 更新或建立退休生活設定 (Upsert) API: PUT /api/v1/user/retirement 說明: 這是全量更新，DTO
	 * 中未傳的欄位可能會被覆蓋為 null (視 BeanUtils 行為而定)
	 */
	@Operation(summary = "完整更新 (Upsert)", description = "全量更新資料，未傳入的欄位可能會被清空")
	@PutMapping
	public ResponseEntity<String> updateRetirement(@RequestBody @Valid UserRetirementDto req) {
		String uid = SecurityUtils.getCurrentUserUid();
		userRetirementService.updateRetirement(uid, req);
		return ResponseEntity.ok("更新成功");
	}

	// ==========================================
	// 局部更新 (Partial Updates / Cards)
	// ==========================================

	/**
	 * [局部更新] Phase 1: 活躍期 (Go-Go Years)
	 * API: PATCH /api/v1/user/retirement/gogo 
	 */
	@Operation(summary = "局部更新 - 活躍期 (Go-Go)", description = "只更新活躍期的相關欄位 (如 activeLivingCost, housingCost)")
	@PatchMapping("/go-go")
	public ResponseEntity<String> patchGoGo(@RequestBody UserRetirementDto req) {
		// 這裡不加 @Valid，允許前端只傳送部分欄位
		String uid = SecurityUtils.getCurrentUserUid();
		userRetirementService.patchRetirement(uid, req);
		return ResponseEntity.ok("Go-Go 設定更新成功");
	}

	/**
	 * [局部更新] Phase 2: 放緩期 (Slow-Go Years)
	 * API: PATCH /api/v1/user/retirement/slow-go
	 */
	@Operation(summary = "局部更新 - 放緩期 (Slow-Go)", description = "只更新放緩期的相關欄位 (如 defenseTierCode, slowGoStartAge)")
	@PatchMapping("/slow-go")
	public ResponseEntity<String> patchSlowGo(@RequestBody UserRetirementDto req) {
		String uid = SecurityUtils.getCurrentUserUid();
		userRetirementService.patchRetirement(uid, req);
		return ResponseEntity.ok("Slow-Go 設定更新成功");
	}

	/**
	 * [局部更新] Phase 3: 長照期 (No-Go Years)
	 * API: PATCH /api/v1/user/retirement/no-go
	 */
	@Operation(summary = "局部更新 - 長照期 (No-Go)", description = "只更新長照期的相關欄位 (如 ltcCareMode, ltcMonthlyCost)")
	@PatchMapping("/no-go")
	public ResponseEntity<String> patchNoGo(@RequestBody UserRetirementDto req) {
		String uid = SecurityUtils.getCurrentUserUid();
		userRetirementService.patchRetirement(uid, req);
		return ResponseEntity.ok("No-Go 設定更新成功");
	}
}