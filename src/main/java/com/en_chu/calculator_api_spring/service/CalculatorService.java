package com.en_chu.calculator_api_spring.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.en_chu.calculator_api_spring.mapper.CalculationMapper; // 引入 MyBatis Mapper
import com.en_chu.calculator_api_spring.model.CompoundInterestRequest;

@Service
public class CalculatorService {

    @Autowired
    private CalculationMapper calculationMapper; // 注入 MyBatis 介面

    public BigDecimal calculateCompoundInterest(CompoundInterestRequest request) {
        // 1. 原本的商業邏輯 (計算複利)
        // 這裡為了簡單，我們假設你之前的邏輯是這樣 (或是保留你原本寫好的算式)
        BigDecimal principal = request.getPrincipal();
        BigDecimal rate = request.getRate();
        int years = request.getYears();
        
        // 複利公式: 本金 * (1 + 利率)^年分
        BigDecimal onePlusRate = BigDecimal.ONE.add(rate);
        BigDecimal result = principal.multiply(onePlusRate.pow(years));
        
        // 2. [新增] 將結果存回 request 物件，準備存檔
        request.setResult(result);

        // 3. [新增] 呼叫 MyBatis 存入資料庫
        // 這行程式碼執行下去，資料就會飛到 Neon 雲端了！
        calculationMapper.insertRecord(request);

        return result;
    }
}