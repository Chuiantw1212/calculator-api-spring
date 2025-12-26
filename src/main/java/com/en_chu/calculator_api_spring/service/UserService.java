package com.en_chu.calculator_api_spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.en_chu.calculator_api_spring.mapper.UserMapper;
import com.en_chu.calculator_api_spring.model.UserCareerReq;
import com.en_chu.calculator_api_spring.model.UserFullDataRes;
import com.en_chu.calculator_api_spring.model.UserProfileReq;
// SecurityUtils 不需要在這裡用了，因為 uid 是從參數傳進來的

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	private UserCareerService userCareerService;

	// ==========================================
	// 1. 取得完整資料
	// ==========================================

	// 修改：增加 String uid 參數，配合 Controller
	public UserFullDataRes getFullUserData(String uid) {
		// 直接使用傳入的 uid，不需要再呼叫 SecurityUtils.getCurrentUserUid()
		return userMapper.selectFullUserDataByFirebaseUid(uid);
	}

	// ==========================================
	// 2. Profile 更新
	// ==========================================

	/**
	 * 更新 Profile 修改：增加 String uid 參數，並將其傳遞給底層 Service
	 */
	@Transactional
	public void updateProfile(String uid, UserProfileReq req) {
		// 將 Controller 傳來的 uid 繼續往下傳
		userProfileService.updateProfile(uid, req);
	}

	// ==========================================
	// 3. Career 更新
	// ==========================================

	/**
	 * 更新 Career 修改：增加 String uid 參數，並將其傳遞給底層 Service
	 */
	@Transactional
	public void updateCareer(String uid, UserCareerReq req) {
		// 將 Controller 傳來的 uid 繼續往下傳
		userCareerService.updateCareer(uid, req);
	}

	// ... 其他方法 (如 syncUser) ...
}