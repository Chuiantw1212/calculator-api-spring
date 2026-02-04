package com.en_chu.calculator_api_spring.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Bean
    public Firestore firestore() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseOptions options;

            // 檢查是否在 Google Cloud 環境 (例如 Cloud Run)
            // GOOGLE_CLOUD_PROJECT 環境變數是 Cloud Run 自動注入的
            if (System.getenv("GOOGLE_CLOUD_PROJECT") != null) {
                log.info("☁️ 在 Google Cloud 環境中，使用應用程式預設憑證 (ADC) 初始化 Firebase...");
                options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.getApplicationDefault())
                        .build();
            } else {
                log.info("🏠 在本地環境中，讀取 service_account_key.json 初始化 Firebase...");
                // 讀取你的 Firebase 金鑰檔案
                InputStream serviceAccount = new ClassPathResource("service_account_key.json").getInputStream();
                options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
            }

            FirebaseApp.initializeApp(options);
        }

        return FirestoreClient.getFirestore();
    }
}