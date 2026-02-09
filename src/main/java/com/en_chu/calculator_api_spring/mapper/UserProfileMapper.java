package com.en_chu.calculator_api_spring.mapper;

import com.en_chu.calculator_api_spring.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserProfileMapper {

    UserProfile selectByUid(@Param("firebaseUid") String firebaseUid);

    int insert(UserProfile entity);

    int updateByUid(UserProfile entity);

    boolean existsByUid(@Param("firebaseUid") String firebaseUid);

    int deleteByUid(@Param("firebaseUid") String firebaseUid);

    boolean checkUserExists(@Param("firebaseUid") String firebaseUid);

    void insertInitUser(@Param("firebaseUid") String firebaseUid);

    void updateLastLogin(@Param("firebaseUid") String firebaseUid);
}
