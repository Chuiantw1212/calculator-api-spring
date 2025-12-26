package com.en_chu.calculator_api_spring.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.en_chu.calculator_api_spring.entity.UserPortfolio;

@Mapper
public interface UserPortfolioMapper {

	/**
	 * 查詢該用戶所有的投資部位 XML id="selectListByUid"
	 */
	List<UserPortfolio> selectListByUid(@Param("firebaseUid") String firebaseUid);

	/**
	 * 新增單筆部位 XML id="insert"
	 */
	void insert(UserPortfolio userSecurity);

	/**
	 * 更新單筆部位 (必須包含 id 與 firebaseUid) XML id="updateById"
	 * 
	 * @return 影響的行數 (1=成功, 0=失敗)
	 */
	int updateById(UserPortfolio userSecurity);

	/**
	 * 刪除單筆部位 XML id="deleteById" 這裡有多個參數，必須使用 @Param 指定名稱
	 */
	int deleteById(@Param("id") Long id, @Param("firebaseUid") String firebaseUid);

	/**
	 * 刪除該用戶所有部位 (重置用) XML id="deleteAllByUid"
	 */
	void deleteAllByUid(@Param("firebaseUid") String firebaseUid);

}