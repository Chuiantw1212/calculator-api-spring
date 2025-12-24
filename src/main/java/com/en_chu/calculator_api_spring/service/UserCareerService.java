package com.en_chu.calculator_api_spring.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.en_chu.calculator_api_spring.mapper.UserCareerMapper;
import com.en_chu.calculator_api_spring.model.UserCareerReq;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserCareerService {

	@Autowired
	private UserCareerMapper userCareerMapper;

	/**
	 * 更新或建立職涯收入資料 * @param req 前端傳來的資料 (包含薪資、保險等)
	 * 
	 * @param userId 當前使用者的 ID (用於新增時關聯)
	 */
	@Transactional
	public void updateCareer(UserCareerReq req, Long userId) {

		// 1. 業務邏輯驗證 (Business Validation)
		validateCareerRules(req);

		// 2. 判斷是新增還是更新
		// req.getId() 是由 UserService 查詢後填入的
		// 如果是 null，代表該用戶在 user_careers 表還沒有資料
		if (req.getId() == null) {
			log.info("Creating new career profile for User ID: {}", userId);
			userCareerMapper.insertCareer(req, userId);
		} else {
			log.info("Updating existing career profile ID: {}", req.getId());
			userCareerMapper.updateCareer(req);
		}
	}

	/**
	 * 驗證職涯資料的合理性 這裡可以放置複雜的理財檢核邏輯
	 */
	private void validateCareerRules(UserCareerReq req) {
		// 範例規則 1: 勞退自提通常不會超過 100% (雖然法律是 6%，但系統或許允許自定義)
		if (req.getPensionRate() != null && req.getPensionRate().compareTo(new BigDecimal("100")) > 0) {
			throw new IllegalArgumentException("勞退提撥率設定不合理 (>100%)");
		}

		// 範例規則 2: 勞保/健保費不應該比本薪還高 (除非本薪是 0)
		if (req.getBaseSalary() != null && req.getBaseSalary().compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal insuranceTotal = req.getLaborInsurance().add(req.getHealthInsurance());
			if (insuranceTotal.compareTo(req.getBaseSalary()) > 0) {
				// 這裡可以選擇 throw Exception 或是 log warning
				log.warn("Warning: Insurance costs are higher than base salary.");
			}
		}

		// 範例規則 3: 如果有填本薪，則必須大於等於 0 (雖然 DTO 有 @Min，這裡可做雙重防護)
		if (req.getBaseSalary() != null && req.getBaseSalary().compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("本薪不能為負數");
		}
	}
}