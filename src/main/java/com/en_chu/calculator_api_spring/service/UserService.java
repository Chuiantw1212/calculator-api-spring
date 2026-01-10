package com.en_chu.calculator_api_spring.service;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.en_chu.calculator_api_spring.entity.UserCareer;
import com.en_chu.calculator_api_spring.entity.UserLaborInsurance;
import com.en_chu.calculator_api_spring.entity.UserLaborPension;
import com.en_chu.calculator_api_spring.entity.UserProfile;
import com.en_chu.calculator_api_spring.entity.UserRetirementExpense;
import com.en_chu.calculator_api_spring.mapper.UserCareerMapper;
import com.en_chu.calculator_api_spring.mapper.UserLaborInsuranceMapper;
import com.en_chu.calculator_api_spring.mapper.UserLaborPensionMapper; // 新增導入
import com.en_chu.calculator_api_spring.mapper.UserProfileMapper;
import com.en_chu.calculator_api_spring.mapper.UserRetirementExpenseMapper;
import com.en_chu.calculator_api_spring.model.UserCareerDto;
import com.en_chu.calculator_api_spring.model.UserFullDataRes;
import com.en_chu.calculator_api_spring.model.UserLaborInsuranceDto;
import com.en_chu.calculator_api_spring.model.UserLaborPensionDto; // 新增導入
import com.en_chu.calculator_api_spring.model.UserProfileDto;
import com.en_chu.calculator_api_spring.model.UserRetirementExpenseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	// 1. 注入所有需要的 Mappers
	private final UserProfileMapper userProfileMapper;
	private final UserCareerMapper userCareerMapper;
	private final UserLaborPensionMapper userLaborPensionMapper;
	private final UserLaborInsuranceMapper userLaborInsuranceMapper;
	private final UserRetirementExpenseMapper userRetirementExpenseMapper;

	// ==========================================
	// 1. 取得完整資料 (Aggregation / 組裝工廠)
	// ==========================================

	/**
	 * 負責整合並讀取使用者的完整資料 策略：分別查詢 Profile, Career, LaborPension, LaborInsurance，再轉為 DTO
	 * 組裝
	 */
	public UserFullDataRes getFullUserData(String uid) {
		log.info("🔍 [UserService] 開始組裝使用者資料: UID={}", uid);

		UserFullDataRes response = new UserFullDataRes();

		// --- Step 1. 取得基本資料 (Profile) ---
		UserProfile profileEntity = userProfileMapper.selectByUid(uid);

		if (profileEntity != null) {
			UserProfileDto profileDto = new UserProfileDto();
			BeanUtils.copyProperties(profileEntity, profileDto);

			response.setProfile(profileDto);
			response.setId(profileEntity.getId());
			log.info("✅ [UserService] Profile 讀取成功: ID={}", profileEntity.getId());
		} else {
			log.warn("⚠️ [UserService] 查無 Profile 資料 (可能是新用戶或同步延遲)");
		}

		// --- Step 2. 取得職涯資料 (Career) ---
		UserCareer careerEntity = userCareerMapper.selectByUid(uid);

		if (careerEntity != null) {
			UserCareerDto careerDto = new UserCareerDto();
			BeanUtils.copyProperties(careerEntity, careerDto);

			response.setCareer(careerDto);
			log.info("✅ [UserService] Career 讀取成功 (月實領: {})", careerDto.getMonthlyNetIncome());
		} else {
			log.info("ℹ️ [UserService] 該用戶尚未設定 Career 資料");
		}

		// --- Step 3. 取得勞工退休金資料 (Labor Pension) ---
		UserLaborPension pensionEntity = userLaborPensionMapper.selectByUid(uid);

		if (pensionEntity != null) {
			UserLaborPensionDto pensionDto = new UserLaborPensionDto();
			BeanUtils.copyProperties(pensionEntity, pensionDto);

			response.setLaborPension(pensionDto);
			log.info("✅ [UserService] Labor Pension 讀取成功 (預退年齡: {})", pensionDto.getExpectedRetirementAge());
		} else {
			log.info("ℹ️ [UserService] 該用戶尚未設定 Labor Pension 資料");
		}

		// --- Step 4. 取得勞工保險資料 (Labor Insurance) --- ✅ 新增區塊
		UserLaborInsurance insuranceEntity = userLaborInsuranceMapper.selectByUid(uid);

		if (insuranceEntity != null) {
			UserLaborInsuranceDto insuranceDto = new UserLaborInsuranceDto();
			BeanUtils.copyProperties(insuranceEntity, insuranceDto);

			response.setLaborInsurance(insuranceDto);
			log.info("✅ [UserService] Labor Insurance 讀取成功 (平均薪資: {})", insuranceDto.getAverageMonthlySalary());
		} else {
			log.info("ℹ️ [UserService] 該用戶尚未設定 Labor Insurance 資料");
		}
		
		// --- Step 5. 取得退休開支資料 (Retirement Expense) ---
        UserRetirementExpense expenseEntity = userRetirementExpenseMapper.selectByUid(uid);
        
        if (expenseEntity != null) {
            UserRetirementExpenseDto expenseDto = new UserRetirementExpenseDto();
            BeanUtils.copyProperties(expenseEntity, expenseDto);
            
            response.setRetirementExpense(expenseDto); // Set 到新的欄位
            log.info("✅ [UserService] Retirement Expense 讀取成功");
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
		boolean exists = userProfileMapper.checkUserExists(uid);

		if (!exists) {
			log.info("✨ [Sync] 偵測到新用戶，建立初始化檔案: UID={}", uid);
			userProfileMapper.insertInitUser(uid);
		} else {
			log.debug("🔄 [Sync] 舊用戶登入，更新時間戳記: UID={}", uid);
			userProfileMapper.updateLastLogin(uid);
		}
	}
}