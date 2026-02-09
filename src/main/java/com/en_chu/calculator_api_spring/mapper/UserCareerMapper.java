package com.en_chu.calculator_api_spring.mapper;

import com.en_chu.calculator_api_spring.entity.UserCareer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserCareerMapper {

    UserCareer selectByUid(@Param("uid") String uid);

    int insert(UserCareer entity);

    int updateByUid(UserCareer entity);

    boolean existsByUid(@Param("uid") String uid);

    int deleteByUid(@Param("uid") String uid);
}
