package com.en_chu.calculator_api_spring.service;

import com.en_chu.calculator_api_spring.entity.*;
import com.en_chu.calculator_api_spring.mapper.*;
import com.en_chu.calculator_api_spring.model.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserProfileMapper userProfileMapper;
    private final UserCareerMapper userCareerMapper;
    private final UserLaborPensionMapper userLaborPensionMapper;
    private final UserLaborInsuranceMapper userLaborInsuranceMapper;
    private final UserRetirementMapper userRetirementMapper;
    private final UserTaxMapper userTaxMapper;
    private final UserBusinessMapper userBusinessMapper;
    private final UserCreditCardMapper userCreditCardMapper;
    private final UserPortfolioMapper userPortfolioMapper;
    private final UserRealEstateMapper userRealEstateMapper;

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

        // --- Step 5. 取得退休生活型態資料 (Retirement Lifestyle) ---
        UserRetirement retirementEntity = userRetirementMapper.selectByUid(uid);

        if (retirementEntity != null) {
            UserRetirementDto retirementDto = new UserRetirementDto();
            BeanUtils.copyProperties(retirementEntity, retirementDto);

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

    @Transactional
    public void syncUser(String uid) {
        if (!userProfileMapper.checkUserExists(uid)) {
            log.info("✨ [Sync] 偵測到新用戶，建立初始化檔案: UID={}", uid);
            userProfileMapper.insertInitUser(uid);
        } else {
            userProfileMapper.updateLastLogin(uid);
        }
    }

    /**
     * 完整刪除一個使用者及其所有相關資料。
     * 這是一個在應用程式層面執行的連鎖刪除操作。
     *
     * @param uid 要刪除的使用者的 Firebase UID。
     */
    @Transactional // 核心！確保所有刪除操作要麼全部成功，要麼全部失敗回滾。
    public void deleteUser(String uid) {
        log.warn("🗑️ [DELETE] 開始刪除使用者所有資料: UID={}", uid);

        // --- Part 1: 刪除本地資料庫中的所有子表紀錄 ---
        // 必須在刪除主表 (user_profiles) 之前執行，以避免違反外鍵約束。
        userTaxMapper.deleteByUid(uid);
        userRetirementMapper.deleteByUid(uid);
        userLaborInsuranceMapper.deleteByUid(uid);
        userLaborPensionMapper.deleteByUid(uid);
        userCareerMapper.deleteByUid(uid);
        userBusinessMapper.deleteByUid(uid);
        userCreditCardMapper.deleteByUid(uid);
        userPortfolioMapper.deleteByUid(uid);
        userRealEstateMapper.deleteByUid(uid);
        log.info("  - 所有子表紀錄已刪除: UID={}", uid);

        // --- Part 2: 最後刪除主表 (user_profiles) 的紀錄 ---
        int profileDeleted = userProfileMapper.deleteByUid(uid);
        if (profileDeleted > 0) {
            log.info("  - 主表 user_profiles 紀錄已刪除: UID={}", uid);
        } else {
            log.warn("  - 嘗試刪除但查無此用戶 Profile: UID={}", uid);
        }

        // --- Part 3: 刪除 Firebase Authentication 中的帳號 ---
        // 這是一個外部 API 呼叫，同樣被包含在交易中。
        try {
            FirebaseAuth.getInstance().deleteUser(uid);
            log.info("🔥 [DELETE] Firebase Auth 帳號已成功刪除: UID={}", uid);
        } catch (FirebaseAuthException e) {
            log.error("❌ [DELETE] Firebase Auth 帳號刪除失敗: UID={}, Error={}", uid, e.getMessage());
            // 拋出 RuntimeException 來觸發整個交易的回滾。
            // 這能確保如果 Firebase 刪除失敗，我們在本地資料庫所做的所有刪除操作都會被復原，
            // 避免了「Firebase 還有帳號，但本地資料庫已空」的資料不一致狀態。
            throw new RuntimeException("Firebase 帳號刪除失敗，資料庫操作已回滾。", e);
        }
    }
}
