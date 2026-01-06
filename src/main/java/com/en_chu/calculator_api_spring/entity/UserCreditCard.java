package com.en_chu.calculator_api_spring.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserCreditCard extends UserBaseEntity {
    private String firebaseUid;

    /**
     * 卡片名稱 (e.g. 玉山 U Bear)
     */
    private String name;

    /**
     * 扣款帳戶 (e.g. 台新 Richart)
     * 對應 JSON: deductionAccount
     */
    private String deductionAccount;

    /**
     * 用途分類代碼 (e.g. online, daily)
     * 對應 JSON: usageType
     */
    private String usageType;

    /**
     * 卡片存放位置 (e.g. wallet, home, digital)
     * 對應 JSON: storageLocation
     */
    private String storageLocation;

    /**
     * 平均月開支 (每月試算支出)
     * 對應 JSON: averageMonthlyExpense
     */
    private BigDecimal averageMonthlyExpense;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}