# 財務計算機 API (Financial Calculator API)

這是一個基於 Spring Boot 3 的現代化後端 API，旨在為開源財務計算機應用提供安全、穩定、可擴展的服務。專案採用了分層架構、RESTful 設計原則，並整合了 Firebase 進行身份驗證，確保了企業級的安全性與開發效率。

## ✨ 功能亮點 (Features)

*   **多維度資產管理**: 提供對使用者個人資料、職涯、退休、稅務、投資組合、房地產、信用卡、副業等多個模組的完整 CRUD 操作。
*   **安全的身份驗證**: 整合 Firebase Admin SDK，將使用者身份驗證與管理完全委託給 Google，極大提升安全性。
*   **分層架構設計**: 嚴格劃分 Controller, Service, Mapper 層，並區分 DTO 與 Entity，使程式碼職責清晰、易於維護。
*   **整合 API 文件**: 內建 SpringDoc (Swagger UI)，自動生成互動式 API 文件，方便前端開發與測試。
*   **應用狀態監控**: 整合 Spring Boot Actuator，提供 `/health`, `/info`, `/metrics` 等多個端點，用於即時監控應用健康狀況與記憶體用量。
*   **統一的錯誤處理**: 透過 `FirebaseAuthenticationEntryPoint` 集中處理權限驗證失敗的例外情況。

## 🛠️ 技術棧 (Tech Stack)

| 類別 | 技術 | 用途 |
| :--- | :--- | :--- |
| **核心框架** | Spring Boot 3.3.2 | 提供快速、穩健的應用程式開發基礎。 |
| **語言** | Java 17 | 專案使用的 JDK 版本。 |
| **資料庫** | PostgreSQL (Neon) | 關聯式資料庫，用於儲存所有業務資料。 |
| **持久層** | MyBatis 3.0.4 | SQL 映射框架，提供對 SQL 的完全控制。 |
| **身份驗證** | Firebase Admin SDK | 驗證前端傳來的 Firebase ID Token，管理使用者。 |
| **API 文件** | SpringDoc OpenAPI | 自動生成並展示 Swagger UI。 |
| **應用監控** | Spring Boot Actuator | 提供生產級的應用監控端點。 |
| **開發工具** | Lombok, Maven | 簡化程式碼，管理專案依賴。 |

---

## ☁️ 部署 (Deployment)

本專案已透過持續部署 (CI/CD) 發布至 Google Cloud Run。

*   **服務網址 (Production URL)**: [https://planner-api-spring-592400229145.asia-east1.run.app](https://planner-api-spring-592400229145.asia-east1.run.app)
*   **API 文件 (Swagger UI)**: [https://planner-api-spring-592400229145.asia-east1.run.app/swagger-ui/index.html](https://planner-api-spring-592400229145.asia-east1.run.app/swagger-ui/index.html)
*   **健康檢查 (Health Check)**: [https://planner-api-spring-592400229145.asia-east1.run.app/actuator/health](https://planner-api-spring-592400229145.asia-east1.run.app/actuator/health)

---

## 🏗️ 專案架構 (Project Architecture)

本專案採用經典的三層架構，並對資料模型進行了嚴格的分層。

### 1. 服務分層

*   **Controller**: 負責接收 HTTP 請求，驗證使用者身份，呼叫 Service 層處理業務邏輯，並回傳標準的 `ResponseEntity`。
*   **Service**: 業務邏輯的核心，負責組合多個 Mapper 的操作，處理複雜計算，並執行交易控制 (`@Transactional`)。
*   **Mapper**: 資料存取層，定義與 MyBatis XML 對應的 Java 介面，負責執行單一的 SQL 操作。

### 2. 資料模型分層 (DTO vs. Entity)

這是本專案最重要的設計原則之一，旨在確保 API 的穩定性與資料庫的安全性。

| 層級 | 資料夾路徑 | 命名約定 | 職責 |
| :--- | :--- | :--- | :--- |
| **DTO (Data Transfer Object)** | `model` | `*Dto`, `*Req`, `*Res` | **面向 API**。用於定義請求 (Request) 和回應 (Response) 的資料結構。可以包含驗證註解 (`@Valid`)。 |
| **Entity** | `entity` | (無後綴) | **面向資料庫**。嚴格與資料庫的 Table 結構 1:1 對應，由 Mapper 使用。 |

---

## 🛡️ 安全性概覽 (Security Overview)

*   **身份驗證**: 所有需要保護的 API 端點都由 `FirebaseTokenFilter` 攔截，驗證請求 Header 中的 `Authorization: Bearer <ID_TOKEN>`。
*   **授權**:
    *   採用「預設拒絕」策略 (`.anyRequest().authenticated()`)，只有明確列入白名單的端點（如 Swagger、Actuator）才能匿名存取。
    *   在 Service 層與 Mapper 層，所有針對特定資源的操作都會同時驗證資源 ID 和使用者 UID，有效防止水平越權攻擊 (IDOR)。
*   **防 SQL 注入**: 所有 MyBatis 的 SQL 查詢均使用 `#{...}` 參數化語法，從根本上杜絕 SQL 注入風險。
*   **CORS**: 採用嚴格的來源白名單策略，只允許指定的網域進行跨域存取。
*   **秘密管理**: 資料庫帳密、Firebase 金鑰等敏感資訊均透過環境變數或外部檔案加載，**不會**硬編碼在程式碼中。

---

## 🚀 快速開始 (Getting Started)

### 1. 環境準備
*   Java 17 或更高版本
*   Maven 3.x
*   PostgreSQL 資料庫

### 2. 資料庫初始化
在你的 PostgreSQL 資料庫中執行以下 SQL 以建立所需表格：
```sql
-- 使用者個人檔案表
CREATE TABLE IF NOT EXISTS user_profiles (
    firebase_uid VARCHAR(128) PRIMARY KEY,
    birth_date DATE,
    gender VARCHAR(10),
    current_age INT,
    life_expectancy INT,
    marriage_year INT,
    career_insurance_type VARCHAR(50),
    biography TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 其他相關表格...
-- (請根據 entity 定義補全 user_careers, user_taxes, user_portfolios 等表格的 CREATE SCRIPT)
```

### 3. 專案配置
#### A. 環境變數 (資料庫連線)
在 IDE 的啟動配置中設定以下環境變數：
*   `DB_HOST`: 資料庫主機位址 (e.g., `your-neon-host.com`)
*   `DB_PORT`: 資料庫端口 (e.g., `5432`)
*   `DB_NAME`: 資料庫名稱 (e.g., `neondb`)
*   `DB_USERNAME`: 資料庫使用者名稱
*   `DB_PASSWORD`: 資料庫密碼

#### B. Firebase 金鑰
1.  從 [Firebase Console](https://console.firebase.google.com/) 下載你的服務帳戶私鑰 JSON 檔案。
2.  將其重新命名為 `serviceAccountKey.json`。
3.  將檔案放入專案的 `src/main/resources/` 目錄下。
    > ⚠️ **安全警告**: `serviceAccountKey.json` 檔案已被加入 `.gitignore`，**絕對不要**將此檔案提交到任何版本控制系統。

### 4. 啟動應用
在專案根目錄下執行以下指令：
```bash
mvn spring-boot:run
```
應用程式將會啟動在 `http://localhost:8888`。
