package com.en_chu.calculator_api_spring.service;

import com.en_chu.calculator_api_spring.entity.UserRetirement;
import com.en_chu.calculator_api_spring.mapper.UserRetirementMapper;
import com.en_chu.calculator_api_spring.model.UserRetirementDto;
import com.en_chu.calculator_api_spring.model.UserRetirementUpdateReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRetirementService {

    private final UserRetirementMapper userRetirementMapper;

    public UserRetirementDto getRetirement(String uid) {
        UserRetirement entity = userRetirementMapper.selectByUid(uid);
        if (entity == null) {
            return null;
        }
        UserRetirementDto dto = new UserRetirementDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Transactional
    public void updateRetirement(String uid, UserRetirementUpdateReq req) {
        UserRetirement entity = userRetirementMapper.selectByUid(uid);
        boolean isNew = entity == null;

        if (isNew) {
            entity = new UserRetirement();
            entity.setFirebaseUid(uid);
        }

        BeanUtils.copyProperties(req, entity);

        if (isNew) {
            userRetirementMapper.insert(entity);
        } else {
            userRetirementMapper.updateByUid(entity);
        }
    }

    @Transactional
    public void patchRetirement(String uid, UserRetirementDto req) {
        UserRetirement entity = userRetirementMapper.selectByUid(uid);
        boolean isNew = entity == null;

        if (isNew) {
            log.info("No existing retirement record found for PATCH, creating a new one for UID: {}", uid);
            entity = new UserRetirement();
            entity.setFirebaseUid(uid);
            entity.setHouseholdType("single"); // Provide a default for the NOT NULL column
        }

        // Use a temporary DTO to safely copy properties for PATCH
        UserRetirement tempDtoEntity = new UserRetirement();
        BeanUtils.copyProperties(req, tempDtoEntity);

        if (tempDtoEntity.getHouseholdType() != null) entity.setHouseholdType(tempDtoEntity.getHouseholdType());
        if (tempDtoEntity.getHousingMode() != null) entity.setHousingMode(tempDtoEntity.getHousingMode());
        if (tempDtoEntity.getHousingCost() != null) entity.setHousingCost(tempDtoEntity.getHousingCost());
        if (tempDtoEntity.getHealthTierCode() != null) entity.setHealthTierCode(tempDtoEntity.getHealthTierCode());
        if (tempDtoEntity.getHealthCost() != null) entity.setHealthCost(tempDtoEntity.getHealthCost());
        if (tempDtoEntity.getActiveLivingCode() != null) entity.setActiveLivingCode(tempDtoEntity.getActiveLivingCode());
        if (tempDtoEntity.getActiveLivingCost() != null) entity.setActiveLivingCost(tempDtoEntity.getActiveLivingCost());
        if (tempDtoEntity.getSlowGoStartAge() != null) entity.setSlowGoStartAge(tempDtoEntity.getSlowGoStartAge());
        if (tempDtoEntity.getDefenseTierCode() != null) entity.setDefenseTierCode(tempDtoEntity.getDefenseTierCode());
        if (tempDtoEntity.getMonthlyMedicalCost() != null) entity.setMonthlyMedicalCost(tempDtoEntity.getMonthlyMedicalCost());
        if (tempDtoEntity.getCriticalIllnessCode() != null) entity.setCriticalIllnessCode(tempDtoEntity.getCriticalIllnessCode());
        if (tempDtoEntity.getCriticalIllnessReserve() != null) entity.setCriticalIllnessReserve(tempDtoEntity.getCriticalIllnessReserve());
        if (tempDtoEntity.getNogoStartAge() != null) entity.setNogoStartAge(tempDtoEntity.getNogoStartAge());
        if (tempDtoEntity.getLtcCareMode() != null) entity.setLtcCareMode(tempDtoEntity.getLtcCareMode());
        if (tempDtoEntity.getLtcMonthlyCost() != null) entity.setLtcMonthlyCost(tempDtoEntity.getLtcMonthlyCost());
        if (tempDtoEntity.getLtcMonthlySupplies() != null) entity.setLtcMonthlySupplies(tempDtoEntity.getLtcMonthlySupplies());
        if (tempDtoEntity.getLtcSubsidy() != null) entity.setLtcSubsidy(tempDtoEntity.getLtcSubsidy());

        if (isNew) {
            userRetirementMapper.insert(entity);
        } else {
            userRetirementMapper.updateByUid(entity);
        }
        log.info("Patched retirement settings for UID: {}", uid);
    }
}
