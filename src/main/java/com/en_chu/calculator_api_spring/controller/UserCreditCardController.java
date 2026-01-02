package com.en_chu.calculator_api_spring.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/credit-cards")
@RequiredArgsConstructor
@Tag(name = "信用卡管理", description = "提供信用卡的新增、修改、刪除與查詢功能")
@Validated
public class UserCreditCardController {

	private final UserCreditCardService userCreditCardService;

	/**
	 * 輔助方法：從 Security Context 取得當前登入使用者的 Firebase UID 假設您的 Security Filter Chain
	 * 已經將 UID 設定為 Principal 或 Authentication 的 Name
	 */
	private String getCurrentUserUid() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			// 這裡通常由 Security Filter 攔截，但做雙重檢查較安全
			throw new RuntimeException("使用者未登入");
		}
		// 根據您的 JWT Filter 實作，UID 可能在 Name 或 Principal 中
		return authentication.getName();
	}

	@GetMapping
	@Operation(summary = "取得所有信用卡", description = "列出當前登入使用者的所有信用卡設定")
	public ResponseEntity<List<UserCreditCard>> getAllCards() {
		String uid = getCurrentUserUid();
		List<UserCreditCard> cards = userCreditCardService.getCards(uid);
		return ResponseEntity.ok(cards);
	}

	@GetMapping("/{id}")
	@Operation(summary = "取得單張信用卡詳情", description = "根據 ID 查詢信用卡，需驗證擁有權")
	public ResponseEntity<UserCreditCard> getCard(@PathVariable Long id) {
		String uid = getCurrentUserUid();
		UserCreditCard card = userCreditCardService.getCard(id, uid);
		return ResponseEntity.ok(card);
	}

	@PostMapping
	@Operation(summary = "新增信用卡", description = "支援部分欄位輸入，後端會自動補齊預設值")
	public ResponseEntity<UserCreditCard> createCard(@Valid @RequestBody UserCreditCardDto dto) {
		String uid = getCurrentUserUid();
		UserCreditCard newCard = userCreditCardService.createCard(dto, uid);
		return ResponseEntity.status(HttpStatus.CREATED).body(newCard);
	}

	@PutMapping
	@Operation(summary = "更新信用卡", description = "僅更新有傳值的欄位 (Partial Update)")
	public ResponseEntity<UserCreditCard> updateCard(@Valid @RequestBody UserCreditCardDto dto) {
		// 更新時 ID 是必須的，通常建議在 DTO 驗證，或是這裡檢查
		if (dto.getId() == null) {
			return ResponseEntity.badRequest().build(); // 或拋出 Exception
		}

		String uid = getCurrentUserUid();
		UserCreditCard updatedCard = userCreditCardService.updateCard(dto, uid);
		return ResponseEntity.ok(updatedCard);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "刪除信用卡", description = "根據 ID 刪除信用卡")
	public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
		String uid = getCurrentUserUid();
		userCreditCardService.deleteCard(id, uid);
		return ResponseEntity.noContent().build();
	}
}