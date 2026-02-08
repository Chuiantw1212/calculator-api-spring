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

        entity.setIndustry(req.getIndustry());
        entity.setCompanyName(req.getCompanyName());
        entity.setJobTitle(req.getJobTitle());
        entity.setIsWorking(req.getIsWorking());
        entity.setMonthlySalary(req.getMonthlySalary());
        entity.setAnnualBonusMonths(req.getAnnualBonusMonths());
        entity.setAvgWorkingHoursPerDay(req.getAvgWorkingHoursPerDay());
        entity.setRetirementAge(req.getRetirementAge());

        if (exists) {
            userCareerMapper.updateByUid(entity);
        } else {
            userCareerMapper.insert(entity);
        }
    }
}
