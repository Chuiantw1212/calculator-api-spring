package com.en_chu.calculator_api_spring.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.en_chu.calculator_api_spring.entity.UserLaborPension;

@Mapper
public interface UserLaborPensionMapper {

	UserLaborPension selectByUid(String uid);

	int insert(UserLaborPension entity);

	int updateByUid(UserLaborPension entity);

	boolean existsByUid(String uid);

	int deleteByUid(String uid);
}
