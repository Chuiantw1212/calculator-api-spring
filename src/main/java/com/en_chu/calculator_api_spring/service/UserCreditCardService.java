package com.en_chu.calculator_api_spring.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
	 *
	 * @param firebaseUid 使用者 ID
	 * @return 信用卡列表
	 */
	public List<UserCreditCard> getCards(String firebaseUid) {
		return userCreditCardMapper.selectByUid(firebaseUid);
	}

	/**
	 * 取得單張信用卡詳情 (含權限檢查)
	 *
	 * @param id          信用卡 ID
	 * @param firebaseUid 使用者 ID
	 * @return 信用卡實體
	 */
	public UserCreditCard getCard(Long id, String firebaseUid) {
		UserCreditCard card = userCreditCardMapper.selectById(id, firebaseUid);
		if (card == null) {
			// 實務上建議拋出自定義 Exception (如 ResourceNotFoundException)
			throw new RuntimeException("找不到該信用卡或無權限存取");
		}
		return card;
	}

	/**
	 * 新增信用卡 (包含預設值邏輯)
	 * <p>
	 * 前端可只傳部分欄位，後端會自動補齊預設值： - Name: "新信用卡" - Bank: "未設定銀行" - UsageType: "daily" -
	 * Expense: 0
	 *
	 * @param dto         前端傳入資料
	 * @param firebaseUid 使用者 ID
	 * @return 建立後的完整實體
	 */
	@Transactional(rollbackFor = Exception.class)
	public UserCreditCard createCard(UserCreditCardDto dto, String firebaseUid) {
		UserCreditCard entity = new UserCreditCard();

		// 1. 複製前端傳來的屬性
		BeanUtils.copyProperties(dto, entity);

		// 2. 補上系統必要資訊
		entity.setFirebaseUid(firebaseUid);

		// 3. --- 設定後端預設值 (Default Values) ---

		// 若名稱為空 -> "新信用卡"
		if (!StringUtils.hasText(entity.getName())) {
			entity.setName("新信用卡");
		}

		// 若銀行為空 -> "未設定銀行"
		if (!StringUtils.hasText(entity.getBankName())) {
			entity.setBankName("未設定銀行");
		}

		// 若用途為空 -> "daily" (對應 opt_credit_card_usage_type 的日常生活)
		if (!StringUtils.hasText(entity.getUsageType())) {
			entity.setUsageType("daily");
		}

		// 若金額為空 -> 0
		if (entity.getEstimatedMonthlyExpense() == null) {
			entity.setEstimatedMonthlyExpense(BigDecimal.ZERO);
		}

		// 4. 寫入資料庫 (MyBatis 會將產生的 ID 寫回 entity)
		userCreditCardMapper.insert(entity);

		log.info("User [{}] created credit card: ID={}, Name={}", firebaseUid, entity.getId(), entity.getName());

		// 5. 回傳完整實體
		return entity;
	}

	/**
	 * 更新信用卡 僅更新 DTO 中有值的欄位 (MyBatis XML <if> 判斷)
	 *
	 * @param dto         更新資料 (需含 ID)
	 * @param firebaseUid 使用者 ID
	 * @return 更新後的完整實體
	 */
	@Transactional(rollbackFor = Exception.class)
	public UserCreditCard updateCard(UserCreditCardDto dto, String firebaseUid) {
		// 1. 先檢查是否存在 (避免無效更新或權限錯誤)
		UserCreditCard existing = userCreditCardMapper.selectById(dto.getId(), firebaseUid);
		if (existing == null) {
			throw new RuntimeException("無法更新：找不到該信用卡或無權限");
		}

		// 2. 準備更新用的實體
		UserCreditCard updateEntity = new UserCreditCard();
		BeanUtils.copyProperties(dto, updateEntity);

		// 確保 ID 和 UID 正確 (防止前端竄改)
		updateEntity.setId(dto.getId());
		updateEntity.setFirebaseUid(firebaseUid);

		// 3. 執行更新
		// 注意：DTO 中為 null 的欄位，BeanUtils 會拷貝成 null 給 updateEntity
		// MyBatis XML 中的 <if test="val != null"> 會忽略這些 null 欄位，達到 Partial Update 效果
		userCreditCardMapper.update(updateEntity);

		log.info("User [{}] updated credit card: ID={}", firebaseUid, dto.getId());

		// 4. 回傳更新後的完整資料 (重新查詢以確保時間戳記等欄位最新)
		return userCreditCardMapper.selectById(dto.getId(), firebaseUid);
	}

	/**
	 * 刪除信用卡
	 *
	 * @param id          信用卡 ID
	 * @param firebaseUid 使用者 ID
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