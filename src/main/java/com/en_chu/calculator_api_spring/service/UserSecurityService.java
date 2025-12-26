package com.en_chu.calculator_api_spring.service;

import java.math.BigDecimal;
import java.util.List; // ✅ 務必確認是 java.util.List

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.en_chu.calculator_api_spring.entity.UserSecurity;
import com.en_chu.calculator_api_spring.mapper.UserSecurityMapper;
import com.en_chu.calculator_api_spring.model.UserSecurityUpdateReq;

@Service
public class UserSecurityService {

	@Autowired
	private UserSecurityMapper userSecurityMapper;

	/**
	 * 1. 查詢該用戶的所有投資部位
	 */
	public List<UserSecurity> getUserSecurities(String uid) {
		return userSecurityMapper.selectListByUid(uid);
	}

	/**
	 * 2. 新增預設部位 (POST) 建立一筆預設為「台灣/台幣」的空資料，並回傳包含新 ID 的物件
	 */
	@Transactional
	public UserSecurity createDefaultSecurity(String uid) {
		UserSecurity entity = UserSecurity.builder().firebaseUid(uid).countryCode("TW") // 預設市場
				.currency("TWD") // 預設幣別
				.exchangeRate(BigDecimal.ONE) // 匯率 1.0
				.marketValue(BigDecimal.ZERO) // 市值 0
				.realizedPnl(BigDecimal.ZERO) // 損益 0
				.build();

		// 執行 Insert，MyBatis 會自動將生成的 ID 填回 entity 物件
		userSecurityMapper.insert(entity);

		return entity;
	}

	/**
	 * 3. 刪除部位 (DELETE) 必須同時驗證 id 與 uid，確保只能刪除自己的資料
	 */
	@Transactional
	public void deleteSecurity(String uid, Long id) {
		int rows = userSecurityMapper.deleteById(id, uid);

		if (rows == 0) {
			throw new RuntimeException("Delete failed: Record not found or permission denied.");
		}
	}

	/**
	 * 4. 更新部位 (PUT) 使用 BeanUtils 複製前端傳來的數值，並補上安全驗證用的 ID 與 UID
	 */
	@Transactional
	public void updateSecurityRecord(String uid, Long id, UserSecurityUpdateReq req) {
		// 1. 建立空物件
		UserSecurity entity = new UserSecurity();

		// 2. 複製前端傳來的屬性 (countryCode, currency, marketValue...)
		BeanUtils.copyProperties(req, entity);

		// 3. ❌ 關鍵步驟：手動補上 ID 與 UID
		// 如果沒補這兩行，Mapper 的 SQL (WHERE id = ? AND firebase_uid = ?) 會因為參數是 null 而更新失敗
		entity.setId(id);
		entity.setFirebaseUid(uid);

		// 4. 執行更新
		int rows = userSecurityMapper.updateById(entity);

		if (rows == 0) {
			throw new RuntimeException("Update failed: Record not found or permission denied.");
		}
	}
}