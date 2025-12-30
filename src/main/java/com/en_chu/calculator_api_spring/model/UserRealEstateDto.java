package com.en_chu.calculator_api_spring.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true) // ✅ 務必加上：比對時包含父類別 ID
public class UserRealEstateDto extends BaseDto {

	// ==========================================
	// 基本資料
	// ==========================================

	@Schema(description = "物件名稱", example = "板橋自用宅", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "物件名稱不能為空")
	private String name;

	@Schema(description = "屋齡 (年)", example = "10")
	@Min(value = 0, message = "屋齡不能小於 0")
	private Integer age;

	// ==========================================
	// 價值與坪數 (計算基礎)
	// ==========================================

	@Schema(description = "權狀坪數", example = "35.5", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "坪數必填")
	@DecimalMin(value = "0.0", inclusive = false, message = "坪數必須大於 0")
	private BigDecimal size;

	@Schema(description = "單價 (萬/坪)", example = "60.5", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "單價必填")
	@DecimalMin(value = "0.0", message = "單價不能小於 0")
	private BigDecimal pricePerPing;

	// 🔒 系統計算欄位：前端傳了也沒用，後端會重算
	@Schema(description = "總價 (系統自動計算：單價 * 坪數)", example = "2147.75", accessMode = Schema.AccessMode.READ_ONLY)
	@JsonProperty(access = Access.READ_ONLY)
	private BigDecimal totalPrice;

	// ==========================================
	// 稅務與貸款
	// ==========================================

	@Schema(description = "公告/評定現值 (稅基)", example = "800.0")
	@DecimalMin(value = "0.0", message = "公告現值不能小於 0")
	private BigDecimal assessedValue;

	@Schema(description = "預估持有稅率 (%)", example = "1.2")
	@DecimalMin(value = "0.0", message = "稅率不能小於 0")
	private BigDecimal holdingTaxRate;

	@Schema(description = "銀行貸款餘額 (萬)", example = "1000.0")
	@DecimalMin(value = "0.0", message = "貸款餘額不能小於 0")
	private BigDecimal loanAmount;

	@Schema(description = "年利率 (%)", example = "2.1")
	@DecimalMin(value = "0.0", message = "利率不能小於 0")
	private BigDecimal interestRate;

	// ==========================================
	// 使用狀態 (Enum 驗證)
	// ==========================================

	@Schema(description = "用途狀態 (self:自用, rent:出租, vacant:閒置)", example = "self", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "用途狀態必填")
	// ✅ 使用 RegEx 強制限制只能輸入這三個字串，防止亂碼
	@Pattern(regexp = "^(self|rent|vacant)$", message = "用途狀態必須為: self, rent, 或 vacant")
	private String usageType;

	@Schema(description = "月租金收入 (僅在 rent 狀態有效)", example = "25000")
	@DecimalMin(value = "0.0", message = "租金不能小於 0")
	private BigDecimal monthlyRent;
}