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
		// 1. 取得當前登入者的 UID (權限鑰匙)
		String uid = SecurityUtils.getCurrentUserUid();

		// 2. 準備更新資料
		UserProfile entity = new UserProfile();
		BeanUtils.copyProperties(req, entity);

		// 3. 設定雙重鎖條件
		// 前端必須傳來 id，後端補上 firebaseUid
		entity.setId(req.getId());
		entity.setFirebaseUid(uid);

		// 4. 執行更新
		// 如果 id 是 null，或者 id 與資料庫不符，或者 uid 是別人的
		// 這裡的 rowsAffected 就會是 0
		int rowsAffected = userProfileMapper.update(entity);

		// 5. 失敗處理
		// 只要沒更新到任何一筆資料，就視為失敗 (前端傳錯 ID，或有人想攻擊)
		if (rowsAffected == 0) {
			throw new RuntimeException("更新失敗：找不到資料，或您無權修改此 ID。");
		}
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