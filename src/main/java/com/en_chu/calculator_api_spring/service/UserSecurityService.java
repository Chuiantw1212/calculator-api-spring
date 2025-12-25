package com.en_chu.calculator_api_spring.service;

import org.springframework.beans.BeanUtils; // 1. 引用這一個
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

	@Transactional
	public void updateSecurityRecord(String uid, Long id, UserSecurityUpdateReq req) {

		// 1. 建立一個空的 Entity
		UserSecurity entity = new UserSecurity();

		// 2. 複製屬性 (來源, 目標)
		// 這會把 req 裡的 countryCode, currency, marketValue... 全部複製過去
		// 前提是：欄位名稱(Name) 和 類型(Type) 必須完全一樣
		BeanUtils.copyProperties(req, entity);

		// 3. 關鍵：手動補上 ID 和 UID (因為 Req 裡面沒有，或是不應該由 Req 決定)
		entity.setId(id);
		entity.setFirebaseUid(uid);

		// 4. 執行更新
		int rowsAffected = userSecurityMapper.updateById(entity);

		if (rowsAffected == 0) {
			throw new RuntimeException("Update failed: Record not found or permission denied.");
		}
	}
}