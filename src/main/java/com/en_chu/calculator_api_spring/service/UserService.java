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
    // Mappers for 1:N relationships are kept for the deleteUser method
    private final UserBusinessMapper userBusinessMapper;
    private final UserCreditCardMapper userCreditCardMapper;
    private final UserPortfolioMapper userPortfolioMapper;
    private final UserRealEstateMapper userRealEstateMapper;

    /**
     * Assembles the core, 1-to-1 data for the current user.
     * 1-to-N list data (like portfolios, businesses) should be fetched via their own dedicated API endpoints.
     * @param uid The Firebase UID of the user.
     * @return A UserFullDataRes object containing only the 1-to-1 related data.
     */
    public UserFullDataRes getFullUserData(String uid) {
        log.info("🔍 [UserService] Assembling core user data for UID: {}", uid);
        UserFullDataRes response = new UserFullDataRes();

        // --- Step 1. Get Profile (1:1) ---
        UserProfile profileEntity = userProfileMapper.selectByUid(uid);
        if (profileEntity != null) {
            UserProfileDto profileDto = new UserProfileDto();
            BeanUtils.copyProperties(profileEntity, profileDto);
            response.setProfile(profileDto);
            response.setId(profileEntity.getId());
        }

        // --- Step 2. Get Career (1:1) ---
        UserCareer careerEntity = userCareerMapper.selectByUid(uid);
        if (careerEntity != null) {
            UserCareerDto careerDto = new UserCareerDto();
            BeanUtils.copyProperties(careerEntity, careerDto);
            response.setCareer(careerDto);
        }

        // --- Step 3. Get Labor Pension (1:1) ---
        UserLaborPension pensionEntity = userLaborPensionMapper.selectByUid(uid);
        if (pensionEntity != null) {
            UserLaborPensionDto pensionDto = new UserLaborPensionDto();
            BeanUtils.copyProperties(pensionEntity, pensionDto);
            response.setLaborPension(pensionDto);
        }

        // --- Step 4. Get Labor Insurance (1:1) ---
        UserLaborInsurance insuranceEntity = userLaborInsuranceMapper.selectByUid(uid);
        if (insuranceEntity != null) {
            UserLaborInsuranceDto insuranceDto = new UserLaborInsuranceDto();
            BeanUtils.copyProperties(insuranceEntity, insuranceDto);
            response.setLaborInsurance(insuranceDto);
        }
        
        // --- Step 5. Get Retirement (1:1) ---
        UserRetirement retirementEntity = userRetirementMapper.selectByUid(uid);
        if (retirementEntity != null) {
            UserRetirementDto retirementDto = new UserRetirementDto();
            BeanUtils.copyProperties(retirementEntity, retirementDto);
            response.setRetirement(retirementDto);
        }

        // --- Step 6. Get Tax (1:1) ---
        UserTax taxEntity = userTaxMapper.selectByUid(uid);
        if (taxEntity != null) {
            UserTaxDto taxDto = new UserTaxDto();
            BeanUtils.copyProperties(taxEntity, taxDto);
            response.setTax(taxDto);
        }

        log.info("✅ [UserService] Core user data assembled successfully for UID: {}", uid);
        return response;
    }

    @Transactional
    public void syncUser(String uid) {
        if (!userProfileMapper.checkUserExists(uid)) {
            log.info("✨ [Sync] New user detected. Creating initial profile for UID: {}", uid);
            UserProfile newProfile = new UserProfile();
            newProfile.setFirebaseUid(uid);
            userProfileMapper.insertInitUser(newProfile);
        } else {
            userProfileMapper.updateLastLogin(uid);
        }
    }

    @Transactional
    public void deleteUser(String uid) {
        log.warn("🗑️ [DELETE] Starting deletion of all data for user: UID={}", uid);
        userTaxMapper.deleteByUid(uid);
        userRetirementMapper.deleteByUid(uid);
        userLaborInsuranceMapper.deleteByUid(uid);
        userLaborPensionMapper.deleteByUid(uid);
        userCareerMapper.deleteByUid(uid);
        userBusinessMapper.deleteByUid(uid);
        userCreditCardMapper.deleteByUid(uid);
        userPortfolioMapper.deleteByUid(uid);
        userRealEstateMapper.deleteByUid(uid);
        log.info("  - All child table records have been deleted for UID: {}", uid);

        int profileDeleted = userProfileMapper.deleteByUid(uid);
        if (profileDeleted > 0) {
            log.info("  - Main table user_profiles record has been deleted for UID: {}", uid);
        } else {
            log.warn("  - Attempted to delete but no profile found for UID: {}", uid);
        }

        try {
            FirebaseAuth.getInstance().deleteUser(uid);
            log.info("🔥 [DELETE] Firebase Auth account has been successfully deleted for UID: {}", uid);
        } catch (FirebaseAuthException e) {
            log.error("❌ [DELETE] Failed to delete Firebase Auth account for UID: {}, Error: {}", uid, e.getMessage());
            throw new RuntimeException("Failed to delete Firebase account. Database operations have been rolled back.", e);
        }
    }
}
