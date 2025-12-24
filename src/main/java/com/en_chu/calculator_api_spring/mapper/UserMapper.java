package com.en_chu.calculator_api_spring.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.en_chu.calculator_api_spring.model.UserCareerRes;
import com.en_chu.calculator_api_spring.model.UserFullDataRes;
import com.en_chu.calculator_api_spring.model.UserProfileRes;

@Mapper
public interface UserMapper {

	// ==========================================
	// 1. 核心查詢 (JOIN)
	// ==========================================

	/**
	 * 根據 Firebase UID 取得使用者完整資料 (包含 User, Profile, Career)
	 */
	UserFullDataRes selectFullUserDataByFirebaseUid(@Param("firebaseUid") String firebaseUid);

	// ==========================================
	// 2. 輔助查詢 (ID轉換 & 檢查)
	// ==========================================

	/**
	 * 【新增】透過 Firebase UID 轉換成資料庫內部的 User ID (Long) UserService.getInternalUserId
	 * 需要此方法
	 */
	Long selectIdByFirebaseUid(@Param("firebaseUid") String firebaseUid);

	/**
	 * 檢查使用者是否存在
	 */
	boolean checkUserExists(@Param("firebaseUid") String firebaseUid);

	// ==========================================
	// 3. 單獨查詢 (Profile / Career)
	// ==========================================

	/**
	 * 【新增】單獨查詢 Profile 資料 UserService.getProfileOnly 需要此方法
	 */
	UserProfileRes findProfileByUserId(@Param("userId") Long userId);

	/**
	 * 【新增】單獨查詢 Career 資料 UserService.getCareerOnly 需要此方法
	 */
	UserCareerRes findCareerByUserId(@Param("userId") Long userId);

	// ==========================================
	// 4. ID 查找 (用於 Update 前確認資料是否存在)
	// ==========================================

	/**
	 * 透過 User ID 查詢對應的 Profile ID
	 */
	Long findProfileIdByUserId(@Param("userId") Long userId);

	/**
	 * 透過 User ID 查詢對應的 Career ID
	 */
	Long findCareerIdByUserId(@Param("userId") Long userId);

	// ==========================================
	// 5. 寫入操作 (User 基本表)
	// ==========================================

	/**
	 * 註冊新使用者
	 */
	void insertUser(@Param("firebaseUid") String firebaseUid, @Param("email") String email);

	/**
	 * 更新最後登入時間
	 */
	void updateLastLogin(@Param("firebaseUid") String firebaseUid);
}