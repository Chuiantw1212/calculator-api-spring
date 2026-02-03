package com.en_chu.calculator_api_spring.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.en_chu.calculator_api_spring.entity.UserTax;

@Mapper
public interface UserTaxMapper {

    /**
     * 對應 XML id="insert"
     */
    int insert(UserTax record);

    /**
     * 對應 XML id="updateByUid"
     */
    int updateByUid(UserTax record);

    /**
     * 對應 XML id="selectByUid"
     */
    UserTax selectByUid(@Param("firebaseUid") String firebaseUid);

    /**
     * 對應 XML id="deleteByUid"
     */
    int deleteByUid(@Param("firebaseUid") String firebaseUid);

}