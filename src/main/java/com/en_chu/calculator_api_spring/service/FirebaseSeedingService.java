package com.en_chu.calculator_api_spring.service;

import java.io.InputStream;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.Firestore;

import lombok.extern.slf4j.Slf4j;

@Slf4j // 1. 自動生成 Log 物件，讓你用 log.info() 印出漂亮日誌
@Service // 2. 標記為 Spring 管理的 Service，讓它能被 @Autowired 注入
public class FirebaseSeedingService {

	@Autowired
	private Firestore firestore; // 3. 注入你在 FirebaseConfig 註冊好的 Firestore 零件

	@Autowired
	private ObjectMapper objectMapper; // 4. 注入 Jackson 核心工具，用來把 JSON 字串轉成 Java Map

	public void syncAllConfigs() {
		try {
			// 5. 建立一個「資源路徑解析器」，它是 Spring 用來搜尋檔案的神器
			PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

			// 6. 掃描 classpath 下 init-data 資料夾中所有以 .json 結尾的檔案
			// 這讓你未來增加新設定檔時，不需改動任何一行程式碼
			Resource[] resources = resolver.getResources("classpath:init-data/*.json");

			log.info("偵測到 {} 個設定檔，準備開始同步...", resources.length);

			for (Resource resource : resources) { // 7. 開始巡迴處理每一個找到的檔案

				// 8. 使用 try-with-resources 語法讀取 Input串流，確保讀取完後自動關閉檔案，避免記憶體洩漏
				try (InputStream is = resource.getInputStream()) {

					// 9. 【核心關鍵】將 JSON 內容轉為通用 Map
					// 使用 TypeReference 告訴 Jackson：「我不確定裡面長怎樣，通通幫我轉成鍵值對就對了」
					// 這讓 opt_ (陣列) 與 cfg_ (物件) 都能被同一個邏輯處理
					Map<String, Object> data = objectMapper.readValue(is, new TypeReference<Map<String, Object>>() {
					});

					// 10. 從解析出來的資料中抓取 "id" 欄位，這將作為 Firebase 的 Document ID
					String docId = (String) data.get("id");

					if (docId != null) {
						// 11. 執行上傳動作
						// collection("metadata")：指定存放在 metadata 這個集合
						// document(docId)：指定文件名稱（如 opt_gender）
						// set(data)：將整包 Map 直接塞進去，Firestore 會自動處理資料型態
						// .get()：因為 Firebase 是非同步操作，加個 .get() 讓 Java 等它傳完再跑下一行
						firestore.collection("metadata").document(docId).set(data).get();

						log.info("同步成功: 檔案 [{}] -> Firestore 文件 [{}]", resource.getFilename(), docId);
					} else {
						log.warn("跳過檔案 {}: 內容缺少 'id' 欄位", resource.getFilename());
					}
				}
			}
			log.info("所有設定檔同步任務完成！");
		} catch (Exception e) {
			// 12. 捕捉任何可能的錯誤（如檔案損壞、網路斷線），並記錄在 Log 中
			log.error("同步過程中發生災難性錯誤: ", e);
		}
	}
}