package com.en_chu.calculator_api_spring.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.en_chu.calculator_api_spring.entity.UserBusiness;
import com.en_chu.calculator_api_spring.mapper.UserBusinessMapper;
import com.en_chu.calculator_api_spring.model.UserBusinessDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserBusinessService {

	private final UserBusinessMapper mapper;

	/**
	 * 1. 取得列表
	 */
	public List<UserBusinessDto> getList(String uid) {
		return mapper.selectByUid(uid).stream().map(this::convertToDto).collect(Collectors.toList());
	}

	/**
	 * 2. 取得單筆 (用於 Update 後回傳最新狀態)
	 */
	public UserBusinessDto getById(String uid, Long id) {
		UserBusiness entity = mapper.selectByIdAndUid(id, uid)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到該事業項目"));
		return convertToDto(entity);
	}

	/**
	 * 3. 新增事業 這裡不需像 RealEstate 那樣計算總價，也不需預設值， 因為 Controller 的 @Valid 已經確保資料完整性。
	 */
	@Transactional
	public UserBusinessDto create(String uid, UserBusinessDto req) {
		// 1. DTO -> Entity
		UserBusiness entity = new UserBusiness();
		BeanUtils.copyProperties(req, entity);

		// 2. 注入 UID (安全防護)
		entity.setFirebaseUid(uid);

		// 3. 寫入資料庫 (ID 會自動生成並回填到 entity)
		mapper.insert(entity);
		log.info("Created business id={} for user={}", entity.getId(), uid);

		// 4. 回傳 DTO
		return convertToDto(entity);
	}

	/**
	 * 4. 更新事業
	 */
	@Transactional
	public UserBusinessDto update(String uid, Long id, UserBusinessDto req) {
		// 1. 準備更新物件
		UserBusiness updateEntity = new UserBusiness();
		BeanUtils.copyProperties(req, updateEntity);

		// 2. 補上 ID 與 UID (Where 條件)
		updateEntity.setId(id);
		updateEntity.setFirebaseUid(uid);

		// 3. 執行更新
		int rows = mapper.update(updateEntity);

		if (rows == 0) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "更新失敗：找不到該資料或無權限");
		}

		// 4. 重新查詢最新狀態並回傳
		return getById(uid, id);
	}

	/**
	 * 5. 刪除事業
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

	private UserBusinessDto convertToDto(UserBusiness entity) {
		UserBusinessDto dto = new UserBusinessDto();
		BeanUtils.copyProperties(entity, dto);
		return dto;
	}
}