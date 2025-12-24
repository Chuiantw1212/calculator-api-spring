package com.en_chu.calculator_api_spring.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.en_chu.calculator_api_spring.entity.UserCareer;
import com.en_chu.calculator_api_spring.mapper.UserCareerMapper;
import com.en_chu.calculator_api_spring.model.UserCareerReq;
import com.en_chu.calculator_api_spring.model.UserCareerRes;
import com.en_chu.calculator_api_spring.util.SecurityUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserCareerService {

	@Autowired
	private UserCareerMapper userCareerMapper;

	/**
	 * 更新或建立職涯收入資料 (Upsert) * 策略：先嘗試 Update，如果回傳 0 筆 (代表第一次設定)，則執行 Insert。
	 */
	@Transactional
	public void updateCareer(UserCareerReq req) {
		// 1. 底層自己拿 UID，確保安全
		String uid = SecurityUtils.getCurrentUserUid();

		// 2. 轉換 DTO -> Entity
		UserCareer entity = new UserCareer();
		BeanUtils.copyProperties(req, entity);

		// 3. 設定 UID (這是最重要的 Key)
		entity.setFirebaseUid(uid);

		// 4. 嘗試更新 (Update by UID)
		int rowsAffected = userCareerMapper.updateByUid(entity);

		// 5. 如果更新筆數為 0，代表該用戶還沒建立過 Career 資料 -> 執行新增
		if (rowsAffected == 0) {
			log.info("No existing career found for UID {}, creating new record.", uid);
			userCareerMapper.insert(entity);
		}
	}

	/**
	 * 取得職涯資料
	 */
	public UserCareerRes getCareer() {
		String uid = SecurityUtils.getCurrentUserUid();

		// 使用 UID 直接查詢
		UserCareer entity = userCareerMapper.selectByUid(uid);

		if (entity == null) {
			return null; // 或者回傳 new UserCareerRes() 視前端需求而定
		}

		UserCareerRes res = new UserCareerRes();
		BeanUtils.copyProperties(entity, res);

		return res;
	}
}