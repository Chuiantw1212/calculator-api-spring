package com.en_chu.calculator_api_spring.model;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "職涯收入詳細設定 (對應前端 CareerProfile)")
public class UserCareerRes {

	@Schema(description = "資料 ID", example = "1")
	private Long id;

	// --- 收入區 ---
	@Schema(description = "本薪 (Base Salary)", example = "50000")
	private BigDecimal baseSalary;

	@Schema(description = "其他津貼/伙食費 (Other Allowance)", example = "2400")
	private BigDecimal otherAllowance;

	// --- 法定扣除額區 ---
	@Schema(description = "勞保費 (Labor Insurance)", example = "1100")
	private BigDecimal laborInsurance;

	@Schema(description = "健保費 (Health Insurance)", example = "800")
	private BigDecimal healthInsurance;

	@Schema(description = "其他扣項 (Other Deduction - 福利金等)", example = "100")
	private BigDecimal otherDeduction;

	// --- 退休金區 (兩者皆存) ---
	@Schema(description = "勞退自提比率 (Pension Rate %)", example = "6.0")
	private BigDecimal pensionRate;

	@Schema(description = "勞退自提金額 (Pension Amount)", example = "3000")
	private BigDecimal pensionAmount;

	// --- 員工認股/投資區 ---
	@Schema(description = "員工認股扣款 (Stock Deduction - 自付額)", example = "2000")
	private BigDecimal stockDeduction;

	@Schema(description = "公司相對提撥/補助 (Stock Company Match)", example = "1000")
	private BigDecimal stockCompanyMatch;
}