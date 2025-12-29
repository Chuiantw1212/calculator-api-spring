package com.en_chu.calculator_api_spring.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.en_chu.calculator_api_spring.entity.UserCareer;
import com.en_chu.calculator_api_spring.mapper.UserCareerMapper;
import com.en_chu.calculator_api_spring.model.UserCareerDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserCareerService {

	@Autowired
	private UserCareerMapper userCareerMapper;

	/**
	 * 更新或建立職涯收入資料 (Upsert) 修改：增加 String uid 參數
	 */
	@Transactional
	public void updateCareer(String uid, UserCareerDto req) {
		// 1. 直接使用傳入的 UID
		// String uid = SecurityUtils.getCurrentUserUid(); // ❌ 舊做法

		// 2. 轉換 DTO -> Entity
		UserCareer entity = new UserCareer();
		BeanUtils.copyProperties(req, entity);

		// 3. 設定 UID (這是最重要的 Key)
		entity.setFirebaseUid(uid);

		// 4. 嘗試更新 (Update by UID)
		// Mapper 會根據 firebase_uid 去找資料
		int rowsAffected = userCareerMapper.updateByUid(entity);

		// 5. 如果更新筆數為 0，代表該用戶還沒建立過 Career 資料 -> 執行新增
		if (rowsAffected == 0) {
			log.info("No existing career found for UID {}, creating new record.", uid);
			userCareerMapper.insert(entity);
		}
	}

	/**
	 * 取得職涯資料 修改：增加 String uid 參數
	 */
	public UserCareerDto getCareer(String uid) {
		// 使用傳入的 UID 直接查詢
		UserCareer entity = userCareerMapper.selectByUid(uid);

		if (entity == null) {
			return null;
		}

		UserCareerDto res = new UserCareerDto();
		BeanUtils.copyProperties(entity, res);

		return res;
	}
}