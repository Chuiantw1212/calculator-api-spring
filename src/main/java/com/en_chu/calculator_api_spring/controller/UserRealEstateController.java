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

import com.en_chu.calculator_api_spring.model.UserRealEstateDto;
import com.en_chu.calculator_api_spring.service.UserRealEstateService;
import com.en_chu.calculator_api_spring.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/real-estate")
@Tag(name = "User Real Estate", description = "房地產資產管理 (CRUD)")
@RequiredArgsConstructor
public class UserRealEstateController {

	private final UserRealEstateService userRealEstateService;

	// ==========================================
	// 1. 取得列表 (GET List)
	// ==========================================
	@Operation(summary = "取得房地產列表", description = "回傳該使用者的所有房地產，依建立時間倒序")
	@GetMapping
	public ResponseEntity<List<UserRealEstateDto>> getList() {
		String uid = SecurityUtils.getCurrentUserUid();
		return ResponseEntity.ok(userRealEstateService.getList(uid));
	}

	// ❌ 已移除：取得單筆 (GET Detail)

	// ==========================================
	// 2. 新增 (POST)
	// ==========================================
	@Operation(summary = "新增房地產", description = "需傳入 name, size, usageType 等必要資訊")
	@PostMapping
	public ResponseEntity<UserRealEstateDto> create(@RequestBody @Valid UserRealEstateDto req) {
		String uid = SecurityUtils.getCurrentUserUid();
		return ResponseEntity.ok(userRealEstateService.create(uid, req));
	}

	// ==========================================
	// 3. 更新 (PUT)
	// ==========================================
	@Operation(summary = "更新房地產資訊", description = "更新指定 ID 的房地產資料，後端會重新計算總價")
	@PutMapping("/{id}")
	public ResponseEntity<UserRealEstateDto> update(@PathVariable Long id, @RequestBody @Valid UserRealEstateDto req) {

		String uid = SecurityUtils.getCurrentUserUid();
		// Update 成功後，通常直接回傳該筆最新的 DTO 給前端更新畫面
		return ResponseEntity.ok(userRealEstateService.update(uid, id, req));
	}

	// ==========================================
	// 4. 刪除 (DELETE)
	// ==========================================
	@Operation(summary = "刪除房地產")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		String uid = SecurityUtils.getCurrentUserUid();

		userRealEstateService.delete(uid, id);

		return ResponseEntity.noContent().build();
	}
}