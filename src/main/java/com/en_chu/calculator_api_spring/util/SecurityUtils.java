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
}