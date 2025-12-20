package com.en_chu.calculator_api_spring.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class SecurityUtils {

	/**
	 * 取得當前登入者的 Firebase UID
	 */
	public static String getCurrentUserUid() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || auth.getPrincipal() == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "找不到身份識別(ID Token)");
		}
		return (String) auth.getPrincipal(); // 在 Filter 裡我們存的是 String uid
	}

	/**
	 * 檢查當前登入者是否有權操作目標 UID
	 * 
	 * @param targetUid 前端傳來的 uid
	 */
	public static void validateUserAccess(String targetUid) {
		String currentUid = getCurrentUserUid();
		if (!currentUid.equals(targetUid)) {
			// 這就是你的 "向開發人員抱怨Q_Q" 的加強版
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "權限不足：您無法修改其他使用者的資料");
		}
	}
}