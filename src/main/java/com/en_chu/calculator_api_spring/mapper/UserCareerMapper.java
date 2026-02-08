package com.en_chu.calculator_api_spring.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.en_chu.calculator_api_spring.entity.UserCareer;

@Mapper
public interface UserCareerMapper {

	UserCareer selectByUid(String uid);

	int insert(UserCareer entity);

	int updateByUid(UserCareer entity);

	boolean existsByUid(String uid);

	int deleteByUid(String uid);
}
