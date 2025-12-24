package com.en_chu.calculator_api_spring.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.en_chu.calculator_api_spring.entity.UserProfile;
import com.en_chu.calculator_api_spring.mapper.UserProfileMapper;
import com.en_chu.calculator_api_spring.model.UserProfileReq;
import com.en_chu.calculator_api_spring.model.UserProfileRes;
import com.en_chu.calculator_api_spring.util.SecurityUtils;

@Service
public class UserProfileService {

	@Autowired
	// 建議變數名稱改為 userProfileMapper 比較不會跟 UserMapper 搞混
	private UserProfileMapper userProfileMapper;

	/**
	 * 新增個人資料
	 */
	@Transactional
	public void createProfile(UserProfileReq req) {
		String uid = SecurityUtils.getCurrentUserUid();

		// 1. 檢查是否已存在 (使用 UID 查)
		// 雖然 DB 有 unique constraint，但先查一次可以回傳比較友善的錯誤訊息
		if (userProfileMapper.selectByUid(uid) != null) {
			throw new RuntimeException("資料已存在，請使用更新功能");
		}

		UserProfile entity = new UserProfile();
		BeanUtils.copyProperties(req, entity);

		// 關鍵：設定 UID，Mapper XML 會利用這個 UID 去 users 表查 id 來填 FK
		entity.setFirebaseUid(uid);

		// 2. 呼叫 Insert
		userProfileMapper.insert(entity);
	}

	/**
	 * 更新個人資料
	 */
	@Transactional
	public void updateProfile(UserProfileReq req) {
		String uid = SecurityUtils.getCurrentUserUid();

		UserProfile entity = new UserProfile();
		BeanUtils.copyProperties(req, entity);

		// 【關鍵修改】
		// 我們 "不" 設定 entity.setId(req.getId())。
		// 因為更新的 WHERE 條件將直接使用 firebase_uid。
		// 這樣就算前端傳了別人的 ID，也絕對不會生效。
		entity.setFirebaseUid(uid);

		// 3. 呼叫 Update (Mapper XML 必須寫 WHERE firebase_uid = #{firebaseUid})
		int rowsAffected = userProfileMapper.updateByUid(entity);

		if (rowsAffected == 0) {
			// 通常代表該用戶還沒有建立 Profile
			throw new RuntimeException("更新失敗：找不到您的個人檔案，請先執行新增 (Create)");
		}
	}

	/**
	 * 查詢個人資料
	 */
	public UserProfileRes getProfile() {
		String uid = SecurityUtils.getCurrentUserUid();

		// 直接用 UID 查，不經過 users 表
		UserProfile entity = userProfileMapper.selectByUid(uid);

		if (entity == null) {
			return null;
		}

		UserProfileRes res = new UserProfileRes();
		BeanUtils.copyProperties(entity, res);

		return res;
	}
}