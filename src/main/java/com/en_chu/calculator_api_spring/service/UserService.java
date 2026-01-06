package com.en_chu.calculator_api_spring.service;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.en_chu.calculator_api_spring.entity.UserCareer;
import com.en_chu.calculator_api_spring.entity.UserProfile;
import com.en_chu.calculator_api_spring.mapper.UserCareerMapper;
import com.en_chu.calculator_api_spring.mapper.UserProfileMapper; // 改用這個
import com.en_chu.calculator_api_spring.model.UserCareerDto;
import com.en_chu.calculator_api_spring.model.UserFullDataRes;
import com.en_chu.calculator_api_spring.model.UserProfileDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	// 1. 改為注入 UserProfileMapper (取代原本的 UserMapper)
	private final UserProfileMapper userProfileMapper;
	private final UserCareerMapper userCareerMapper;

	// ==========================================
	// 1. 取得完整資料 (Aggregation / 組裝工廠)
	// ==========================================

	/**
	 * 負責整合並讀取使用者的完整資料 策略：分別查詢 Profile (Entity) 與 Career (Entity)，再轉為 DTO 組裝
	 */
	public UserFullDataRes getFullUserData(String uid) {
		log.info("🔍 [UserService] 開始組裝使用者資料: UID={}", uid);

		UserFullDataRes response = new UserFullDataRes();

		// --- Step 1. 取得基本資料 (Profile) ---
		// 注意：Mapper 回傳的是 Entity，需轉為 DTO
		UserProfile profileEntity = userProfileMapper.selectByUid(uid);

		if (profileEntity != null) {
			UserProfileDto profileDto = new UserProfileDto();
			BeanUtils.copyProperties(profileEntity, profileDto);

			response.setProfile(profileDto);
			response.setId(profileEntity.getId()); // 將 Profile ID 設為 Response 的主 ID
			log.info("✅ [UserService] Profile 讀取成功: ID={}", profileEntity.getId());
		} else {
			log.warn("⚠️ [UserService] 查無 Profile 資料 (可能是新用戶或同步延遲)");
		}

		// --- Step 2. 取得職涯資料 (Career) ---
		UserCareer careerEntity = userCareerMapper.selectByUid(uid);

		if (careerEntity != null) {
			UserCareerDto careerDto = new UserCareerDto();
			// 這裡會自動映射 monthlyNetIncome
			BeanUtils.copyProperties(careerEntity, careerDto);

			response.setCareer(careerDto);
			log.info("✅ [UserService] Career 讀取成功 (月實領: {})", careerDto.getMonthlyNetIncome());
		} else {
			log.info("ℹ️ [UserService] 該用戶尚未設定 Career 資料");
		}

		return response;
	}

	// ==========================================
	// 2. 使用者同步 (Login Sync)
	// ==========================================

	/**
	 * 當 Firebase Token 驗證通過後，確保資料庫有該使用者的紀錄
	 */
	@Transactional
	public void syncUser(String uid) {
		// 1. 使用 UserProfileMapper 檢查
		boolean exists = userProfileMapper.checkUserExists(uid);

		if (!exists) {
			log.info("✨ [Sync] 偵測到新用戶，建立初始化檔案: UID={}", uid);

			// 2. 呼叫專門的初始化方法 (只存 UID)
			userProfileMapper.insertInitUser(uid);

		} else {
			log.debug("🔄 [Sync] 舊用戶登入，更新時間戳記: UID={}", uid);

			// 3. 呼叫更新時間方法
			userProfileMapper.updateLastLogin(uid);
		}
	}
}