package com.en_chu.calculator_api_spring.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true) // 2. 比較時包含父類別
public class UserProfile  extends UserBaseEntity {

	// 1. 內部主鍵 (DB Primary Key)
	private Long id;

	// 2. 外部身分證 (Firebase Auth UID)
	private String firebaseUid;

	// 3. 業務資料欄位
	private LocalDate birthDate;
	private String gender;
	private Integer currentAge;
	private Integer lifeExpectancy;
	private Integer marriageYear;
	private String careerInsuranceType;
	private String biography;

	// 4. 系統稽核欄位 (Audit Fields)
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}