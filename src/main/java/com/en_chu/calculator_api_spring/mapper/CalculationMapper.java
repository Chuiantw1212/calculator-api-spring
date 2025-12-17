package com.en_chu.calculator_api_spring.mapper; // 👈 這行是關鍵

import com.en_chu.calculator_api_spring.model.CompoundInterestRequest;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CalculationMapper {
	// 定義方法：新增紀錄
	void insertRecord(CompoundInterestRequest record);

	// 定義方法：查詢紀錄
	List<CompoundInterestRequest> findAllRecords();
}