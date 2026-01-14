package com.en_chu.calculator_api_spring.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.en_chu.calculator_api_spring.entity.UserRetirement;

@Mapper
public interface UserRetirementMapper {

    int insert(UserRetirement record);

    int updateByUid(UserRetirement record);
    
    int updateSelectiveByUid(UserRetirement record);

    UserRetirement selectByUid(@Param("firebaseUid") String firebaseUid);
}