package com.en_chu.calculator_api_spring.service;

import com.en_chu.calculator_api_spring.entity.UserCareer;
import com.en_chu.calculator_api_spring.mapper.UserCareerMapper;
import com.en_chu.calculator_api_spring.model.UserCareerUpdateReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCareerService {

    private final UserCareerMapper userCareerMapper;

    @Transactional
    public void updateCareer(String uid, UserCareerUpdateReq req) {
        boolean exists = userCareerMapper.existsByUid(uid);
        UserCareer entity;

        if (exists) {
            entity = userCareerMapper.selectByUid(uid);
        } else {
            log.info("No existing career found for UID {}, creating new record.", uid);
            entity = new UserCareer();
            entity.setFirebaseUid(uid);
        }

        // --- 安全地更新欄位 ---
        entity.setBaseSalary(req.getBaseSalary());
        entity.setOtherAllowance(req.getOtherAllowance());
        entity.setLaborInsurance(req.getLaborInsurance());
        entity.setHealthInsurance(req.getHealthInsurance());
        entity.setOtherDeduction(req.getOtherDeduction());
        entity.setPensionPersonalRate(req.getPensionPersonalRate());
        entity.setStockDeduction(req.getStockDeduction());
        entity.setStockCompanyMatch(req.getStockCompanyMatch());
        entity.setAnnualBonus(req.getAnnualBonus());
        entity.setDependents(req.getDependents());

        // --- 重新計算衍生欄位 (如果需要) ---
        // 這裡可以加入計算 monthlyNetIncome, annualTotalIncome 等欄位的邏輯
        // 為了保持簡單，我們先假設這些計算在前端完成，或在另一個服務中處理

        if (exists) {
            userCareerMapper.updateByUid(entity);
        } else {
            userCareerMapper.insert(entity);
        }
    }
}
