package com.en_chu.calculator_api_spring.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

	@Bean
	public Firestore firestore() throws IOException {
		// 1. 檢查 Firebase 是否已經初始化，避免重複初始化報錯
		if (FirebaseApp.getApps().isEmpty()) {
			// 讀取你的 Firebase 金鑰檔案
			InputStream serviceAccount = new ClassPathResource("service_account_key.json").getInputStream();

			FirebaseOptions options = FirebaseOptions.builder()
					.setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();

			FirebaseApp.initializeApp(options);
		}

		// 2. 這是最重要的一步：將 Firestore 實例交給 Spring 管理
		// 這樣你在 Service 寫 @Autowired private Firestore firestore; 才找得到東西
		return FirestoreClient.getFirestore();
	}
}