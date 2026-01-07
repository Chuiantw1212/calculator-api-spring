package com.en_chu.calculator_api_spring.service;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.en_chu.calculator_api_spring.entity.UserLaborPension;
import com.en_chu.calculator_api_spring.mapper.UserLaborPensionMapper;
import com.en_chu.calculator_api_spring.model.UserLaborPensionDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserLaborPensionService {

	private final UserLaborPensionMapper userLaborPensionMapper;

	/**
	 * 取得勞工退休金設定
	 * 
	 * @param uid Firebase UID
	 * @return DTO or null
	 */
	public UserLaborPensionDto getLaborPension(String uid) {
		// 1. 查詢 DB
		UserLaborPension entity = userLaborPensionMapper.selectByUid(uid);

		if (entity == null) {
			log.info("ℹ️ [LaborPension] 查無資料: UID={}", uid);
			return null; // Controller 可決定回傳 404 或空物件
		}

		// 2. 轉為 DTO
		UserLaborPensionDto res = new UserLaborPensionDto();
		BeanUtils.copyProperties(entity, res);

		return res;
	}

	/**
	 * 更新或建立勞工退休金設定 (Upsert)
	 * 
	 * @param uid Firebase UID (來自 Token)
	 * @param req 前端傳入的 DTO
	 * @return 更新後的完整資料
	 */
	@Transactional
	public UserLaborPensionDto updateLaborPension(String uid, UserLaborPensionDto req) {
		log.info("🔄 [LaborPension] 準備更新資料: UID={}", uid);

		// 1. DTO -> Entity
		UserLaborPension entity = new UserLaborPension();
		
		// --- 手動防呆：如果前端沒傳，就塞預設值 ---
	    if (entity.getExpectedRetirementAge() == null) {
	        entity.setExpectedRetirementAge(65);
	    }
	    
		BeanUtils.copyProperties(req, entity);

		// 2. 強制綁定 UID (核心安全檢查)
		entity.setFirebaseUid(uid);

		// 3. 嘗試更新 (Update)
		int rowsAffected = userLaborPensionMapper.updateByUid(entity);

		// 4. 若無更新筆數，代表該用戶尚未建立資料 -> 執行新增 (Insert)
		if (rowsAffected == 0) {
			log.info("✨ [LaborPension] 新用戶，建立新紀錄: UID={}", uid);
			userLaborPensionMapper.insert(entity);
		}

		// 5. 回傳最新的資料 (通常建議重新查詢一次，或直接回傳 req)
		// 這裡為了效能直接回傳 req，但補上 id (如果有需要的話)
		// 若需最嚴謹的 DB 狀態，可呼叫 return getLaborPension(uid);
		return getLaborPension(uid);
	}
}