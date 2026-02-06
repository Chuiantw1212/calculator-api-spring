package com.en_chu.calculator_api_spring.controller;

import com.en_chu.calculator_api_spring.service.FirebaseSeedingService;
import com.en_chu.calculator_api_spring.service.StartupDataCleanupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin API", description = "後台管理與資料維護工具")
@RequiredArgsConstructor
public class AdminController {

    private final FirebaseSeedingService seedingService;
    private final StartupDataCleanupService cleanupService;

    @Operation(summary = "同步 Firestore 設定檔 (Metadata)", description = "從本地 init-data 資料夾同步設定檔至 Firestore。")
    @PostMapping("/sync/metadata")
    public ResponseEntity<String> syncMetadata() {
        seedingService.syncMetadataConfigs();
        return ResponseEntity.ok("Metadata configs synced successfully.");
    }

    @Operation(summary = "同步 Firestore 生命表 (Life Table)", description = "從本地 init-data 同步生命表至 Firestore。此操作較耗時。")
    @PostMapping("/sync/life-table")
    public ResponseEntity<String> syncLifeTable() {
        seedingService.syncLifeTable();
        return ResponseEntity.ok("Life Table data sync started/completed.");
    }

    @Operation(summary = "清理孤兒資料", description = "手動觸發，刪除所有在主表 `user_profiles` 中已不存在對應使用者的關聯資料。")
    @PostMapping("/cleanup-orphans")
    public ResponseEntity<String> cleanupOrphanedData() {
        cleanupService.cleanupOrphanedData();
        return ResponseEntity.ok("Orphaned data cleanup process finished.");
    }
}
