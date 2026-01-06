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

	private String getCurrentUserUid() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new RuntimeException("使用者未登入");
		}
		return authentication.getName();
	}

	@GetMapping
	@Operation(summary = "取得所有信用卡")
	public ResponseEntity<List<UserCreditCard>> getAllCards() {
		String uid = getCurrentUserUid();
		return ResponseEntity.ok(userCreditCardService.getCards(uid));
	}

	@GetMapping("/{id}")
	@Operation(summary = "取得單張信用卡詳情")
	public ResponseEntity<UserCreditCard> getCard(@PathVariable Long id) {
		String uid = getCurrentUserUid();
		return ResponseEntity.ok(userCreditCardService.getCard(id, uid));
	}

	// --- 修改重點 ---
	@PostMapping
	@Operation(summary = "新增信用卡", description = "無需傳入 Body，後端自動生成預設值")
	public ResponseEntity<UserCreditCard> createCard(
			// 1. 設定 required = false，允許前端不傳 Body
			@RequestBody(required = false) UserCreditCardDto dto) {
		String uid = getCurrentUserUid();

		// 2. 如果前端沒傳 Body，dto 會是 null，我們手動 new 一個空的物件傳給 Service
		// (雖然 Service 目前邏輯是不看 DTO 內容，但為了避免傳 null 進去導致意外，給個空物件較安全)
		if (dto == null) {
			dto = new UserCreditCardDto();
		}

		UserCreditCard newCard = userCreditCardService.createCard(dto, uid);
		return ResponseEntity.status(HttpStatus.CREATED).body(newCard);
	}
	// ----------------

	@PutMapping
	@Operation(summary = "更新信用卡")
	public ResponseEntity<UserCreditCard> updateCard(@Valid @RequestBody UserCreditCardDto dto) {
		if (dto.getId() == null) {
			return ResponseEntity.badRequest().build();
		}
		String uid = getCurrentUserUid();
		return ResponseEntity.ok(userCreditCardService.updateCard(dto, uid));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "刪除信用卡")
	public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
		String uid = getCurrentUserUid();
		userCreditCardService.deleteCard(id, uid);
		return ResponseEntity.noContent().build();
	}
}