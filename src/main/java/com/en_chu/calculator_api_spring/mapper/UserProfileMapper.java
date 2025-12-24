package com.en_chu.calculator_api_spring.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.en_chu.calculator_api_spring.entity.UserProfile;

@Mapper
public interface UserProfileMapper {

	/**
	 * 新增個人資料 (Insert) 對應 XML id="insert" 注意：XML 需使用子查詢 (SELECT id FROM users...)
	 * 來自動填入 user_id
	 */
	void insert(UserProfile userProfile);

	/**
	 * 透過 UID 更新個人資料 (Update) 對應 XML id="updateByUid" * 我們改名為 updateByUid 以區別傳統的
	 * update (by PK)。 這樣很清楚知道 SQL 的 WHERE 條件是 firebase_uid。
	 */
	int updateByUid(UserProfile userProfile);

	/**
	 * 透過 UID 查詢個人資料 (Select) 對應 XML id="selectByUid"
	 */
	UserProfile selectByUid(@Param("firebaseUid") String firebaseUid);

	/**
	 * 透過 PK ID 查詢 (Select) 對應 XML id="selectById" (目前主要邏輯改用 UID，此方法保留給管理者後台或除錯使用)
	 */
	UserProfile selectById(@Param("id") Long id);
}	