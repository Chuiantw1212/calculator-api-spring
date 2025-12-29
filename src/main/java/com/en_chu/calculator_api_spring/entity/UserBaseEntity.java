package com.en_chu.calculator_api_spring.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class UserBaseEntity {

	// ==========================================
	// 1. 資料庫主鍵 (Primary Key)
	// ==========================================
	/**
	 * 每筆資料的唯一識別碼 (例如：Portfolio ID, Career ID) 這是給前端用來修改(PUT)或刪除(DELETE)特定資料用的
	 */
	@JsonProperty(access = Access.READ_ONLY)
	private Long id;

	// ==========================================
	// 2. 資料歸屬者 (Owner)
	// ==========================================
	/**
	 * Firebase UID 標示這筆資料屬於哪個使用者。
	 * 
	 * @JsonIgnore: 確保這個欄位永遠不會傳給前端 (資安考量)
	 */
	@JsonIgnore
	private String firebaseUid;

	// ==========================================
	// 3. 稽核時間 (Audit Timestamps)
	// ==========================================

	/**
	 * 建立時間
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt;

	/**
	 * 更新時間
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedAt;
}