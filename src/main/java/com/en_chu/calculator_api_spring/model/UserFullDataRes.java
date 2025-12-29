package com.en_chu.calculator_api_spring.model;

import java.util.List;

import lombok.Data; // 假設你有用 Lombok，沒有的話就手寫 Getter/Setter
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserFullDataRes  extends BaseDto {	
	private String email;

	// 2. 個人檔案 (從 UserProfile table 來)
	private UserProfileDto profile;

	// 3. 職涯資料 (從 UserCareer table 來)
	// 假設你已經有了 UserCareerRes，沒有的話要建一個
	private UserCareerDto career;

	// ✅ 必須新增這個欄位，變數名稱要跟 XML 裡的 property="portfolios" 一樣
	private List<UserPortfolioDto> portfolios;
}