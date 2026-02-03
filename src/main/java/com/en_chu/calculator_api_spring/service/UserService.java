package com.en_chu.calculator_api_spring.service;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.en_chu.calculator_api_spring.entity.UserCareer;
import com.en_chu.calculator_api_spring.entity.UserLaborInsurance;
import com.en_chu.calculator_api_spring.entity.UserLaborPension;
import com.en_chu.calculator_api_spring.entity.UserProfile;
import com.en_chu.calculator_api_spring.entity.UserRetirement;
import com.en_chu.calculator_api_spring.entity.UserTax;
import com.en_chu.calculator_api_spring.mapper.UserCareerMapper;
import com.en_chu.calculator_api_spring.mapper.UserLaborInsuranceMapper;
import com.en_chu.calculator_api_spring.mapper.UserLaborPensionMapper;
import com.en_chu.calculator_api_spring.mapper.UserProfileMapper;
import com.en_chu.calculator_api_spring.mapper.UserRetirementMapper;
import com.en_chu.calculator_api_spring.mapper.UserTaxMapper;
import com.en_chu.calculator_api_spring.model.UserCareerDto;
import com.en_chu.calculator_api_spring.model.UserFullDataRes;
import com.en_chu.calculator_api_spring.model.UserLaborInsuranceDto;
import com.en_chu.calculator_api_spring.model.UserLaborPensionDto;
import com.en_chu.calculator_api_spring.model.UserProfileDto;
import com.en_chu.calculator_api_spring.model.UserRetirementDto;
import com.en_chu.calculator_api_spring.model.UserTaxDto;

// ✅ [新增] Firebase 相關 Import
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

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

    // ✅ [更換] 改為注入 UserRetirementMapper
    private final UserRetirementMapper userRetirementMapper;

    private final UserTaxMapper userTaxMapper;

    // ==========================================
    // 1. 取得完整資料 (Aggregation / 組裝工廠)
    // ==========================================

    /**
     * 負責整合並讀取使用者的完整資料
     * 策略：分別查詢 Profile, Career, Pension, Insurance, Retirement, Tax，再轉為 DTO 組裝
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

        // --- Step 4. 取得勞工保險資料 (Labor Insurance) ---
        UserLaborInsurance insuranceEntity = userLaborInsuranceMapper.selectByUid(uid);

        if (insuranceEntity != null) {
            UserLaborInsuranceDto insuranceDto = new UserLaborInsuranceDto();
            BeanUtils.copyProperties(insuranceEntity, insuranceDto);

            response.setLaborInsurance(insuranceDto);
            log.info("✅ [UserService] Labor Insurance 讀取成功 (平均薪資: {})", insuranceDto.getAverageMonthlySalary());
        } else {
            log.info("ℹ️ [UserService] 該用戶尚未設定 Labor Insurance 資料");
        }

        // --- Step 5. 取得退休生活型態資料 (Retirement Lifestyle) --- ✅ [已更新]
        UserRetirement retirementEntity = userRetirementMapper.selectByUid(uid);

        if (retirementEntity != null) {
            UserRetirementDto retirementDto = new UserRetirementDto();
            BeanUtils.copyProperties(retirementEntity, retirementDto);

            // ⚠️ 請確保 UserFullDataRes 已新增 setRetirement 方法
            response.setRetirement(retirementDto);
            log.info("✅ [UserService] Retirement Lifestyle 讀取成功 (模式: {})", retirementDto.getHousingMode());
        } else {
            log.info("ℹ️ [UserService] 該用戶尚未設定 Retirement Lifestyle 資料");
        }

        // --- Step 6. 取得稅務資料 (Tax) ---
        UserTax taxEntity = userTaxMapper.selectByUid(uid);

        if (taxEntity != null) {
            UserTaxDto taxDto = new UserTaxDto();
            BeanUtils.copyProperties(taxEntity, taxDto);

            response.setTax(taxDto);
            log.info("✅ [UserService] Tax Data 讀取成功");
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

    // ==========================================
    // 3. 刪除使用者 (Delete User)
    // ==========================================

    /**
     * 刪除指定 UID 的所有關聯資料 (物理刪除)
     * 因為各個 Entity 都是以 UID 為 Foreign Key 或獨立關聯，
     * 需依序刪除各表資料，確保不留殘餘。
     */
    @Transactional
    public void deleteUser(String uid) {
        log.info("🗑️ [UserService] 開始刪除使用者資料: UID={}", uid);

        // --- Part 1: 刪除本地資料庫 (Local DB) ---

        // 1. 刪除稅務資料 (Tax)
        int taxDeleted = userTaxMapper.deleteByUid(uid);
        log.debug("   - Tax 資料已刪除: {} 筆", taxDeleted);

        // 2. 刪除退休生活型態資料 (Retirement)
        int retirementDeleted = userRetirementMapper.deleteByUid(uid);
        log.debug("   - Retirement 資料已刪除: {} 筆", retirementDeleted);

        // 3. 刪除勞保資料 (Insurance)
        int insuranceDeleted = userLaborInsuranceMapper.deleteByUid(uid);
        log.debug("   - Insurance 資料已刪除: {} 筆", insuranceDeleted);

        // 4. 刪除勞退資料 (Pension)
        int pensionDeleted = userLaborPensionMapper.deleteByUid(uid);
        log.debug("   - Pension 資料已刪除: {} 筆", pensionDeleted);

        // 5. 刪除職涯資料 (Career)
        int careerDeleted = userCareerMapper.deleteByUid(uid);
        log.debug("   - Career 資料已刪除: {} 筆", careerDeleted);

        // 6. 最後刪除個人檔案 (Profile)
        int profileDeleted = userProfileMapper.deleteByUid(uid);
        log.debug("   - Profile 資料已刪除: {} 筆", profileDeleted);

        if (profileDeleted > 0) {
            log.info("✅ [UserService] 本地資料庫刪除成功: UID={}", uid);
        } else {
            log.warn("⚠️ [UserService] 嘗試刪除但查無此用戶 Profile: UID={}", uid);
        }

        // --- Part 2: 刪除 Firebase Auth 帳號 ---
        // 注意：這一步驟是呼叫外部 API，若失敗會拋出例外，
        // 因為有 @Transactional，如果 Firebase 刪除失敗，本地資料庫的刪除也會 Rollback (回滾)。
        // 如果您希望「即使 Firebase 刪除失敗，本地也要刪除」，請對這段加上 try-catch。

        try {
            FirebaseAuth.getInstance().deleteUser(uid);
            log.info("🔥 [UserService] Firebase Auth 帳號已刪除: UID={}", uid);
        } catch (FirebaseAuthException e) {
            log.error("❌ [UserService] Firebase Auth 刪除失敗: UID={}, Error={}", uid, e.getMessage());
            // 這裡可以選擇：
            // 1. 拋出 RuntimeException -> 讓整個 Transaction Rollback (前端會收到 500)
            throw new RuntimeException("Firebase 帳號刪除失敗，請稍後再試", e);

            // 2. 或者只記錄 Log，讓本地刪除繼續完成 (視您的業務邏輯而定)
        }
    }
}