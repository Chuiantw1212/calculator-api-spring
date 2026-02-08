package com.en_chu.calculator_api_spring.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.en_chu.calculator_api_spring.entity.UserTax;

@Mapper
public interface UserTaxMapper {

    UserTax selectByUid(String uid);

    int insert(UserTax entity);

    int updateByUid(UserTax entity);

    boolean existsByUid(String uid);

    int deleteByUid(String uid);
}
