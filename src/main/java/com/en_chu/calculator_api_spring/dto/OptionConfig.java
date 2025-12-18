package com.en_chu.calculator_api_spring.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class OptionConfig {
	private String id; // 對應 JSON 的 "id" -> 用作 Document ID
	private String name; // 對應 JSON 的 "name" -> 用作描述
	private List<Map<String, Object>> list; // 對應 JSON 的 "list" -> 選單陣列
}