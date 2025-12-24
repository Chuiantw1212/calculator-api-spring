package com.en_chu.calculator_api_spring.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.en_chu.calculator_api_spring.model.UserFullDataRes;
@Mapper
public interface UserMapper {

    /**
     * 以 Profile 為核心，組裝完整資料
     */
    UserFullDataRes selectFullUserDataByFirebaseUid(@Param("firebaseUid") String firebaseUid);

    /**
     * 檢查 Profile 是否已存在
     */
    boolean checkUserExists(@Param("firebaseUid") String firebaseUid);

    /**
     * 這裡的語意變成了 "初始化 Profile" (如果是 Login Filter 自動觸發的話)
     * 如果你沒有 email 欄位在 profile，記得拿掉 @Param("email")
     */
    void insertUser(@Param("firebaseUid") String firebaseUid);

    /**
     * 更新登入時間 (操作 user_profiles)
     */
    void updateLastLogin(@Param("firebaseUid") String firebaseUid);
}