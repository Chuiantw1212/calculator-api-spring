package com.en_chu.calculator_api_spring.model;

import lombok.Data; // 假設你有用 Lombok，沒有的話就手寫 Getter/Setter

@Data
public class UserFullDataRes {
	// 1. 基本帳號資訊 (從 Users table 來)
	private Long userId;
	private String email;

	// 2. 個人檔案 (從 UserProfile table 來)
	private UserProfileRes profile;

	// 3. 職涯資料 (從 UserCareer table 來)
	// 假設你已經有了 UserCareerRes，沒有的話要建一個
	private UserCareerRes career;
}