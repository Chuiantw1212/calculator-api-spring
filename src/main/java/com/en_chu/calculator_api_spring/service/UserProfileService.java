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
	private UserProfileMapper userMapper;

	@Transactional
	public void createProfile(UserProfileReq req) {
		String uid = SecurityUtils.getCurrentUserUid();

		// 1. (選用) 檢查是否已經有資料，避免重複建立
		if (userMapper.selectByUid(uid) != null) {
			throw new RuntimeException("資料已存在，請使用更新功能");
		}

		UserProfile entity = new UserProfile();
		BeanUtils.copyProperties(req, entity);
		entity.setFirebaseUid(uid);

		// 2. 呼叫 Insert
		userMapper.insert(entity);
	}

	@Transactional
	public void updateProfile(UserProfileReq req) {
		String uid = SecurityUtils.getCurrentUserUid();

		UserProfile entity = new UserProfile();
		BeanUtils.copyProperties(req, entity);

		// 設定雙重鎖條件
		entity.setId(req.getId());
		entity.setFirebaseUid(uid);

		// 3. 呼叫 Update
		int rowsAffected = userMapper.update(entity);

		if (rowsAffected == 0) {
			throw new RuntimeException("更新失敗：找不到資料，或您無權修改此 ID。");
		}
	}

	public UserProfileRes getProfile() {
		String uid = SecurityUtils.getCurrentUserUid();
		UserProfile entity = userMapper.selectByUid(uid);

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