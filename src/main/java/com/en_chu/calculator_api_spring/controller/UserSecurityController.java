package com.en_chu.calculator_api_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.en_chu.calculator_api_spring.model.UserSecurityUpdateReq;
import com.en_chu.calculator_api_spring.service.UserSecurityService;

@RestController
@RequestMapping("/api/v1/user/securities")
public class UserSecurityController {

	@Autowired
	private UserSecurityService userSecurityService;

	// API 路徑: PUT /api/v1/user/securities/123
	@PutMapping("/{id}")
	public ResponseEntity<?> updateSingleRecord(@PathVariable Long id, @RequestBody UserSecurityUpdateReq req,
			Authentication authentication) {

		// 1. 從 Token 取得當前使用者的 UID (Principal)
		String firebaseUid = (String) authentication.getPrincipal();

		// 2. 呼叫 Service 進行安全更新
		userSecurityService.updateSecurityRecord(firebaseUid, id, req);

		// 3. 回傳成功訊息
		return ResponseEntity.ok().body("Record updated successfully");
	}
}