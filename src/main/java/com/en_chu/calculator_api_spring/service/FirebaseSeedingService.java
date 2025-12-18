package com.en_chu.calculator_api_spring.service;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import com.en_chu.calculator_api_spring.dto.OptionConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.Firestore;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FirebaseSeedingService {

    @Autowired
    private Firestore firestore;

    @Autowired
    private ObjectMapper objectMapper; // Spring Boot 預設已注入 ObjectMapper

    /**
     * 批次同步 init-data 下的所有 JSON 設定檔到 Firebase
     */
    public void syncAllConfigs() {
        try {
            // 1. 建立資源解析器，用來掃描資料夾
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            
            // 2. 抓取 resources/init-data/ 底下所有 json 檔案
            Resource[] resources = resolver.getResources("classpath:init-data/*.json");

            log.info("開始同步 Firebase 設定檔，共發現 {} 個檔案", resources.length);

            for (Resource resource : resources) {
                // 3. 使用 InputStream 讀取內容 (最安全做法)
                try (InputStream is = resource.getInputStream()) {
                    
                    // 4. 將 JSON 轉為我們定義好的 DTO
                    OptionConfig config = objectMapper.readValue(is, OptionConfig.class);

                    if (config.getId() == null || config.getId().isEmpty()) {
                        log.warn("檔案 {} 缺少 id 欄位，跳過同步", resource.getFilename());
                        continue;
                    }

                    // 5. 上傳到 Firebase (使用 metadata 作為 Collection)
                    // .set(config) 會直接將 DTO 轉為 Firestore 的文件內容
                    firestore.collection("metadata")
                             .document(config.getId())
                             .set(config)
                             .get(); // 確保非同步操作完成

                    log.info("同步成功: [{}] - {}", config.getId(), config.getName());
                }
            }
            log.info("所有設定檔同步完成！");

        } catch (Exception e) {
            log.error("同步 Firebase 過程中發生錯誤: ", e);
        }
    }
}