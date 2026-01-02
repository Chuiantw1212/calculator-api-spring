package com.en_chu.calculator_api_spring.model;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserCreditCardDto extends BaseDto {

	@Schema(description = "卡片名稱 (若為空，後端預設為 '新信用卡')", example = "富邦 J 卡")
    private String name;

    @Schema(description = "發卡銀行 (若為空，後端預設為 '未設定銀行')", example = "台北富邦")
    private String bankName;

    @Schema(description = "用途分類 (若為空，後端預設為 'daily')", example = "daily")
    private String usageType;

    @Schema(description = "每月試算支出 (若為空，後端預設為 0)", example = "15000")
    @Min(value = 0, message = "支出金額不能為負數")
    private BigDecimal estimatedMonthlyExpense;
}