package com.en_chu.calculator_api_spring.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.en_chu.calculator_api_spring.entity.UserLaborInsurance;

@Mapper
public interface UserLaborInsuranceMapper {
	UserLaborInsurance selectByUid(@Param("firebaseUid") String firebaseUid);

	int updateByUid(UserLaborInsurance record);

	int insert(UserLaborInsurance record);

	int deleteByUid(@Param("firebaseUid") String firebaseUid);
}