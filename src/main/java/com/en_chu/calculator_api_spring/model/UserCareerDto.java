package com.en_chu.calculator_api_spring.model;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "更新職涯收入請求物件")
public class UserCareerDto {

	@Schema(description = "資料 ID (新增時可為空，更新時必填)", example = "1")
	private Long id;

	@NotNull(message = "本薪不能為空")
	@Min(value = 0, message = "本薪不能為負數")
	@Schema(description = "本薪", example = "50000")
	private BigDecimal baseSalary;

	@Schema(description = "其他津貼", example = "2400")
	private BigDecimal otherAllowance;

	@Schema(description = "勞保費", example = "1100")
	private BigDecimal laborInsurance;

	@Schema(description = "健保費", example = "800")
	private BigDecimal healthInsurance;

	@Schema(description = "其他扣項", example = "100")
	private BigDecimal otherDeduction;

	// 這裡通常建議前端傳 Rate 或 Amount 擇一計算，但如果你資料庫要同時存，就都開放
	@Schema(description = "勞退自提比率 (%)", example = "6.0")
	private BigDecimal pensionRate;

	@Schema(description = "勞退自提金額", example = "3000")
	private BigDecimal pensionAmount;

	@Schema(description = "員工認股扣款", example = "2000")
	private BigDecimal stockDeduction;

	@Schema(description = "公司相對提撥", example = "1000")
	private BigDecimal stockCompanyMatch;
}