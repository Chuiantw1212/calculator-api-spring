package com.en_chu.calculator_api_spring.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder; // 1. 必須引入這個

@Data
@SuperBuilder // 2. 這裡一定要是 SuperBuilder，不能是 @Builder
@NoArgsConstructor
@AllArgsConstructor
public abstract class UserBaseEntity { // 建議加上 abstract，因為這類別不會單獨存在

	@JsonIgnore	
	private String firebaseUid;
}