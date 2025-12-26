package com.en_chu.calculator_api_spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
// Transactional 在這裡可能只用於 syncUser，讀取通常不需要（除非有 Lazy Loading）
// import org.springframework.transaction.annotation.Transactional; 

import com.en_chu.calculator_api_spring.mapper.UserMapper;
import com.en_chu.calculator_api_spring.model.UserFullDataRes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

	@Autowired
	private UserMapper userMapper;

	// 注意：這裡不再需要注入 UserProfileService 和 UserCareerService 了
	// 除非 syncUser 方法裡面需要用到它們

	// ==========================================
	// 1. 取得完整資料 (Aggregation)
	// ==========================================

	/**
	 * 負責整合並讀取使用者的完整資料 這是 UserService 的核心職責之一：提供一個統一的讀取視圖
	 */
	public UserFullDataRes getFullUserData(String uid) {
		// 直接使用傳入的 uid
		return userMapper.selectFullUserDataByFirebaseUid(uid);
	}

	// ==========================================
	// 2. 使用者同步 (如果是全域邏輯，保留在此)
	// ==========================================

	// public void syncUser(UserSyncReq req) { ... }
}