package com.en_chu.calculator_api_spring.service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteBatch;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FirebaseSeedingService {

	@Autowired
	private Firestore firestore;

	@Autowired
	private ObjectMapper objectMapper;

	public void syncAllConfigs() {
		try {
			PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
			Resource[] resources = resolver.getResources("classpath:init-data/*.json");

			log.info("偵測到 {} 個設定檔，準備開始同步...", resources.length);

			for (Resource resource : resources) {
				try (InputStream is = resource.getInputStream()) {

					// 1. 先讀成通用 Map
					Map<String, Object> data = objectMapper.readValue(is, new TypeReference<Map<String, Object>>() {
					});
					String docId = (String) data.get("id");

					if (docId == null) {
						log.warn("跳過檔案 {}: 缺少 'id' 欄位", resource.getFilename());
						continue;
					}

					// ==========================================
					// 2. 例外處理：如果是生命表 (opt_life_table)
					// ==========================================
					if ("opt_life_table".equals(docId)) {
						log.info("🚀 偵測到生命表資料，啟動特殊結構轉換程序...");
						syncLifeTableData(docId, data);
					}
					// ==========================================
					// 3. 標準處理：其他 metadata (整包塞入)
					// ==========================================
					else {
						firestore.collection("metadata").document(docId).set(data).get();
						log.info("同步成功 (Metadata): [{}]", docId);
					}
				}
			}
			log.info("所有設定檔同步任務完成！");
		} catch (Exception e) {
			log.error("同步過程中發生災難性錯誤: ", e);
		}
	}

	/**
	 * 特殊處理：將生命表 List 拆散為單一文件 (Granular Document) * 目標結構： Collection: opt_life_table
	 * Document ID: "2025_MALE_0" Fields: { "year": 2025, "gender": "MALE", "age":
	 * 0, "expected_lifespan": 78.22 }
	 */
	private void syncLifeTableData(String collectionName, Map<String, Object> sourceData) throws Exception {
		// 1. 使用 Jackson 安全轉型 (解決 Unchecked cast 警告)
		List<Map<String, Object>> list = objectMapper.convertValue(sourceData.get("list"),
				new TypeReference<List<Map<String, Object>>>() {
				});

		if (list == null || list.isEmpty()) {
			return;
		}

		log.info("🚀 開始處理生命表資料拆分，共 {} 筆...", list.size());

		WriteBatch batch = firestore.batch();
		int batchCount = 0;
		int totalCount = 0;

		for (Map<String, Object> row : list) {
			Integer year = (Integer) row.get("year");
			String gender = (String) row.get("gender");
			Integer age = (Integer) row.get("age");

			// 處理數值轉換 (安全起見)
			Object lifespanObj = row.get("expected_lifespan");
			Double lifespan = (lifespanObj instanceof Number) ? ((Number) lifespanObj).doubleValue() : 0.0;

			// 2025_MALE_0
			String docKey = year + "_" + gender + "_" + age;

			Map<String, Object> docData = new HashMap<>();
			docData.put("year", year);
			docData.put("gender", gender);
			docData.put("age", age);
			docData.put("expected_lifespan", lifespan);

			DocumentReference docRef = firestore.collection(collectionName).document(docKey);
			batch.set(docRef, docData);

			batchCount++;
			totalCount++;

			// 每 500 筆提交一次
			if (batchCount >= 500) {
				batch.commit().get();
				batch = firestore.batch();
				batchCount = 0;
			}
		}

		if (batchCount > 0) {
			batch.commit().get();
		}

		log.info("同步成功 (LifeTable): 已將 {} 筆資料拆分為獨立文件 (Collection: {})", totalCount, collectionName);
	}
}