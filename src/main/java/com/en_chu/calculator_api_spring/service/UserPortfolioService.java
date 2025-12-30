package com.en_chu.calculator_api_spring.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.en_chu.calculator_api_spring.entity.UserPortfolio;
import com.en_chu.calculator_api_spring.mapper.UserPortfolioMapper;
import com.en_chu.calculator_api_spring.model.UserPortfolioDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor // 1. 取代 @Autowired，自動注入 final 欄位
public class UserPortfolioService {

	private final UserPortfolioMapper mapper;

	/**
	 * 1. 查詢該用戶的所有投資部位 (Get List)
	 */
	public List<UserPortfolioDto> getList(String uid) {
		return mapper.selectByUid(uid).stream() // 假設 Mapper 方法名已統一為 selectByUid
				.map(this::convertToDto) // 2. 使用抽出的小工具轉換
				.collect(Collectors.toList());
	}

	/**
	 * 2. 查詢單筆部位 (Get One) - 為了 Update 回傳使用
	 */
	public UserPortfolioDto getById(String uid, Long id) {
		UserPortfolio entity = mapper.selectByIdAndUid(id, uid)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到該投資部位"));
		return convertToDto(entity);
	}

	/**
	 * 3. 新增部位 (Create) 修改建議：原本的 createDefaultPosition 寫死了數值， 建議改成接收 DTO，讓前端傳入 "TW",
	 * "TWD" 等初始值，更具彈性。
	 */
	@Transactional
	public UserPortfolioDto create(String uid, UserPortfolioDto req) {
		// Entity 建構
		UserPortfolio entity = new UserPortfolio();
		BeanUtils.copyProperties(req, entity);

		// 強制注入安全欄位
		entity.setFirebaseUid(uid);

		// 寫入 DB
		mapper.insert(entity);
		log.info("Created portfolio id={} for user={}", entity.getId(), uid);

		// 回傳 DTO
		return convertToDto(entity);
	}

	/**
	 * 4. 更新部位 (Update)
	 */
	@Transactional
	public UserPortfolioDto update(String uid, Long id, UserPortfolioDto req) {
		// 準備更新物件
		UserPortfolio updateEntity = new UserPortfolio();
		BeanUtils.copyProperties(req, updateEntity);

		// 補上 ID 與 UID (Where 條件)
		updateEntity.setId(id);
		updateEntity.setFirebaseUid(uid);

		// 執行更新
		int rows = mapper.update(updateEntity); // 假設 Mapper 方法名已統一為 update

		if (rows == 0) {
			// 使用標準 HTTP 404 異常
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "更新失敗：找不到該資料或無權限");
		}

		// 重新查詢最新狀態 (包含 Trigger 更新的時間) 並回傳
		return getById(uid, id);
	}

	/**
	 * 5. 刪除部位 (Delete)
	 */
	@Transactional
	public void delete(String uid, Long id) {
		int rows = mapper.deleteByIdAndUid(id, uid); // 假設 Mapper 方法名已統一

		if (rows == 0) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "刪除失敗：找不到該資料或無權限");
		}
	}

	// ==========================================
	// Private Helper Methods (你的要求)
	// ==========================================

	private UserPortfolioDto convertToDto(UserPortfolio entity) {
		UserPortfolioDto dto = new UserPortfolioDto();
		BeanUtils.copyProperties(entity, dto);
		return dto;
	}
}