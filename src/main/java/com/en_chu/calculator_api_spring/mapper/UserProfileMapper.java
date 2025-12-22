package com.en_chu.calculator_api_spring.mapper;

import com.en_chu.calculator_api_spring.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserProfileMapper {

	// 對應 id="insert"
	void insert(UserProfile userProfile);

	// 對應 id="update" (回傳影響筆數)
	int update(UserProfile userProfile);

	// 對應 id="selectByUid"
	UserProfile selectByUid(String firebaseUid);

	// 對應 id="selectById"
	UserProfile selectById(Long id);
}