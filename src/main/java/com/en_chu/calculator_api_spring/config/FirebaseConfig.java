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
            FirebaseOptions.Builder optionsBuilder = FirebaseOptions.builder();

            // K_SERVICE 是 Google Cloud Run 保證會設定的標準環境變數。
            if (System.getenv("K_SERVICE") != null) {
                log.info("☁️ Cloud Run 環境已檢測。使用應用程式預設憑證 (ADC)。");

                // 從環境變數讀取 Project ID
                String projectId = System.getenv("GOOGLE_CLOUD_PROJECT");
                if (projectId == null) {
                    throw new IllegalStateException("GOOGLE_CLOUD_PROJECT environment variable is not set in Cloud Run.");
                }
                log.info("Project ID '{}' 已設定。", projectId);

                optionsBuilder
                    .setCredentials(GoogleCredentials.getApplicationDefault())
                    .setProjectId(projectId);

            } else {
                log.info("🏠 本地環境已檢測。從 Classpath 讀取 'service_account_key.json'。");
                // 本地開發邏輯不變
                InputStream serviceAccount = new ClassPathResource("service_account_key.json").getInputStream();
                optionsBuilder.setCredentials(GoogleCredentials.fromStream(serviceAccount));
            }

            FirebaseApp.initializeApp(optionsBuilder.build());
        }

        return FirestoreClient.getFirestore();
    }
}
