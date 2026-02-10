package com.en_chu.calculator_api_spring.service;

import com.en_chu.calculator_api_spring.entity.UserProfile;
import com.en_chu.calculator_api_spring.mapper.UserProfileMapper;
import com.en_chu.calculator_api_spring.model.UserProfileDto;
import com.en_chu.calculator_api_spring.model.UserProfileUpdateReq;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Period;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileMapper userProfileMapper;

    @Transactional
    public void createProfile(String uid, UserProfileDto req) {
        // 在新增前，先檢查資料是否存在，避免重複建立
        if (userProfileMapper.existsByUid(uid)) {
            throw new RuntimeException("資料已存在，請使用更新功能");
        }

        UserProfile entity = new UserProfile();
        BeanUtils.copyProperties(req, entity);
        entity.setFirebaseUid(uid);

        userProfileMapper.insert(entity);
    }

    /**
     * 安全地更新或建立 (Upsert) 使用者個人資料。
     * 這個方法遵循了兩個重要的最佳實踐：
     * 1. 效能優化：先用高效的 `exists` 查詢判斷是否存在，避免不必要的 `SELECT *`。
     * 2. 安全性：使用特定的 `UserProfileUpdateReq` DTO，只允許更新指定的欄位，防範巨量請求攻擊。
     *
     * @param uid 使用者的 Firebase UID
     * @param req 只包含允許更新欄位的請求 DTO
     */
    @Transactional
    public void updateProfile(String uid, UserProfileUpdateReq req) {
        // 效能優化：先用一個輕量的查詢判斷紀錄是否存在
        boolean exists = userProfileMapper.existsByUid(uid);
        UserProfile entity;

        if (exists) {
            // 只有在確定紀錄存在時，才執行 SELECT 來獲取完整的 Entity 物件
            entity = userProfileMapper.selectByUid(uid);
        } else {
            // 如果不存在，則建立一個新的實體，準備後續的插入操作
            entity = new UserProfile();
            entity.setFirebaseUid(uid);
        }

        // 安全性：手動、明確地將請求 DTO 中的欄位值，設定到 Entity 上
        // 這可以防止惡意使用者透過 API 更新他們不應該有權限修改的欄位 (例如 id, firebaseUid, createdAt)
        entity.setBirthDate(req.getBirthDate());
        entity.setGender(req.getGender());
        entity.setMarriageYear(req.getMarriageYear());
        entity.setBiography(req.getBiography());

        // 業務邏輯：如果生日有變更，自動重新計算並更新年齡
        if (req.getBirthDate() != null) {
            entity.setCurrentAge(Period.between(req.getBirthDate(), java.time.LocalDate.now()).getYears());
        }

        // 根據是否存在，來決定是執行更新還是插入
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
