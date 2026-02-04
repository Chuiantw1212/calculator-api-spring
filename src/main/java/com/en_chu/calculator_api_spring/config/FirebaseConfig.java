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

            // K_SERVICE 是 Google Cloud Run 保證會設定的標準環境變數。
            // 這是檢測 Cloud Run 環境最可靠的方法。
            if (System.getenv("K_SERVICE") != null) {
                log.info("☁️ Cloud Run 環境已檢測 (K_SERVICE is set)。使用應用程式預設憑證 (ADC)。");
                options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.getApplicationDefault())
                        .build();
            } else {
                log.info("🏠 本地環境已檢測。從 Classpath 讀取 'service_account_key.json'。");
                // 這個邏輯專為本地開發保留
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
