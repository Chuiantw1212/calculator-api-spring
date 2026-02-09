package com.en_chu.calculator_api_spring.mapper;

import com.en_chu.calculator_api_spring.entity.UserCreditCard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserCreditCardMapper {

    int insert(UserCreditCard entity);

    int update(UserCreditCard entity);

    List<UserCreditCard> selectByUid(@Param("uid") String uid);

    Optional<UserCreditCard> selectByIdAndUid(@Param("id") Long id, @Param("uid") String uid);

    int deleteByIdAndUid(@Param("id") Long id, @Param("uid") String uid);

    int deleteByUid(@Param("uid") String uid);
}
