package com.en_chu.calculator_api_spring.mapper;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.en_chu.calculator_api_spring.entity.UserBusiness;

@Mapper
public interface UserBusinessMapper {

	/**
	 * 新增事業項目 XML id="insert"
	 */
	int insert(UserBusiness entity);

	/**
	 * 查詢該用戶所有的事業列表 XML id="selectByUid"
	 */
	List<UserBusiness> selectByUid(@Param("uid") String firebaseUid);
	
	// 分頁查詢 (注意 PostgreSQL 的 LIMIT/OFFSET 參數)
    List<UserBusiness> selectPage(
        @Param("uid") String uid, 
        @Param("limit") int limit, 
        @Param("offset") int offset
    );

    // 總數查詢
    long countByUid(@Param("uid") String uid);

	/**
	 * 查詢單筆事業 (包含權限檢查) XML id="selectByIdAndUid"
	 */
	Optional<UserBusiness> selectByIdAndUid(@Param("id") Long id, @Param("uid") String firebaseUid);

	/**
	 * 更新事業資訊 XML id="update"
	 */
	int update(UserBusiness entity);

	/**
	 * 刪除單筆事業 XML id="deleteByIdAndUid"
	 */
	int deleteByIdAndUid(@Param("id") Long id, @Param("uid") String firebaseUid);

	/**
	 * 刪除該用戶所有事業 XML id="deleteByUid"
	 */
	int deleteByUid(@Param("uid") String firebaseUid);

}