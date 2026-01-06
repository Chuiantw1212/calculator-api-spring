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

import com.en_chu.calculator_api_spring.entity.UserCreditCard;
import com.en_chu.calculator_api_spring.model.UserCreditCardDto;
import com.en_chu.calculator_api_spring.service.UserCreditCardService;
import com.en_chu.calculator_api_spring.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
	
@RestController
@RequestMapping("/api/v1/user/credit-cards") // 調整：加上 user 前綴，與 Portfolio 一致
@Tag(name = "User Credit Card", description = "使用者信用卡管理")
@RequiredArgsConstructor
public class UserCreditCardController {

	private final UserCreditCardService userCreditCardService;

	// ==========================================
	// 1. 取得列表 (GET)
	// ==========================================
	@Operation(summary = "取得信用卡列表", description = "回傳該使用者的所有信用卡")
	@GetMapping
	public ResponseEntity<List<UserCreditCard>> getList() {
		String uid = SecurityUtils.getCurrentUserUid();
		return ResponseEntity.ok(userCreditCardService.getCards(uid));
	}

	// ==========================================
	// 2. 取得單筆 (GET)
	// ==========================================
	@Operation(summary = "取得單張信用卡詳情")
	@GetMapping("/{id}")
	public ResponseEntity<UserCreditCard> getOne(@PathVariable Long id) {
		String uid = SecurityUtils.getCurrentUserUid();
		return ResponseEntity.ok(userCreditCardService.getCard(id, uid));
	}

	// ==========================================
	// 3. 新增 (POST)
	// ==========================================
	@Operation(summary = "新增信用卡", description = "可不傳 Body，後端自動生成預設值")
	@PostMapping
	public ResponseEntity<UserCreditCard> create(
			// 這裡保留 required = false 的邏輯
			@RequestBody(required = false) UserCreditCardDto req) {

		String uid = SecurityUtils.getCurrentUserUid();

		// 防呆：若前端完全沒傳 body，手動補一個空的物件
		if (req == null) {
			req = new UserCreditCardDto();
		}

		// 雖然 REST 標準建議 201 Created，但為了配合您的參考範本風格，這裡統一用 ok (200)
		return ResponseEntity.ok(userCreditCardService.createCard(req, uid));
	}

	// ==========================================
	// 4. 更新 (PUT)
	// ==========================================
	@Operation(summary = "更新信用卡資訊")
	@PutMapping("/{id}")
	public ResponseEntity<UserCreditCard> update(@PathVariable Long id, @RequestBody @Valid UserCreditCardDto req) {

		String uid = SecurityUtils.getCurrentUserUid();

		// 確保 DTO 內的 ID 與路徑上的 ID 一致
		req.setId(id);

		return ResponseEntity.ok(userCreditCardService.updateCard(req, uid));
	}

	// ==========================================
	// 5. 刪除 (DELETE)
	// ==========================================
	@Operation(summary = "刪除信用卡")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		String uid = SecurityUtils.getCurrentUserUid();

		userCreditCardService.deleteCard(id, uid);

		return ResponseEntity.noContent().build();
	}
}