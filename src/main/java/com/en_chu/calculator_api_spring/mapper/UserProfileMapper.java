package com.en_chu.calculator_api_spring.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.en_chu.calculator_api_spring.entity.UserProfile;

@Mapper
public interface UserProfileMapper {

	// ==========================================
	// 1. 基本 CRUD (給 UserProfileService 使用)
	// ==========================================

	/**
	 * 根據 Firebase UID 查詢使用者基本資料
	 */
	UserProfile selectByUid(@Param("firebaseUid") String firebaseUid);

	/**
	 * 新增完整使用者資料 (通常用於測試或後台建立)
	 */
	int insert(UserProfile record);

	/**
	 * 更新使用者基本資料 (不含 ID 與 CreatedAt)
	 */
	int updateByUid(UserProfile record);

	/**
	 * 根據 Firebase UID 刪除使用者
	 */
	int deleteByUid(@Param("firebaseUid") String firebaseUid);

	// ==========================================
	// 2. 登入同步專用 (給 UserService.syncUser 使用)
	// ==========================================

	/**
	 * 檢查使用者是否存在 (回傳 true/false) 用於判斷是「新用戶註冊」還是「舊用戶登入」
	 */
	boolean checkUserExists(@Param("firebaseUid") String firebaseUid);

	/**
	 * 舊用戶登入：只更新最後登入時間 (updated_at)
	 */
	int updateLastLogin(@Param("firebaseUid") String firebaseUid);

	/**
	 * 新用戶註冊：初始化一筆僅含 UID 的資料 XML 對應：INSERT INTO ... VALUES (#{firebaseUid}, ...)
	 */
	int insertInitUser(@Param("firebaseUid") String firebaseUid);

}