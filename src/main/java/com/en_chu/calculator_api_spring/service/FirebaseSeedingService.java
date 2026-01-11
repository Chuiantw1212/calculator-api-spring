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

	/**
	 * 1. 同步一般設定檔 (Metadata) 邏輯：掃描所有 JSON，但「跳過」生命表 (opt_life_table)
	 */
	public void syncMetadataConfigs() {
		try {
			PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
			Resource[] resources = resolver.getResources("classpath:init-data/*.json");

			log.info("📂 [Metadata] 掃描到 {} 個檔案，開始同步一般設定...", resources.length);

			for (Resource resource : resources) {
				try (InputStream is = resource.getInputStream()) {
					Map<String, Object> data = objectMapper.readValue(is, new TypeReference<Map<String, Object>>() {
					});
					String docId = (String) data.get("id");

					if (docId == null)
						continue;

					// 🛑 關鍵：如果是生命表，直接跳過，不處理
					if ("opt_life_table".equals(docId)) {
						continue;
					}

					// 執行一般 Metadata 更新
					firestore.collection("metadata").document(docId).set(data).get();
					log.info("✅ 同步成功 (Metadata): [{}]", docId);
				}
			}
			log.info("✨ 一般設定檔同步完成！");
		} catch (Exception e) {
			log.error("❌ Metadata 同步失敗: ", e);
			throw new RuntimeException("Metadata Sync Failed", e);
		}
	}

	/**
	 * 2. 同步生命表 (Life Table) 邏輯：掃描所有 JSON，只處理 id 為 "opt_life_table" 的檔案
	 */
	public void syncLifeTable() {
		try {
			PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
			Resource[] resources = resolver.getResources("classpath:init-data/*.json");

			log.info("📂 [LifeTable] 尋找生命表設定檔...");

			boolean found = false;

			for (Resource resource : resources) {
				try (InputStream is = resource.getInputStream()) {
					// 這裡只讀取 Map 的部分內容來檢查 ID，避免一次讀入太大
					Map<String, Object> data = objectMapper.readValue(is, new TypeReference<Map<String, Object>>() {
					});
					String docId = (String) data.get("id");

					// 🛑 關鍵：只處理生命表
					if ("opt_life_table".equals(docId)) {
						found = true;
						log.info("🚀 找到生命表檔案，開始執行拆分寫入程序...");
						processLifeTableBatch(docId, data);
						break; // 處理完就跳出迴圈，不需要看其他檔案
					}
				}
			}

			if (!found) {
				log.warn("⚠️ 未找到 id 為 'opt_life_table' 的設定檔");
			}

		} catch (Exception e) {
			log.error("❌ LifeTable 同步失敗: ", e);
			throw new RuntimeException("LifeTable Sync Failed", e);
		}
	}

	/**
	 * 內部私有方法：執行生命表的 Batch 寫入邏輯
	 */
	private void processLifeTableBatch(String collectionName, Map<String, Object> sourceData) throws Exception {
		List<Map<String, Object>> list = objectMapper.convertValue(sourceData.get("list"),
				new TypeReference<List<Map<String, Object>>>() {
				});

		if (list == null || list.isEmpty()) {
			log.warn("生命表 list 為空，不進行寫入");
			return;
		}

		log.info("📊 準備處理 {} 筆生命表數據...", list.size());

		WriteBatch batch = firestore.batch();
		int batchCount = 0;
		int totalCount = 0;

		for (Map<String, Object> row : list) {
			Integer year = (Integer) row.get("year");
			String gender = (String) row.get("gender");
			Integer age = (Integer) row.get("age");

			Object lifespanObj = row.get("expected_lifespan");
			Double lifespan = (lifespanObj instanceof Number) ? ((Number) lifespanObj).doubleValue() : 0.0;

			// Document ID: 2025_MALE_0
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

			// Firestore Batch 上限為 500
			if (batchCount >= 500) {
				batch.commit().get();
				log.debug("已批次寫入 500 筆...");
				batch = firestore.batch();
				batchCount = 0;
			}
		}

		if (batchCount > 0) {
			batch.commit().get();
		}

		log.info("✨ 生命表同步完成！總共寫入 {} 筆資料至 Collection: {}", totalCount, collectionName);
	}
}