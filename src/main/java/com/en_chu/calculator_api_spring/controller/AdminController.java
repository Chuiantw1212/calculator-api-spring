package com.en_chu.calculator_api_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.en_chu.calculator_api_spring.service.FirebaseSeedingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/admin/sync")
@Tag(name = "Admin Sync", description = "後台資料同步工具")
public class AdminController {

	@Autowired
	private FirebaseSeedingService seedingService;

	@Operation(summary = "同步一般設定檔 (Metadata)", description = "同步 init-data 資料夾下的設定檔，但不包含生命表。")
	@PostMapping("/metadata")
	public ResponseEntity<String> syncMetadata() {
		seedingService.syncMetadataConfigs();
		return ResponseEntity.ok("Metadata configs synced successfully.");
	}

	@Operation(summary = "同步生命表 (Life Table)", description = "專門處理 opt_life_table，將其拆分為獨立文件寫入 Firestore。此操作較耗時。")
	@PostMapping("/life-table")
	public ResponseEntity<String> syncLifeTable() {
		seedingService.syncLifeTable();
		return ResponseEntity.ok("Life Table data sync started/completed.");
	}
}