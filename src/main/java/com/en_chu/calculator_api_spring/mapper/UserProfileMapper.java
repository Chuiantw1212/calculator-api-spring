package com.en_chu.calculator_api_spring.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.en_chu.calculator_api_spring.entity.UserProfile;

@Mapper
public interface UserProfileMapper {

	/**
	 * 新增個人資料 對應 XML id="insert"
	 */
	void insert(UserProfile userProfile);

	/**
	 * 更新個人資料 (根據 UID) 對應 XML id="updateByUid" * 關鍵：這個方法名稱必須跟 XML 的 id 一模一樣
	 */
	int updateByUid(UserProfile userProfile);

	/**
	 * 查詢個人資料 (根據 UID) 對應 XML id="selectByUid"
	 */
	UserProfile selectByUid(@Param("firebaseUid") String firebaseUid);

}