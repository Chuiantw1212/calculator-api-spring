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
@RequestMapping("/api/v1/user/credit-cards")
@Tag(name = "User Credit Card API", description = "使用者信用卡管理")
@RequiredArgsConstructor
public class UserCreditCardController {

	private final UserCreditCardService userCreditCardService;

	@Operation(summary = "獲取所有信用卡")
	@GetMapping
	public ResponseEntity<List<UserCreditCard>> getList() {
		String uid = SecurityUtils.getCurrentUserUid();
		return ResponseEntity.ok(userCreditCardService.getCards(uid));
	}

	@Operation(summary = "獲取單張信用卡")
	@GetMapping("/{id}")
	public ResponseEntity<UserCreditCard> getOne(@PathVariable Long id) {
		String uid = SecurityUtils.getCurrentUserUid();
		return ResponseEntity.ok(userCreditCardService.getCard(id, uid));
	}

	@Operation(summary = "新增信用卡")
	@PostMapping
	public ResponseEntity<UserCreditCard> create(@RequestBody(required = false) UserCreditCardDto req) {
		String uid = SecurityUtils.getCurrentUserUid();
		if (req == null) {
			req = new UserCreditCardDto();
		}
		return ResponseEntity.ok(userCreditCardService.createCard(req, uid));
	}

	@Operation(summary = "更新信用卡")
	@PutMapping("/{id}")
	public ResponseEntity<UserCreditCard> update(@PathVariable Long id, @RequestBody @Valid UserCreditCardDto req) {
		String uid = SecurityUtils.getCurrentUserUid();
		req.setId(id);
		return ResponseEntity.ok(userCreditCardService.updateCard(req, uid));
	}

	@Operation(summary = "刪除信用卡")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		String uid = SecurityUtils.getCurrentUserUid();
		userCreditCardService.deleteCard(id, uid);
		return ResponseEntity.noContent().build();
	}
}