package com.en_chu.calculator_api_spring.mapper;

import com.en_chu.calculator_api_spring.entity.UserBusiness;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserBusinessMapper {

    int insert(UserBusiness entity);

    int update(UserBusiness entity);

    List<UserBusiness> selectByUid(@Param("uid") String uid);

    Optional<UserBusiness> selectByIdAndUid(@Param("id") Long id, @Param("uid") String uid);

    List<UserBusiness> selectPage(@Param("uid") String uid, @Param("limit") int limit, @Param("offset") int offset);

    long countByUid(@Param("uid") String uid);

    int deleteByIdAndUid(@Param("id") Long id, @Param("uid") String uid);

    int deleteByUid(@Param("uid") String uid);
}
