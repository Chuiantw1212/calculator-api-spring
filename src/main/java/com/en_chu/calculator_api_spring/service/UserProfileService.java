package com.en_chu.calculator_api_spring.service;

import com.en_chu.calculator_api_spring.entity.UserProfile;
import com.en_chu.calculator_api_spring.mapper.UserProfileMapper;
import com.en_chu.calculator_api_spring.model.UserProfileDto;
import com.en_chu.calculator_api_spring.model.UserProfileUpdateReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Period;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileMapper userProfileMapper;

    /**
     * Gets a user's profile. If not found, creates a minimal one, saves it, and returns it.
     * @param uid The Firebase UID of the user.
     * @return The user's profile DTO.
     */
    public UserProfileDto getProfile(String uid) {
        UserProfile entity = userProfileMapper.selectByUid(uid);
        if (entity == null) {
            log.warn("No profile found for UID: {}. Creating a minimal default profile.", uid);
            return createDefaultProfile(uid);
        }
        return convertToDto(entity);
    }

    /**
     * Updates a user's profile. If not found, creates a new one (Upsert logic).
     * @param uid The Firebase UID of the user.
     * @param req The request DTO containing the fields to update.
     */
    @Transactional
    public void updateProfile(String uid, UserProfileUpdateReq req) {
        UserProfile entity = userProfileMapper.selectByUid(uid);
        boolean isNew = entity == null;

        if (isNew) {
            log.info("No existing profile found for update, creating a new one for UID: {}", uid);
            entity = new UserProfile();
            entity.setFirebaseUid(uid);
        }

        // Safely map fields from the request DTO
        entity.setBirthDate(req.getBirthDate());
        entity.setGender(req.getGender());
        entity.setMarriageYear(req.getMarriageYear());
        entity.setBiography(req.getBiography());

        // Business Logic: Recalculate age if birth date is provided
        if (req.getBirthDate() != null) {
            entity.setCurrentAge(Period.between(req.getBirthDate(), java.time.LocalDate.now()).getYears());
        } else if (isNew) {
            // Ensure age is null for a new profile without a birthdate
            entity.setCurrentAge(null);
        }

        if (isNew) {
            userProfileMapper.insert(entity);
        } else {
            userProfileMapper.updateByUid(entity);
        }
    }

    /**
     * Creates a minimal default profile for a new user, containing only the UID.
     * This ensures the record exists in the database.
     * @param uid The Firebase UID of the new user.
     * @return The DTO of the newly created minimal profile.
     */
    @Transactional
    private UserProfileDto createDefaultProfile(String uid) {
        UserProfile newProfile = new UserProfile();
        newProfile.setFirebaseUid(uid);
        // All other fields will be null by default, which is the desired "empty" state.
        
        userProfileMapper.insert(newProfile);
        log.info("✅ Minimal default profile created for UID: {}", uid);
        return convertToDto(newProfile);
    }

    private UserProfileDto convertToDto(UserProfile entity) {
        UserProfileDto dto = new UserProfileDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
