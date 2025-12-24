package com.en_chu.calculator_api_spring.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.en_chu.calculator_api_spring.model.UserCareerReq;
import com.en_chu.calculator_api_spring.model.UserCareerRes;

@Mapper
public interface UserCareerMapper {

	/**
	 * 根據 User ID 查詢職涯收入資料 用於 /api/v1/user/career (GET) * @param userId 使用者 ID
	 * 
	 * @return 職涯資料回應物件 (若無資料則回傳 null)
	 */
	UserCareerRes findCareerByUserId(@Param("userId") Long userId);

	/**
	 * 新增職涯資料 (Insert) 當使用者第一次設定薪資資料時呼叫 * @param req 前端傳來的資料物件 (包含薪資、保險等)
	 * 
	 * @param userId 關聯的使用者 ID (Foreign Key)
	 */
	void insertCareer(@Param("req") UserCareerReq req, @Param("userId") Long userId);

	/**
	 * 更新職涯資料 (Update) 當使用者修改既有資料時呼叫 * @param req 前端傳來的資料物件 (必須包含 id)
	 */
	void updateCareer(@Param("req") UserCareerReq req);
}