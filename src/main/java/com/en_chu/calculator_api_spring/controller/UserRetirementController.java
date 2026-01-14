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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user/retirement")
public class UserRetirementController {

	@Autowired
	private UserRetirementService userRetirementService;

	/**
	 * 取得使用者的退休生活設定 API: GET /api/v1/user/retirement
	 */
	@GetMapping
	public ResponseEntity<UserRetirement> getRetirement() {
		String uid = SecurityUtils.getCurrentUserUid();
		UserRetirement result = userRetirementService.getByUid(uid);
		// 若無資料回傳 null，前端會收到 200 OK 及空白 body
		return ResponseEntity.ok(result);
	}

	/**
	 * [完整更新] 更新或建立退休生活設定 (Upsert) API: PUT /api/v1/user/retirement 說明: 這是全量更新，DTO
	 * 中未傳的欄位可能會被覆蓋為 null (視 BeanUtils 行為而定)
	 */
	@PutMapping
	public ResponseEntity<String> updateRetirement(@RequestBody @Valid UserRetirementDto req) {
		String uid = SecurityUtils.getCurrentUserUid();
		userRetirementService.updateRetirement(uid, req);
		return ResponseEntity.ok("更新成功");
	}

	/**
	 * [局部更新] 只更新傳入的欄位 API: PATCH /api/v1/user/retirement/gogo 說明:
	 * 專門應對前端卡片式更新，只更新有值的欄位，其餘欄位維持資料庫原值
	 */
	@PatchMapping("/gogo")
	public ResponseEntity<String> patchRetirement(@RequestBody UserRetirementDto req) {
		// 這裡不加 @Valid，允許前端只傳送部分欄位 (例如只傳 housingCost)
		String uid = SecurityUtils.getCurrentUserUid();
		userRetirementService.patchRetirement(uid, req);
		return ResponseEntity.ok("局部更新成功");
	}
}