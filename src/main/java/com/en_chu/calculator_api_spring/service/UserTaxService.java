package com.en_chu.calculator_api_spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.en_chu.calculator_api_spring.entity.UserTax;
import com.en_chu.calculator_api_spring.mapper.UserTaxMapper;
import com.en_chu.calculator_api_spring.model.UserTaxDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserTaxService {

    @Autowired
    private UserTaxMapper userTaxMapper;

    /**
     * 更新或新增稅務設定
     */
    @Transactional
    public void updateTax(String uid, UserTaxDto req) {
        // 1. 檢查是否存在
        UserTax existing = userTaxMapper.selectByUid(uid);

        // 2. 轉換 DTO -> Entity
        UserTax entity = UserTax.builder()
                .firebaseUid(uid)
                .estimatedOtherIncome(req.getEstimatedOtherIncome())
                .build();

        if (existing == null) {
            // 新增
            userTaxMapper.insert(entity);
            log.info("已新增使用者稅務設定: {}", uid);
        } else {
            // 更新
            userTaxMapper.updateByUid(entity);
            log.info("已更新使用者稅務設定: {}", uid);
        }
    }
    
    /**
     * 獲取稅務設定 (通常給 UserService 整合用)
     */
    public UserTax getByUid(String uid) {
        return userTaxMapper.selectByUid(uid);
    }
}