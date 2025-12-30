package com.en_chu.calculator_api_spring.mapper;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.en_chu.calculator_api_spring.entity.UserRealEstate;

@Mapper
public interface UserRealEstateMapper {

	/**
	 * 新增房地產
	 * 
	 * @param entity (MyBatis 會自動把產生的 ID 填回這個物件)
	 * @return 影響行數 (1=成功)
	 */
	int insert(UserRealEstate entity);

	/**
	 * 查詢用戶的所有房地產列表
	 * 
	 * @param firebaseUid 用戶 ID
	 * @return 房地產列表
	 */
	List<UserRealEstate> selectByUid(@Param("uid") String firebaseUid);

	/**
	 * 查詢單一房地產 (包含權限檢查)
	 * 
	 * @param id          物件 ID
	 * @param firebaseUid 用戶 ID (確保只能查自己的)
	 * @return Optional 包裝的 Entity
	 */
	Optional<UserRealEstate> selectByIdAndUid(@Param("id") Long id, @Param("uid") String firebaseUid);

	/**
	 * 更新房地產資料
	 * 
	 * @param entity 需包含 id 與 firebaseUid
	 * @return 影響行數
	 */
	int update(UserRealEstate entity);

	/**
	 * 刪除房地產
	 * 
	 * @param id          物件 ID
	 * @param firebaseUid 用戶 ID (確保只能刪自己的)
	 * @return 影響行數
	 */
	int deleteByIdAndUid(@Param("id") Long id, @Param("uid") String firebaseUid);
}