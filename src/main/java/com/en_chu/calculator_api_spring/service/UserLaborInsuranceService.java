package com.en_chu.calculator_api_spring.service;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.en_chu.calculator_api_spring.entity.UserLaborInsurance;
import com.en_chu.calculator_api_spring.mapper.UserLaborInsuranceMapper;
import com.en_chu.calculator_api_spring.model.UserLaborInsuranceDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserLaborInsuranceService {

	private final UserLaborInsuranceMapper mapper;

	public UserLaborInsuranceDto getLaborInsurance(String uid) {
		UserLaborInsurance entity = mapper.selectByUid(uid);
		if (entity == null)
			return null;

		UserLaborInsuranceDto dto = new UserLaborInsuranceDto();
		BeanUtils.copyProperties(entity, dto);
		return dto;
	}

	@Transactional
	public UserLaborInsuranceDto updateLaborInsurance(String uid, UserLaborInsuranceDto req) {
		log.info("🔄 [LaborInsurance] 更新資料: UID={}", uid);

		UserLaborInsurance entity = new UserLaborInsurance();
		BeanUtils.copyProperties(req, entity);
		entity.setFirebaseUid(uid);

		int rows = mapper.updateByUid(entity);
		if (rows == 0) {
			log.info("✨ [LaborInsurance] 建立新資料: UID={}", uid);
			mapper.insert(entity);
		}

		return getLaborInsurance(uid);
	}
}