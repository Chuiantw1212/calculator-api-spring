package com.en_chu.calculator_api_spring.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.en_chu.calculator_api_spring.entity.UserCreditCard;
import com.en_chu.calculator_api_spring.mapper.UserCreditCardMapper;
import com.en_chu.calculator_api_spring.model.UserCreditCardDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCreditCardService {

	private final UserCreditCardMapper userCreditCardMapper;

	/**
	 * 取得使用者的所有信用卡列表
	 */
	public List<UserCreditCard> getCards(String firebaseUid) {
		return userCreditCardMapper.selectByUid(firebaseUid);
	}

	/**
	 * 取得單張信用卡詳情
	 */
	public UserCreditCard getCard(Long id, String firebaseUid) {
		UserCreditCard card = userCreditCardMapper.selectById(id, firebaseUid);
		if (card == null) {
			throw new RuntimeException("找不到該信用卡或無權限存取");
		}
		return card;
	}

	/**
	 * 新增信用卡 (後端生成預設值)
	 * <p>
	 * 此方法**不複製**前端 DTO 的屬性。 無論前端傳什麼，這裡都會強制建立一張帶有預設值的全新卡片。
	 *
	 * @param dto         前端傳入資料 (僅作為觸發，不使用其內容)
	 * @param firebaseUid 使用者 ID
	 * @return 建立後的完整實體 (含預設值)
	 */
	@Transactional(rollbackFor = Exception.class)
	public UserCreditCard createCard(UserCreditCardDto dto, String firebaseUid) {
		UserCreditCard entity = new UserCreditCard();

		// 1. 設定系統欄位
		entity.setFirebaseUid(firebaseUid);

		// 2. --- 強制設定後端預設值 (Default Values) ---
		// 不複製 DTO，直接給定初始狀態

		entity.setName("");
		entity.setDeductionAccount("");
		entity.setUsageType("daily"); // 對應 opt_credit_card_usage_type
		entity.setStorageLocation("wallet"); // 預設在錢包
		entity.setAverageMonthlyExpense(BigDecimal.ZERO);

		// 3. 寫入資料庫
		userCreditCardMapper.insert(entity);

		log.info("User [{}] initialized new credit card: ID={}, Name={}", firebaseUid, entity.getId(),
				entity.getName());

		return entity;
	}

	/**
	 * 更新信用卡 這裡 **會** 複製前端屬性，支援部分更新
	 */
	@Transactional(rollbackFor = Exception.class)
	public UserCreditCard updateCard(UserCreditCardDto dto, String firebaseUid) {
		// 1. 檢查是否存在
		UserCreditCard existing = userCreditCardMapper.selectById(dto.getId(), firebaseUid);
		if (existing == null) {
			throw new RuntimeException("無法更新：找不到該信用卡或無權限");
		}

		// 2. 準備更新實體
		UserCreditCard updateEntity = new UserCreditCard();

		// 這裡使用 copyProperties，因為是「編輯」模式，需套用前端輸入的值
		BeanUtils.copyProperties(dto, updateEntity);

		// 3. 確保 ID 與 UID 安全
		updateEntity.setId(dto.getId());
		updateEntity.setFirebaseUid(firebaseUid);

		// 4. 執行更新 (MyBatis XML 會過濾 null 值，只更新有傳的欄位)
		userCreditCardMapper.update(updateEntity);

		log.info("User [{}] updated credit card: ID={}", firebaseUid, dto.getId());

		return userCreditCardMapper.selectById(dto.getId(), firebaseUid);
	}

	/**
	 * 刪除信用卡
	 */
	@Transactional(rollbackFor = Exception.class)
	public void deleteCard(Long id, String firebaseUid) {
		int rows = userCreditCardMapper.delete(id, firebaseUid);
		if (rows == 0) {
			throw new RuntimeException("刪除失敗：找不到該信用卡或無權限");
		}
		log.info("User [{}] deleted credit card: ID={}", firebaseUid, id);
	}
}