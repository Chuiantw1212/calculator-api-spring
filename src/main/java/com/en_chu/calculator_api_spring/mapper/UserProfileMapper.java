package com.en_chu.calculator_api_spring.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.en_chu.calculator_api_spring.entity.UserProfile;

@Mapper
public interface UserProfileMapper {

	UserProfile selectByUid(String uid);

	int insert(UserProfile entity);

	int updateByUid(UserProfile entity);

	boolean existsByUid(String uid);

	int deleteByUid(String uid);

	// The following methods seem to be used in UserService but might not be in the XML
	boolean checkUserExists(String uid);
	void insertInitUser(String uid);
	void updateLastLogin(String uid);
}
