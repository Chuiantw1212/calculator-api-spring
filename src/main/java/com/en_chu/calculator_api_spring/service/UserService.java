package com.en_chu.calculator_api_spring.service;

import com.en_chu.calculator_api_spring.entity.UserProfile;
import com.en_chu.calculator_api_spring.mapper.*;
import com.en_chu.calculator_api_spring.model.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    // Services for 1:1 relationships
    private final UserProfileService userProfileService;
    private final UserCareerService userCareerService;
    private final UserLaborPensionService userLaborPensionService;
    private final UserLaborInsuranceService userLaborInsuranceService;
    private final UserRetirementService userRetirementService;
    private final UserTaxService userTaxService;

    // Mappers for 1:N relationships (only used for deleteUser)
    private final UserBusinessMapper userBusinessMapper;
    private final UserCreditCardMapper userCreditCardMapper;
    private final UserPortfolioMapper userPortfolioMapper;
    private final UserRealEstateMapper userRealEstateMapper;
    
    // Mapper for user sync
    private final UserProfileMapper userProfileMapper;

    /**
     * Assembles the core, 1-to-1 data for the current user.
     * If any of the core data records do not exist, they will be created with default values.
     */
    public UserFullDataRes getFullUserData(String uid) {
        log.info("🔍 [UserService] Assembling core user data for UID: {}", uid);
        UserFullDataRes response = new UserFullDataRes();

        // Each get...() method now contains the "get or create default" logic.
        response.setProfile(userProfileService.getProfile(uid));
        response.setCareer(userCareerService.getCareer(uid));
        response.setLaborPension(userLaborPensionService.getLaborPension(uid));
        response.setLaborInsurance(userLaborInsuranceService.getLaborInsurance(uid));
        response.setRetirement(userRetirementService.getRetirement(uid));
        response.setTax(userTaxService.getTax(uid));
        
        // Set the top-level ID from the profile
        if (response.getProfile() != null) {
            response.setId(response.getProfile().getId());
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
