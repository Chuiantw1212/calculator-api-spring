package com.en_chu.calculator_api_spring.service;

import com.en_chu.calculator_api_spring.entity.UserTax;
import com.en_chu.calculator_api_spring.mapper.UserTaxMapper;
import com.en_chu.calculator_api_spring.model.UserTaxUpdateReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserTaxService {

    private final UserTaxMapper userTaxMapper;

    @Transactional
    public void updateTax(String uid, UserTaxUpdateReq req) {
        boolean exists = userTaxMapper.existsByUid(uid);
        UserTax entity;

        if (exists) {
            entity = userTaxMapper.selectByUid(uid);
        } else {
            log.info("✨ [Tax] 新用戶，建立新紀錄: UID={}", uid);
            entity = new UserTax();
            entity.setFirebaseUid(uid);
        }

        // --- 安全地更新欄位 ---
        entity.setEstimatedOtherIncome(req.getEstimatedOtherIncome());

        if (exists) {
            userTaxMapper.updateByUid(entity);
        } else {
            userTaxMapper.insert(entity);
        }
    }
    
    public UserTax getByUid(String uid) {
        return userTaxMapper.selectByUid(uid);
    }
}
