package com.en_chu.calculator_api_spring.service;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.en_chu.calculator_api_spring.entity.UserRetirementExpense;
import com.en_chu.calculator_api_spring.mapper.UserRetirementExpenseMapper;
import com.en_chu.calculator_api_spring.model.UserRetirementExpenseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRetirementExpenseService {

	private final UserRetirementExpenseMapper mapper;

	/**
	 * 讀取退休開支規劃
	 */
	public UserRetirementExpenseDto getRetirementExpense(String uid) {
		UserRetirementExpense entity = mapper.selectByUid(uid);

		if (entity == null) {
			return null; // 若無資料，回傳 null (Controller 層可決定回傳 204 或空物件)
		}

		UserRetirementExpenseDto dto = new UserRetirementExpenseDto();
		BeanUtils.copyProperties(entity, dto);
		return dto;
	}

	/**
	 * 更新或建立退休開支規劃 (Upsert)
	 */
	@Transactional
	public UserRetirementExpenseDto updateRetirementExpense(String uid, UserRetirementExpenseDto req) {
		log.info("🔄 [RetirementExpense] 更新資料: UID={}", uid);

		UserRetirementExpense entity = new UserRetirementExpense();
		BeanUtils.copyProperties(req, entity);
		entity.setFirebaseUid(uid); // 確保 UID 正確

		// 1. 嘗試更新
		int rows = mapper.updateByUid(entity);

		// 2. 若更新筆數為 0，代表該用戶尚未建立資料，改為新增
		if (rows == 0) {
			log.info("✨ [RetirementExpense] 建立新資料: UID={}", uid);
			mapper.insert(entity);
		}

		// 3. 回傳最新資料 (確保前端拿到 DB 寫入後的狀態，如 created_at)
		return getRetirementExpense(uid);
	}
}