package com.en_chu.calculator_api_spring.model;

import lombok.Data;

import java.time.LocalDate;

/**
 * 專門用於更新使用者個人資料的請求 DTO。
 * 只包含允許使用者修改的欄位，以防範巨量請求攻擊。
 */
@Data
public class UserProfileUpdateReq {

    private LocalDate birthDate;

    private String gender;

    private Integer marriageYear;

    private String biography;
}
