package com.en_chu.calculator_api_spring.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.en_chu.calculator_api_spring.entity.UserRetirementExpense;

@Mapper
public interface UserRetirementExpenseMapper {
	UserRetirementExpense selectByUid(@Param("firebaseUid") String firebaseUid);

	int updateByUid(UserRetirementExpense record);

	int insert(UserRetirementExpense record);
}