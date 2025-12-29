package com.en_chu.calculator_api_spring.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.en_chu.calculator_api_spring.entity.UserProfile;
import com.en_chu.calculator_api_spring.mapper.UserProfileMapper;
import com.en_chu.calculator_api_spring.model.UserProfileDto;

@Service
public class UserProfileService {

	@Autowired
	private UserProfileMapper userProfileMapper;

	/**
	 * 新增個人資料 修改：增加 String uid 參數
	 */
	@Transactional
	public void createProfile(String uid, UserProfileDto req) {
		// 1. 檢查是否已存在 (使用傳入的 UID 查)
		if (userProfileMapper.selectByUid(uid) != null) {
			throw new RuntimeException("資料已存在，請使用更新功能");
		}

		UserProfile entity = new UserProfile();
		BeanUtils.copyProperties(req, entity);

		// 關鍵：設定傳入的 UID
		entity.setFirebaseUid(uid);

		// 2. 呼叫 Insert
		userProfileMapper.insert(entity);
	}

	/**
	 * 更新個人資料 修改：增加 String uid 參數
	 */
	@Transactional
	public void updateProfile(String uid, UserProfileDto req) {
		UserProfile entity = new UserProfile();
		BeanUtils.copyProperties(req, entity);

		// 【關鍵修改】
		// 使用 Controller -> UserService 傳遞下來的 uid
		entity.setFirebaseUid(uid);

		// 3. 呼叫 Update (Mapper XML 必須寫 WHERE firebase_uid = #{firebaseUid})
		int rowsAffected = userProfileMapper.updateByUid(entity);

		if (rowsAffected == 0) {
			// 通常代表該用戶還沒有建立 Profile
			throw new RuntimeException("更新失敗：找不到您的個人檔案，請先執行新增 (Create)");
		}
	}

	/**
	 * 查詢個人資料 修改：增加 String uid 參數
	 */
	public UserProfileDto getProfile(String uid) {
		// 直接用傳入的 UID 查
		UserProfile entity = userProfileMapper.selectByUid(uid);

		if (entity == null) {
			return null;
		}

		UserProfileDto res = new UserProfileDto();
		BeanUtils.copyProperties(entity, res);

		return res;
	}
}