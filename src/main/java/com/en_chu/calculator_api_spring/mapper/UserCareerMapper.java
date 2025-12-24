package com.en_chu.calculator_api_spring.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.en_chu.calculator_api_spring.entity.UserCareer;

@Mapper
public interface UserCareerMapper {

	/**
	 * 透過 UID 更新職涯資料 對應 XML id="updateByUid"
	 * 
	 * @return 影響筆數 (0 代表無此資料，Service 會轉去呼叫 insert)
	 */
	int updateByUid(UserCareer userCareer);

	/**
	 * 新增職涯資料 對應 XML id="insert" 注意：XML 需使用子查詢 (SELECT id FROM users...) 自動填入
	 * user_id
	 */
	void insert(UserCareer userCareer);

	/**
	 * 透過 UID 查詢 對應 XML id="selectByUid"
	 */
	UserCareer selectByUid(@Param("firebaseUid") String firebaseUid);
}