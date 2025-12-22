package com.en_chu.calculator_api_spring.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.en_chu.calculator_api_spring.entity.UserProfile;

@Mapper
public interface UserProfileMapper {

	// 根據 UID 查詢
	UserProfile selectByUid(String uid);

	// 根據 ID 查詢
	UserProfile selectById(Long id);

	// 新增資料
	void insert(UserProfile userProfile);

	// 更新資料
	int update(UserProfile userProfile);
}