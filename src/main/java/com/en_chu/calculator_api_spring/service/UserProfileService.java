package com.en_chu.calculator_api_spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.en_chu.calculator_api_spring.entity.UserProfile;
import com.en_chu.calculator_api_spring.mapper.UserProfileMapper;
import com.en_chu.calculator_api_spring.model.PersonalProfileReq;

@Service
public class UserProfileService {

	@Autowired
	private UserProfileMapper userProfileMapper;

	/**
	 * 將 DTO 轉換為 Entity 並存檔 (Upsert)
	 */
	@Transactional
	public void saveProfile(PersonalProfileReq req) {
		// 1. 建立 Entity (準備進倉庫的貨物)
		UserProfile entity = new UserProfile();

		// 2. 手動搬運資料 (Mapping)
		// 這裡可以做資料清洗，例如把 marriageYear 的字串轉數字
		entity.setId(req.getId());
		entity.setBirthYear(req.getBirthYear());
		entity.setBirthDate(req.getBirthDate());
		entity.setGender(req.getGender());
		entity.setCurrentAge(req.getCurrentAge());
		entity.setLifeExpectancy(req.getLifeExpectancy());

		// 特殊處理：前端傳 String，DB 存 Integer
		if (req.getMarriageYear() != null && !req.getMarriageYear().isBlank()) {
			try {
				entity.setMarriageYear(Integer.parseInt(req.getMarriageYear()));
			} catch (NumberFormatException e) {
				// 處理轉換失敗，或是 log warning
				entity.setMarriageYear(null);
			}
		}

		entity.setCareerInsuranceType(req.getCareerInsuranceType());
		entity.setBiography(req.getBiography());

		// 3. 呼叫 MyBatis 儲存
		userProfileMapper.upsertProfile(entity);
	}
}