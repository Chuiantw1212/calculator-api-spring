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
        if (userProfileMapper.selectByUid(uid) != null) {
            throw new RuntimeException("資料已存在，請使用更新功能");
        }

        UserProfile entity = new UserProfile();
        BeanUtils.copyProperties(req, entity);
        entity.setFirebaseUid(uid);

        userProfileMapper.insert(entity);
    }

    /**
     * 更新個人資料 - 安全模式
     * @param uid 使用者 Firebase UID
     * @param req 只包含允許更新欄位的 DTO
     */
    @Transactional
    public void updateProfile(String uid, UserProfileUpdateReq req) {
        // 1. 先從資料庫取出最新的 Entity，確保我們在最新的資料上修改
        UserProfile entity = userProfileMapper.selectByUid(uid);

        // 2. 如果找不到資料，拋出例外或執行新增邏輯
        if (entity == null) {
            // 這裡可以根據業務邏輯決定是拋出錯誤，還是自動為他建立一筆新的
            // 為求簡單，我們先拋出錯誤
            throw new RuntimeException("更新失敗：找不到您的個人檔案，請先執行新增 (Create)");
        }

        // 3. 手動、安全地將請求中的欄位值，設定到 Entity 上
        entity.setBirthDate(req.getBirthDate());
        entity.setGender(req.getGender());
        entity.setMarriageYear(req.getMarriageYear());
        entity.setBiography(req.getBiography());

        // 4. 如果生日有變更，重新計算年齡
        if (req.getBirthDate() != null) {
            entity.setCurrentAge(Period.between(req.getBirthDate(), java.time.LocalDate.now()).getYears());
        }

        // 5. 呼叫 Update
        userProfileMapper.updateByUid(entity);
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
