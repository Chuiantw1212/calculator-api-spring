package com.en_chu.calculator_api_spring.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.en_chu.calculator_api_spring.entity.UserProfile;

@Mapper
public interface UserProfileMapper {

	// 定義一個方法叫 upsertProfile
	// MyBatis 會拿這個名字去 XML 找對應的 SQL
	int upsertProfile(UserProfile profile);

	// 如果未來要查詢，可以再加：
	// UserProfile findById(String uid);
}