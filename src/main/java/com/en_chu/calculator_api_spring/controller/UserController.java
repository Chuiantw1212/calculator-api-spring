package com.en_chu.calculator_api_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.en_chu.calculator_api_spring.model.UserCareerDto;
import com.en_chu.calculator_api_spring.model.UserFullDataRes;
import com.en_chu.calculator_api_spring.model.UserLaborInsuranceDto;
import com.en_chu.calculator_api_spring.model.UserLaborPensionDto; // 1. 新增 Import
import com.en_chu.calculator_api_spring.model.UserProfileDto;
import com.en_chu.calculator_api_spring.model.UserRetirementExpenseDto;
import com.en_chu.calculator_api_spring.service.UserCareerService;
import com.en_chu.calculator_api_spring.service.UserLaborInsuranceService;
import com.en_chu.calculator_api_spring.service.UserLaborPensionService; // 2. 新增 Import
import com.en_chu.calculator_api_spring.service.UserProfileService;
import com.en_chu.calculator_api_spring.service.UserRetirementExpenseService;
import com.en_chu.calculator_api_spring.service.UserService;
import com.en_chu.calculator_api_spring.util.SecurityUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	private UserCareerService userCareerService;

	@Autowired
	private UserLaborPensionService userLaborPensionService;

	@Autowired
	private UserLaborInsuranceService userLaborInsuranceService;

	@Autowired
	private UserRetirementExpenseService userRetirementExpenseService;

	@GetMapping("/me")
	public ResponseEntity<UserFullDataRes> getMe() {
		String uid = SecurityUtils.getCurrentUserUid();
		return ResponseEntity.ok(userService.getFullUserData(uid));
	}

	@PutMapping("/profile")
	public ResponseEntity<String> updateProfile(@RequestBody @Valid UserProfileDto req) {
		String uid = SecurityUtils.getCurrentUserUid();
		userProfileService.updateProfile(uid, req);
		return ResponseEntity.ok("更新成功");
	}

	@PutMapping("/career")
	public ResponseEntity<String> updateCareer(@RequestBody @Valid UserCareerDto req) {
		String uid = SecurityUtils.getCurrentUserUid();
		userCareerService.updateCareer(uid, req);
		return ResponseEntity.ok("更新成功");
	}

	@PutMapping("/labor-pension")
	public ResponseEntity<String> updateLaborPension(@RequestBody @Valid UserLaborPensionDto req) {
		String uid = SecurityUtils.getCurrentUserUid();
		// 直接調用專門的 Service
		userLaborPensionService.updateLaborPension(uid, req);
		return ResponseEntity.ok("更新成功");
	}

	@PutMapping("/labor-insurance")
	public ResponseEntity<UserLaborInsuranceDto> update(@Valid @RequestBody UserLaborInsuranceDto req) {
		String uid = SecurityUtils.getCurrentUserUid();
		return ResponseEntity.ok(userLaborInsuranceService.updateLaborInsurance(uid, req));
	}

	@PutMapping("/retirement-expense")
    public ResponseEntity<UserRetirementExpenseDto> updateRetirementExpense(@RequestBody @Valid UserRetirementExpenseDto req) {
        String uid = SecurityUtils.getCurrentUserUid();
        return ResponseEntity.ok(userRetirementExpenseService.updateRetirementExpense(uid, req));
    }
}