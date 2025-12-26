package com.en_chu.calculator_api_spring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.en_chu.calculator_api_spring.entity.UserSecurity;
import com.en_chu.calculator_api_spring.model.UserSecurityUpdateReq;
import com.en_chu.calculator_api_spring.service.UserSecurityService;
import com.en_chu.calculator_api_spring.util.SecurityUtils; // 記得 import

@RestController
@RequestMapping("/api/v1/user/securities")
public class UserSecurityController {

	@Autowired
	private UserSecurityService userSecurityService;

	// 0. 取得列表
	@GetMapping
	public ResponseEntity<List<UserSecurity>> getList() {
		// ✅ 這裡呼叫，如果沒登入，直接回傳 401，不會往下走
		String uid = SecurityUtils.getCurrentUserUid();

		return ResponseEntity.ok(userSecurityService.getUserSecurities(uid));
	}

	// 1. 新增 (POST)
	@PostMapping
	public ResponseEntity<UserSecurity> create() {
		// ✅ 乾淨俐落
		String uid = SecurityUtils.getCurrentUserUid();

		UserSecurity newRecord = userSecurityService.createDefaultSecurity(uid);
		return ResponseEntity.ok(newRecord);
	}

	// 2. 刪除 (DELETE)
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		String uid = SecurityUtils.getCurrentUserUid();

		userSecurityService.deleteSecurity(uid, id);
		return ResponseEntity.ok().body("Deleted successfully");
	}

	// 3. 更新 (PUT)
	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UserSecurityUpdateReq req) {

		String uid = SecurityUtils.getCurrentUserUid();

		userSecurityService.updateSecurityRecord(uid, id, req);
		return ResponseEntity.ok().body("Updated successfully");
	}
}