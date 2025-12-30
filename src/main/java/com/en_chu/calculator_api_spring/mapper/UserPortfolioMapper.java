package com.en_chu.calculator_api_spring.mapper;

import java.util.List;
import java.util.Optional; // 記得加這個 import

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.en_chu.calculator_api_spring.entity.UserPortfolio;

@Mapper
public interface UserPortfolioMapper {

	/**
	 * 新增單筆部位
	 * 
	 * @return 影響行數
	 */
	int insert(UserPortfolio entity);

	/**
	 * 查詢該用戶所有的投資部位 (舊名: selectListByUid -> 改名: selectByUid)
	 */
	List<UserPortfolio> selectByUid(@Param("uid") String firebaseUid);

	/**
	 * 🆕 新增：查詢單筆部位 (包含權限檢查) Service 的 getById 和 update 後回傳需要用到
	 */
	Optional<UserPortfolio> selectByIdAndUid(@Param("id") Long id, @Param("uid") String firebaseUid);

	/**
	 * 更新單筆部位 (舊名: updateById -> 改名: update)
	 */
	int update(UserPortfolio entity);

	/**
	 * 刪除單筆部位 (舊名: deleteById -> 改名: deleteByIdAndUid，語意更精確)
	 */
	int deleteByIdAndUid(@Param("id") Long id, @Param("uid") String firebaseUid);

	/**
	 * 刪除該用戶所有部位 (重置用) - 這個可以保留，雖然 CRUD 沒用到，但在測試或重置功能會用到
	 */
	void deleteAllByUid(@Param("uid") String firebaseUid);

}