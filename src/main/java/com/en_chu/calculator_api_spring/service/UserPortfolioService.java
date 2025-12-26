package com.en_chu.calculator_api_spring.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.en_chu.calculator_api_spring.entity.UserPortfolio;
import com.en_chu.calculator_api_spring.mapper.UserPortfolioMapper; // ✅ 改名
import com.en_chu.calculator_api_spring.model.UserPortfolioUpdateReq; // ✅ 改名

@Service
public class UserPortfolioService {

	@Autowired
	private UserPortfolioMapper userPortfolioMapper; // ✅ 改名

	/**
	 * 1. 查詢該用戶的所有投資部位 (Get Portfolio)
	 */
	public List<UserPortfolio> getUserPortfolio(String uid) { // ✅ 改名
		return userPortfolioMapper.selectListByUid(uid);
	}

	/**
	 * 2. 新增預設部位 (Create Position)
	 */
	@Transactional
	public UserPortfolio createDefaultPosition(String uid) { // ✅ 改名
		// 1. 使用 Builder 建構物件
		UserPortfolio entity = UserPortfolio.builder().firebaseUid(uid) // 設定父類別欄位
				.countryCode("TW").currency("TWD").exchangeRate(BigDecimal.ONE).marketValue(BigDecimal.ZERO)
				.realizedPnl(BigDecimal.ZERO).build();

		// 2. 寫入資料庫
		userPortfolioMapper.insert(entity); // ✅ 改用新的 Mapper

		// 3. 回傳 (UserBaseEntity 會自動處理 @JsonIgnore)
		return entity;
	}

	/**
	 * 3. 刪除部位 (Delete Position)
	 */
	@Transactional
	public void deletePosition(String uid, Long id) { // ✅ 改名
		int rows = userPortfolioMapper.deleteById(id, uid); // ✅ 改用新的 Mapper

		if (rows == 0) {
			throw new RuntimeException("Delete failed: Record not found or permission denied.");
		}
	}

	/**
	 * 4. 更新部位 (Update Position)
	 */
	@Transactional
	public void updatePosition(String uid, Long id, UserPortfolioUpdateReq req) { // ✅ 改名 (包含參數型別)
		// 1. 建立空物件
		UserPortfolio entity = new UserPortfolio();

		// 2. 複製前端傳來的屬性
		BeanUtils.copyProperties(req, entity);

		// 3. 關鍵步驟：手動補上 ID 與 UID
		entity.setId(id);
		entity.setFirebaseUid(uid);

		// 4. 執行更新
		int rows = userPortfolioMapper.updateById(entity); // ✅ 改用新的 Mapper

		if (rows == 0) {
			throw new RuntimeException("Update failed: Record not found or permission denied.");
		}
	}
}