package com.en_chu.calculator_api_spring.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.en_chu.calculator_api_spring.entity.UserRealEstate;
import com.en_chu.calculator_api_spring.mapper.UserRealEstateMapper;
import com.en_chu.calculator_api_spring.model.UserRealEstateDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRealEstateService {

	private final UserRealEstateMapper mapper;

	/**
	 * 新增房地產
	 */
	@Transactional
	public UserRealEstateDto create(String uid, UserRealEstateDto req) {
		// 1. DTO -> Entity
		UserRealEstate entity = new UserRealEstate();
		BeanUtils.copyProperties(req, entity);

		// 2. 強制注入 UID (資安關鍵)
		entity.setFirebaseUid(uid);

		// 3. 執行商業計算 (總價 = 單價 * 坪數)
		calculateTotalPrice(entity);

		// 4. 寫入資料庫
		mapper.insert(entity);
		log.info("Created real estate id={} for user={}", entity.getId(), uid);

		// 5. Entity -> DTO (回傳包含 ID 與時間的完整物件)
		return convertToDto(entity);
	}

	/**
	 * 取得列表
	 */
	public List<UserRealEstateDto> getList(String uid) {
		return mapper.selectByUid(uid).stream().map(this::convertToDto).collect(Collectors.toList());
	}

	/**
	 * 取得單筆詳細
	 */
	public UserRealEstateDto getById(String uid, Long id) {
		UserRealEstate entity = mapper.selectByIdAndUid(id, uid)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到該房地產資料"));

		return convertToDto(entity);
	}

	/**
	 * 更新房地產
	 */
	@Transactional
	public UserRealEstateDto update(String uid, Long id, UserRealEstateDto req) {
	    // 1. 準備更新用的 Entity
	    UserRealEstate updateEntity = new UserRealEstate();
	    BeanUtils.copyProperties(req, updateEntity);
	    
	    // 2. 補上 ID 與 UID (給 SQL WHERE 用)
	    updateEntity.setId(id);
	    updateEntity.setFirebaseUid(uid);

	    // 3. 重新計算總價
	    calculateTotalPrice(updateEntity);

	    // 4. 執行更新，並直接檢查回傳的行數 (rows)
	    // 這裡直接做掉了「檢查存在性」的工作，省去一次 Query
	    int rows = mapper.update(updateEntity);
	    
	    if (rows == 0) {
	        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到該房地產資料，或無權限修改");
	    }
	    
	    // 5. 再次查詢最新狀態回傳 (因為有 Trigger 更新時間)
	    return getById(uid, id); 
	}

	/**
	 * 刪除房地產
	 */
	@Transactional
	public void delete(String uid, Long id) {
		int rows = mapper.deleteByIdAndUid(id, uid);
		if (rows == 0) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "刪除失敗：找不到該資料或無權限");
		}
	}

	// ==========================================
	// Private Helper Methods
	// ==========================================

	/**
	 * 商業邏輯：計算總價 邏輯：總價 (萬) = 單價 (萬/坪) * 坪數
	 */
	private void calculateTotalPrice(UserRealEstate entity) {
		if (entity.getPricePerPing() != null && entity.getSize() != null) {
			// 使用 BigDecimal 運算避免精度遺失
			BigDecimal total = entity.getPricePerPing().multiply(entity.getSize());
			entity.setTotalPrice(total);
		} else {
			entity.setTotalPrice(BigDecimal.ZERO);
		}
	}

	/**
	 * 工具：Entity 轉 DTO
	 */
	private UserRealEstateDto convertToDto(UserRealEstate entity) {
		UserRealEstateDto dto = new UserRealEstateDto();
		BeanUtils.copyProperties(entity, dto);
		return dto;
	}
}