package com.en_chu.calculator_api_spring.service;

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

	/**
	 * 新增或更新個人資料 (Upsert)
	 */
	@Transactional
	public void saveProfile(UserProfileReq req) { // ✅ 參數型別已更新
		// 1. 從 Token 取得絕對可信的 UID (防篡改核心)
		String uid = SecurityUtils.getCurrentUserUid();

		// 2. 查詢 DB 是否已有資料
		UserProfile entity = userProfileMapper.selectByUid(uid);

		// 如果是新使用者，建立新的 Entity 並注入 UID
		if (entity == null) {
			entity = new UserProfile();
			entity.setUid(uid); // 🔑 只有新增時寫入 UID
		}

		// 3. 🏗️ 【組裝階段】 DTO (UserProfileReq) -> Entity (UserProfile)
		// 將前端傳來的資料更新到 Entity 中
		entity.setBirthDate(req.getBirthDate());
		entity.setGender(req.getGender());
		entity.setCurrentAge(req.getCurrentAge());
		entity.setLifeExpectancy(req.getLifeExpectancy());
		entity.setMarriageYear(req.getMarriageYear());
		entity.setCareerInsuranceType(req.getCareerInsuranceType());
		entity.setBiography(req.getBiography());

		// 4. 呼叫 Mapper 存檔
		// 假設 Mapper 有 insert 和 update 方法，或是一個 upsert 方法
		if (entity.getId() == null) {
			userProfileMapper.insert(entity);
		} else {
			userProfileMapper.update(entity);
		}
	}

	/**
	 * 取得個人資料 (回傳 Response DTO)
	 */
	public UserProfileRes getProfile() {
		String uid = SecurityUtils.getCurrentUserUid();
		UserProfile entity = userProfileMapper.selectByUid(uid);

		if (entity == null) {
			return null;
		}

		// Entity -> Response DTO (過濾 UID)
		return UserProfileRes.builder().birthDate(entity.getBirthDate())
				.gender(entity.getGender()).currentAge(entity.getCurrentAge())
				.lifeExpectancy(entity.getLifeExpectancy()).marriageYear(entity.getMarriageYear())
				.careerInsuranceType(entity.getCareerInsuranceType()).biography(entity.getBiography()).build();
	}
}