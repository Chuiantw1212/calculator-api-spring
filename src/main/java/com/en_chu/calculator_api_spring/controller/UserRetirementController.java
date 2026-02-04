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
@Tag(name = "User Retirement API", description = "使用者退休生活參數設定")
public class UserRetirementController {

	@Autowired
	private UserRetirementService userRetirementService;

	@Operation(summary = "獲取退休設定")
	@GetMapping
	public ResponseEntity<UserRetirement> getRetirement() {
		String uid = SecurityUtils.getCurrentUserUid();
		UserRetirement result = userRetirementService.getByUid(uid);
		return ResponseEntity.ok(result);
	}

	@Operation(summary = "完整更新退休設定 (Upsert)")
	@PutMapping
	public ResponseEntity<String> updateRetirement(@RequestBody @Valid UserRetirementDto req) {
		String uid = SecurityUtils.getCurrentUserUid();
		userRetirementService.updateRetirement(uid, req);
		return ResponseEntity.ok("更新成功");
	}

	@Operation(summary = "局部更新 - 活躍期 (Go-Go)")
	@PatchMapping("/go-go")
	public ResponseEntity<String> patchGoGo(@RequestBody UserRetirementDto req) {
		String uid = SecurityUtils.getCurrentUserUid();
		userRetirementService.patchRetirement(uid, req);
		return ResponseEntity.ok("Go-Go 設定更新成功");
	}

	@Operation(summary = "局部更新 - 放緩期 (Slow-Go)")
	@PatchMapping("/slow-go")
	public ResponseEntity<String> patchSlowGo(@RequestBody UserRetirementDto req) {
		String uid = SecurityUtils.getCurrentUserUid();
		userRetirementService.patchRetirement(uid, req);
		return ResponseEntity.ok("Slow-Go 設定更新成功");
	}

	@Operation(summary = "局部更新 - 長照期 (No-Go)")
	@PatchMapping("/no-go")
	public ResponseEntity<String> patchNoGo(@RequestBody UserRetirementDto req) {
		String uid = SecurityUtils.getCurrentUserUid();
		userRetirementService.patchRetirement(uid, req);
		return ResponseEntity.ok("No-Go 設定更新成功");
	}
}