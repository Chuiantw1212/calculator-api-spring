package com.en_chu.calculator_api_spring.mapper;

import com.en_chu.calculator_api_spring.entity.UserLaborInsurance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserLaborInsuranceMapper {

	UserLaborInsurance selectByUid(@Param("uid") String uid);

	int insert(UserLaborInsurance entity);

	int updateByUid(UserLaborInsurance entity);

	int deleteByUid(@Param("uid") String uid);

	boolean existsByUid(@Param("uid") String uid);
}
