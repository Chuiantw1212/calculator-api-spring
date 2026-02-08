package com.en_chu.calculator_api_spring.service;

import com.en_chu.calculator_api_spring.entity.UserProfile;
import com.en_chu.calculator_api_spring.mapper.UserProfileMapper;
import com.en_chu.calculator_api_spring.model.UserProfileDto;
import com.en_chu.calculator_api_spring.model.UserProfileUpdateReq;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Period;

@Service
public class UserProfileService {

    private final UserProfileMapper userProfileMapper;

    public UserProfileService(UserProfileMapper userProfileMapper) {
        this.userProfileMapper = userProfileMapper;
    }

    @Transactional
    public void createProfile(String uid, UserProfileDto req) {
        if (userProfileMapper.existsByUid(uid)) {
            throw new RuntimeException("資料已存在，請使用更新功能");
        }

        UserProfile entity = new UserProfile();
        BeanUtils.copyProperties(req, entity);
        entity.setFirebaseUid(uid);

        userProfileMapper.insert(entity);
    }

    @Transactional
    public void updateProfile(String uid, UserProfileUpdateReq req) {
        UserProfile entity;
        boolean exists = userProfileMapper.existsByUid(uid);

        if (exists) {
            // 只有在確定存在時，才執行 SELECT 來獲取完整資料
            entity = userProfileMapper.selectByUid(uid);
        } else {
            // 如果不存在，則建立一個新的實體準備插入
            entity = new UserProfile();
            entity.setFirebaseUid(uid);
        }

        // 安全地將請求中的欄位值，設定到 Entity 上
        entity.setBirthDate(req.getBirthDate());
        entity.setGender(req.getGender());
        entity.setMarriageYear(req.getMarriageYear());
        entity.setBiography(req.getBiography());

        // 如果生日有變更，重新計算年齡
        if (req.getBirthDate() != null) {
            entity.setCurrentAge(Period.between(req.getBirthDate(), java.time.LocalDate.now()).getYears());
        }

        // 根據是否存在，決定呼叫 insert 還是 update
        if (exists) {
            userProfileMapper.updateByUid(entity);
        } else {
            userProfileMapper.insert(entity);
        }
    }

    public UserProfileDto getProfile(String uid) {
        UserProfile entity = userProfileMapper.selectByUid(uid);
        if (entity == null) {
            return null;
        }
        UserProfileDto res = new UserProfileDto();
        BeanUtils.copyProperties(entity, res);
        return res;
    }
}
