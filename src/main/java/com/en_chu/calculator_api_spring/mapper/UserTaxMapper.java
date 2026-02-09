package com.en_chu.calculator_api_spring.mapper;

import com.en_chu.calculator_api_spring.entity.UserTax;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserTaxMapper {

    UserTax selectByUid(@Param("uid") String uid);

    int insert(UserTax entity);

    int updateByUid(UserTax entity);

    boolean existsByUid(@Param("uid") String uid);

    int deleteByUid(@Param("uid") String uid);
}
