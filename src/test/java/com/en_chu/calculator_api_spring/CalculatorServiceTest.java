package com.en_chu.calculator_api_spring;

import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions; // JUnit 5 的斷言工具
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.en_chu.calculator_api_spring.model.CompoundInterestRequest;
import com.en_chu.calculator_api_spring.service.CalculatorService;

@SpringBootTest // 1. 告訴 Spring 這是一個測試，請幫我準備環境
public class CalculatorServiceTest {

    @Autowired // 2. 自動把寫好的 Service 注入進來
    private CalculatorService calculatorService;

    @Test // 3. 這是一個測試案例
    public void testCalculateCompoundInterest() {
        // --- 準備資料 (Arrange) ---
        CompoundInterestRequest request = new CompoundInterestRequest();
        request.setPrincipal(new BigDecimal("100000")); // 本金
        request.setRate(new BigDecimal("0.05"));        // 利率 5%
        request.setYears(10);                           // 10年

        // --- 執行動作 (Act) ---
        BigDecimal result = calculatorService.calculateCompoundInterest(request);

        // --- 驗證結果 (Assert) ---
        // 我們預期結果是 162889.46
        BigDecimal expected = new BigDecimal("162889.46");
        
        // 金融業鐵律：BigDecimal 比較要用 compareTo，不能用 equals
        // result.compareTo(expected) == 0 代表數值相等
        Assertions.assertEquals(0, result.compareTo(expected), "計算結果應該要等於 162,889.46");
        
        System.out.println("測試成功！計算結果為: " + result);
    }
    
    @Test
    public void testNegativePrincipal() {
        // 測試例外狀況：本金為負數
        CompoundInterestRequest request = new CompoundInterestRequest();
        request.setPrincipal(new BigDecimal("-100")); 
        request.setRate(new BigDecimal("0.05"));
        request.setYears(1);

        // 預期這行程式碼會拋出 IllegalArgumentException
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            calculatorService.calculateCompoundInterest(request);
        });
        
        System.out.println("測試成功！成功攔截負數本金。");
    }
}