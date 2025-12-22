package com.en_chu.calculator_api_spring.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.en_chu.calculator_api_spring.entity.UserProfile;
import com.en_chu.calculator_api_spring.mapper.UserProfileMapper;
import com.en_chu.calculator_api_spring.model.UserProfileReq; // ✅ 更新 import
import com.en_chu.calculator_api_spring.model.UserProfileRes;
import com.en_chu.calculator_api_spring.util.SecurityUtils;

@Service
public class UserProfileService {

	@Autowired
	private UserProfileMapper userProfileMapper;

	@Transactional
	public void saveProfile(UserProfileReq req) {
		// 1. 取得 UID
		String uid = SecurityUtils.getCurrentUserUid();

		// 2. 準備要更新的資料物件
		UserProfile entity = new UserProfile();

		// 3. 複製前端傳來的屬性 (Req -> Entity)
		BeanUtils.copyProperties(req, entity);

		// 4. 設定 FIREBASE UID (作為更新條件)
		entity.setFirebaseUid(uid);

		// 5. 呼叫 Mapper 更新，並檢查回傳的「影響筆數」
		// updateProfile 回傳 int，代表資料庫改了幾筆
		int rowsAffected = userProfileMapper.update(entity);

		// 6. 判斷結果
		if (rowsAffected == 0) {
			// 回傳 0 代表資料庫裡找不到這個 user_id
			throw new RuntimeException("更新失敗：找不到使用者資料 (UID: " + uid + ")");
		}

		// 如果是 1，代表成功，甚麼都不用做
	}

	public UserProfileRes getProfile() {
		String uid = SecurityUtils.getCurrentUserUid();
		UserProfile entity = userProfileMapper.selectByUid(uid);

		if (entity == null) {
			return null;
		}

		// 1. 先 new 一個空的 DTO (就像 JS 的 const res = {})
		UserProfileRes res = new UserProfileRes();

		// 2. ✨ 魔法時刻：類似 Object.assign(res, entity)
		// entity 是來源 (Source)，res 是目標 (Target)
		BeanUtils.copyProperties(entity, res);

		// 3. 搞定回傳
		return res;
	}
}