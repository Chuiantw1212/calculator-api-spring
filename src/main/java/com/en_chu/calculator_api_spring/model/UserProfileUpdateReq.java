package com.en_chu.calculator_api_spring.model;

import lombok.Data;
import java.time.LocalDate;

/**
 * DTO for updating a user profile.
 */
@Data
public class UserProfileUpdateReq {

    private LocalDate birthDate;
    private String gender;
    private Integer marriageYear;
    private String biography;
    private String careerInsuranceType; // ✅ 補全缺失的欄位
}
