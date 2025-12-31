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

import com.en_chu.calculator_api_spring.model.UserBusinessDto;
import com.en_chu.calculator_api_spring.service.UserBusinessService;
import com.en_chu.calculator_api_spring.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/user/businesses") // ✅ 使用複數路徑，符合 RESTful
@Tag(name = "User Business", description = "副業與創業項目管理 (嚴格驗證模式)")
@RequiredArgsConstructor
public class UserBusinessController {

	private final UserBusinessService userBusinessService;

	// ==========================================
	// 1. 取得列表 (GET List)
	// ==========================================
	@Operation(summary = "取得所有事業列表", description = "回傳該使用者的所有創業項目")
	@GetMapping
	public ResponseEntity<List<UserBusinessDto>> getList() {
		String uid = SecurityUtils.getCurrentUserUid();
		return ResponseEntity.ok(userBusinessService.getList(uid));
	}

	// ==========================================
	// 2. 新增 (POST) - 嚴格模式
	// ==========================================
	@Operation(summary = "新增事業項目", description = "⚠️ 必須傳入完整資料 (Name, StartDate, IncomeMode...)，後端不提供預設值")
	@PostMapping
	public ResponseEntity<UserBusinessDto> create(
			// ✅ 這裡不加 required=false，預設就是必填
			// ✅ 加上 @Valid，會觸發 DTO 裡的 @NotNull, @DecimalMin 驗證
			@RequestBody @Valid UserBusinessDto req) {
		String uid = SecurityUtils.getCurrentUserUid();

		// 不需要檢查 req == null，因為 Spring 在進入這行之前就會先攔截並回傳 400
		return ResponseEntity.ok(userBusinessService.create(uid, req));
	}

	// ==========================================
	// 3. 更新 (PUT) - 嚴格模式
	// ==========================================
	@Operation(summary = "更新事業資訊")
	@PutMapping("/{id}")
	public ResponseEntity<UserBusinessDto> update(@PathVariable Long id, @RequestBody @Valid UserBusinessDto req // ✅
																													// 更新也同樣嚴格檢查
	) {

		String uid = SecurityUtils.getCurrentUserUid();
		return ResponseEntity.ok(userBusinessService.update(uid, id, req));
	}

	// ==========================================
	// 4. 刪除 (DELETE)
	// ==========================================
	@Operation(summary = "刪除事業")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		String uid = SecurityUtils.getCurrentUserUid();

		userBusinessService.delete(uid, id);

		// 刪除成功，回傳 204 No Content
		return ResponseEntity.noContent().build();
	}
}