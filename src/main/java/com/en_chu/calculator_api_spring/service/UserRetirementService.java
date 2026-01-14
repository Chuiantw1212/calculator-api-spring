package com.en_chu.calculator_api_spring.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.en_chu.calculator_api_spring.entity.UserRetirement;
import com.en_chu.calculator_api_spring.mapper.UserRetirementMapper;
import com.en_chu.calculator_api_spring.model.UserRetirementDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserRetirementService {

    @Autowired
    private UserRetirementMapper userRetirementMapper;

    /**
     * 更新或建立退休生活設定 (Upsert)
     */
    @Transactional
    public void updateRetirement(String uid, UserRetirementDto req) {
        
        // 1. 轉換 DTO -> Entity
        UserRetirement entity = new UserRetirement();
        BeanUtils.copyProperties(req, entity);

        // 2. 設定 UID (Key)
        entity.setFirebaseUid(uid);

        // 3. 嘗試更新 (Update by UID)
        // Mapper 根據 firebase_uid 更新，回傳影響筆數
        int rowsAffected = userRetirementMapper.updateByUid(entity);

        // 4. 如果更新筆數為 0，代表該用戶還沒建立過資料 -> 執行新增
        if (rowsAffected == 0) {
            log.info("No existing retirement settings found for UID {}, creating new record.", uid);
            userRetirementMapper.insert(entity);
        }
    }
    
    /**
     * 獲取退休設定
     */
    public UserRetirement getByUid(String uid) {
        return userRetirementMapper.selectByUid(uid);
    }
    
    /**
     * 局部更新退休設定 (PATCH)
     * 只更新 DTO 中非 null 的欄位
     */
    @Transactional
    public void patchRetirement(String uid, UserRetirementDto req) {
        
        // 1. 建立 Entity 並複製屬性
        // BeanUtils 會將 req 中的 null 也複製過去，
        // 但因為我們是 new 一個新的 Entity，所以沒設值的欄位自然就是 null
        UserRetirement entity = new UserRetirement();
        BeanUtils.copyProperties(req, entity);
        
        // 2. 設定 Key
        entity.setFirebaseUid(uid);

        // 3. 執行局部更新
        int rows = userRetirementMapper.updateSelectiveByUid(entity);
        
        // 4. 如果更新筆數為 0，代表資料不存在，改為執行新增 (Insert)
        // 這種情況下，缺失的欄位會變成資料庫的預設值或 NULL
        if (rows == 0) {
            log.info("No record found for PATCH, inserting new one: {}", uid);
            userRetirementMapper.insert(entity);
        } else {
            log.info("Patched retirement settings for UID: {}", uid);
        }
    }
}