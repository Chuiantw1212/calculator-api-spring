package com.en_chu.calculator_api_spring.mapper;

import com.en_chu.calculator_api_spring.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserProfileMapper {

    UserProfile selectByUid(@Param("uid") String uid);

    int insert(UserProfile entity);

    int updateByUid(UserProfile entity);

    boolean existsByUid(@Param("uid") String uid);

    int deleteByUid(@Param("uid") String uid);

    boolean checkUserExists(@Param("uid") String uid);

    void insertInitUser(@Param("uid") String uid);

    void updateLastLogin(@Param("uid") String uid);
}
