package com.en_chu.calculator_api_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.en_chu.calculator_api_spring.model.UserCareerReq;
import com.en_chu.calculator_api_spring.model.UserFullDataRes;
import com.en_chu.calculator_api_spring.model.UserProfileReq;
import com.en_chu.calculator_api_spring.service.UserCareerService; // Import 這裡
import com.en_chu.calculator_api_spring.service.UserProfileService; // Import 這裡
import com.en_chu.calculator_api_spring.service.UserService;
import com.en_chu.calculator_api_spring.util.SecurityUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

	@Autowired
	private UserService userService; // 負責讀取

	@Autowired
	private UserProfileService userProfileService; // 負責 Profile 寫入

	@Autowired
	private UserCareerService userCareerService; // 負責 Career 寫入

	@GetMapping("/me")
	public ResponseEntity<UserFullDataRes> getMe() {
		String uid = SecurityUtils.getCurrentUserUid();
		return ResponseEntity.ok(userService.getFullUserData(uid));
	}

	@PutMapping("/profile")
	public ResponseEntity<String> updateProfile(@RequestBody @Valid UserProfileReq req) {
		String uid = SecurityUtils.getCurrentUserUid();
		// 直接調用專門的 Service
		userProfileService.updateProfile(uid, req);
		return ResponseEntity.ok("更新成功");
	}

	@PutMapping("/career")
	public ResponseEntity<String> updateCareer(@RequestBody @Valid UserCareerReq req) {
		String uid = SecurityUtils.getCurrentUserUid();
		// 直接調用專門的 Service
		userCareerService.updateCareer(uid, req);
		return ResponseEntity.ok("更新成功");
	}
}