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
            log.info("ℹ️ [LaborPension] 查無資料: UID={}", uid);
            return null;
        }
        UserLaborPensionDto res = new UserLaborPensionDto();
        BeanUtils.copyProperties(entity, res);
        return res;
    }

    @Transactional
    public void updateLaborPension(String uid, UserLaborPensionUpdateReq req) {
        log.info("🔄 [LaborPension] 準備更新資料: UID={}", uid);

        boolean exists = userLaborPensionMapper.existsByUid(uid);
        UserLaborPension entity;

        if (exists) {
            entity = userLaborPensionMapper.selectByUid(uid);
        } else {
            log.info("✨ [LaborPension] 新用戶，建立新紀錄: UID={}", uid);
            entity = new UserLaborPension();
            entity.setFirebaseUid(uid);
        }

        // --- 安全地更新欄位 ---
        entity.setExpectedRetirementAge(req.getExpectedRetirementAge());
        entity.setRetirementRoi(req.getRetirementRoi());
        entity.setEmployerContribution(req.getEmployerContribution());
        entity.setEmployerEarnings(req.getEmployerEarnings());
        entity.setPersonalContribution(req.getPersonalContribution());
        entity.setPersonalEarnings(req.getPersonalEarnings());
        entity.setCurrentWorkSeniority(req.getCurrentWorkSeniority());

        // 衍生欄位 (如 predictedLumpSum) 應由後端計算，而不是由前端傳入
        // 這裡可以加入計算衍生欄位的邏輯

        if (exists) {
            userLaborPensionMapper.updateByUid(entity);
        } else {
            userLaborPensionMapper.insert(entity);
        }
    }
}
