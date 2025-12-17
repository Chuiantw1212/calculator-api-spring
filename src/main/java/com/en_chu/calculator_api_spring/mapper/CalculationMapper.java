package com.en_chu.calculator_api_spring.mapper; // 再次確認路徑

import org.apache.ibatis.annotations.Mapper; // 👈 這裡要對
import com.en_chu.calculator_api_spring.model.CompoundInterestRequest;
import java.util.List;

@Mapper // 👈 這個絕對不能少
public interface CalculationMapper {
	void insertRecord(CompoundInterestRequest record);

	List<CompoundInterestRequest> findAllRecords();
}