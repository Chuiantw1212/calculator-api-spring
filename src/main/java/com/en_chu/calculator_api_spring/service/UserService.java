package com.en_chu.calculator_api_spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.en_chu.calculator_api_spring.mapper.UserMapper;
import com.en_chu.calculator_api_spring.model.UserCareerReq;
import com.en_chu.calculator_api_spring.model.UserFullDataRes;
import com.en_chu.calculator_api_spring.model.UserProfileReq;
import com.en_chu.calculator_api_spring.util.SecurityUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	private UserCareerService userCareerService;

	// ... (getFullUserData 保持原樣) ...

	/**
	 * 更新 Profile 職責：單純委派。底層 Service 會自己去 SecurityContext 拿 UID。
	 */
	@Transactional
	public void updateProfile(UserProfileReq req) {
		// 不需要傳 UID，直接把 Request 丟下去
		userProfileService.updateProfile(req);
	}

	/**
	 * 更新 Career 職責：單純委派。
	 */
	@Transactional
	public void updateCareer(UserCareerReq req) {
		// 不需要傳 UID，直接把 Request 丟下去
		userCareerService.updateCareer(req);
	}

	// ... (getFullUserData / syncUser 等方法保持原樣) ...
	public UserFullDataRes getFullUserData() {
		String uid = SecurityUtils.getCurrentUserUid();
		return userMapper.selectFullUserDataByFirebaseUid(uid);
	}
}