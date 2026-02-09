package com.en_chu.calculator_api_spring.mapper;

import com.en_chu.calculator_api_spring.entity.UserPortfolio;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserPortfolioMapper {

    int insert(UserPortfolio entity);

    int update(UserPortfolio entity);

    List<UserPortfolio> selectByUid(@Param("uid") String uid);

    Optional<UserPortfolio> selectByIdAndUid(@Param("id") Long id, @Param("uid") String uid);

    int deleteByIdAndUid(@Param("id") Long id, @Param("uid") String uid);

    int deleteByUid(@Param("uid") String uid);
}
