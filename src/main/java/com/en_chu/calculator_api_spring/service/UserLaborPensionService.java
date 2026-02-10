package com.en_chu.calculator_api_spring.service;

import com.en_chu.calculator_api_spring.entity.UserLaborPension;
import com.en_chu.calculator_api_spring.mapper.UserLaborPensionMapper;
import com.en_chu.calculator_api_spring.model.UserLaborPensionDto;
import com.en_chu.calculator_api_spring.model.UserLaborPensionUpdateReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserLaborPensionService {

    private final UserLaborPensionMapper userLaborPensionMapper;

    public UserLaborPensionDto getLaborPension(String uid) {
        UserLaborPension entity = userLaborPensionMapper.selectByUid(uid);
        if (entity == null) {
            log.warn("No labor pension record found for UID: {}. Creating a minimal default record.", uid);
            return createDefaultLaborPension(uid);
        }
        return convertToDto(entity);
    }

    @Transactional
    public void updateLaborPension(String uid, UserLaborPensionUpdateReq req) {
        boolean exists = userLaborPensionMapper.existsByUid(uid);
        UserLaborPension entity;

        if (exists) {
            entity = userLaborPensionMapper.selectByUid(uid);
        } else {
            log.info("No existing labor pension found for update, creating a new one for UID: {}", uid);
            entity = new UserLaborPension();
            entity.setFirebaseUid(uid);
        }

        BeanUtils.copyProperties(req, entity);

        if (exists) {
            userLaborPensionMapper.updateByUid(entity);
        } else {
            userLaborPensionMapper.insert(entity);
        }
    }

    @Transactional
    private UserLaborPensionDto createDefaultLaborPension(String uid) {
        UserLaborPension newPension = new UserLaborPension();
        newPension.setFirebaseUid(uid);
        userLaborPensionMapper.insert(newPension);
        log.info("✅ Minimal default labor pension record created for UID: {}", uid);
        return convertToDto(newPension);
    }

    private UserLaborPensionDto convertToDto(UserLaborPension entity) {
        UserLaborPensionDto dto = new UserLaborPensionDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
