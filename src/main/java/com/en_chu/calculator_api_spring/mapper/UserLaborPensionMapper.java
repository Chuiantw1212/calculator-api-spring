package com.en_chu.calculator_api_spring.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.en_chu.calculator_api_spring.entity.UserLaborPension;

@Mapper
public interface UserLaborPensionMapper {

	UserLaborPension selectByUid(@Param("firebaseUid") String firebaseUid);

	int updateByUid(UserLaborPension record);

	int insert(UserLaborPension record);

	int deleteByUid(@Param("firebaseUid") String firebaseUid);
}