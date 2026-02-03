package com.en_chu.calculator_api_spring.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.en_chu.calculator_api_spring.entity.UserCreditCard;

@Mapper
public interface UserCreditCardMapper {

	/**
	 * 查詢使用者所有信用卡列表 對應 XML id="selectByUid" * @param firebaseUid 使用者 ID
	 * 
	 * @return 信用卡列表
	 */
	List<UserCreditCard> selectByUid(@Param("firebaseUid") String firebaseUid);

	/**
	 * 查詢單張信用卡詳情 對應 XML id="selectById" * @param id 信用卡 ID (PK)
	 * 
	 * @param firebaseUid 使用者 ID (確保資料安全性)
	 * @return 信用卡實體
	 */
	UserCreditCard selectById(@Param("id") Long id, @Param("firebaseUid") String firebaseUid);

	/**
	 * 新增信用卡 對應 XML id="insert" * @param record 信用卡實體
	 * 
	 * @return 影響筆數 (成功通常回傳 1)
	 */
	int insert(UserCreditCard record);

	/**
	 * 更新信用卡資訊 對應 XML id="update" * @param record 信用卡實體 (需包含 id 與 firebaseUid)
	 * 
	 * @return 影響筆數
	 */
	int update(UserCreditCard record);

	/**
	 * 刪除信用卡 對應 XML id="delete" * @param id 信用卡 ID
	 * 
	 * @param firebaseUid 使用者 ID (確保只能刪除自己的資料)
	 * @return 影響筆數
	 */
	int delete(@Param("id") Long id, @Param("firebaseUid") String firebaseUid);

	/**
	 * 刪除該用戶所有信用卡
	 */
	int deleteByUid(@Param("firebaseUid") String firebaseUid);
}