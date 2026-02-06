# 財務計算機 API (Financial Calculator API)

這是一個基於 Spring Boot 3 的現代化後端 API，旨在為開源財務計算機應用提供安全、穩定、可擴展的服務。專案採用了分層架構、RESTful 設計原則，並整合了 Firebase 進行身份驗證，確保了企業級的安全性與開發效率。

## ✨ 功能亮點 (Features)

*   **多維度資產管理**: 提供對使用者個人資料、職涯、退休、稅務、投資組合、房地產、信用卡、副業等多個模組的完整 CRUD 操作。
*   **安全的身份驗證**: 整合 Firebase Admin SDK，將使用者身份驗證與管理完全委託給 Google，極大提升安全性。
*   **容器化部署**: 提供一個優化的 `Dockerfile`，確保在任何環境下都能有一致、可靠的建置與部署。
*   **整合 API 文件**: 內建 SpringDoc (Swagger UI)，自動生成互動式 API 文件，方便前端開發與測試。
*   **應用狀態監控**: 整合 Spring Boot Actuator，提供 `/health`, `/info`, `/metrics` 等多個端點，用於即時監控應用健康狀況與記憶體用量。

## 🛠️ 技術棧 (Tech Stack)

| 類別 | 技術 | 用途 |
| :--- | :--- | :--- |
| **核心框架** | Spring Boot 3.3.2 | 提供快速、穩健的應用程式開發基礎。 |
| **語言** | Java 17 | 專案使用的 JDK 版本。 |
| **容器化** | Docker | 透過 `Dockerfile` 定義應用程式的建置與運行環境。 |
| **資料庫** | PostgreSQL (Neon) | 關聯式資料庫，用於儲存所有業務資料。 |
| **持久層** | MyBatis 3.0.3 | SQL 映射框架，提供對 SQL 的完全控制。 |
| **身份驗證** | Firebase Admin SDK 9.3.0 | 驗證前端傳來的 Firebase ID Token，管理使用者。 |
| **API 文件** | SpringDoc OpenAPI 2.5.0 | 自動生成並展示 Swagger UI。 |
| **應用監控** | Spring Boot Actuator | 提供生產級的應用監控端點。 |
| **開發工具** | Lombok, Maven | 簡化程式碼，管理專案依賴。 |

---

## ☁️ 部署狀態 (Deployment)

本專案已透過 CI/CD 自動部署至 **Google Cloud Run**。

*   **服務狀態**: ✅ **線上運行中 (Online)**
*   **根目錄狀態**: [https://calculator-api-spring-592400229145.asia-east1.run.app/](https://calculator-api-spring-592400229145.asia-east1.run.app/)
*   **API 文件 (Swagger UI)**: [https://calculator-api-spring-592400229145.asia-east1.run.app/swagger-ui/index.html](https://calculator-api-spring-592400229145.asia-east1.run.app/swagger-ui/index.html)
*   **健康檢查 (Health Check)**: [https://calculator-api-spring-592400229145.asia-east1.run.app/actuator/health](https://calculator-api-spring-592400229145.asia-east1.run.app/actuator/health)

---

## 📦 容器化與建置 (Containerization & Build)

為了確保部署的穩定性與一致性，本專案採用**明確定義的 `Dockerfile`** 進行容器化，而不是依賴雲端平台自動偵測的 Buildpacks。

### Dockerfile 解析
`Dockerfile` 採用了**多階段建置 (Multi-stage build)** 的最佳實踐：
1.  **Build Stage**: 使用一個包含完整 Maven 和 JDK 的映像 (`maven:3.8.5-openjdk-17`) 來編譯原始碼、下載依賴，並將應用程式打包成一個可執行的 JAR 檔案。
2.  **Run Stage**: 使用一個極度輕量化的、僅包含 Java 運行時的映像 (`openjdk:17-jdk-slim`)，並從 Build Stage 中僅複製最終生成的 JAR 檔案。

這個作法能帶來以下好處：
*   **最小化映像體積**: 最終部署的容器不包含任何編譯工具 (如 Maven)，體積更小，啟動更快。
*   **提升安全性**: 減少了容器內的攻擊面。
*   **建置過程可預測**: 完全消除了因 Buildpack 版本或行為變更而導致的部署失敗風險。

### Cloud Build 設定
在 Google Cloud Run 的持續部署設定中，建置類型已指定為 `Dockerfile`，並指向專案根目錄下的 `Dockerfile` 檔案。

---

## 🚀 快速開始 (Getting Started)

### 1. 環境準備
*   Java 17 或更高版本
*   Maven 3.x
*   Docker
*   PostgreSQL 資料庫

### 2. 本地運行
#### A. 環境變數
在 IDE 的啟動配置中設定以下環境變數：
*   `DB_HOST`: 資料庫主機位址
*   `DB_PORT`: 資料庫端口
*   `DB_NAME`: 資料庫名稱
*   `DB_USERNAME`: 使用者名稱
*   `DB_PASSWORD`: 密碼

#### B. Firebase 金鑰 (本地開發用)
1.  從 Firebase Console 下載你的服務帳戶私鑰 JSON 檔案。
2.  將其重新命名為 `serviceAccountKey.json`。
3.  將檔案放入專案的 `src/main/resources/` 目錄下。

#### C. 啟動應用
```bash
mvn spring-boot:run
```
應用程式將會啟動在 `http://localhost:8888`。

### 3. 使用 Docker 運行
你也可以使用專案提供的 `Dockerfile` 在本地模擬生產環境：
```bash
# 1. 建置 Docker 映像
docker build -t calculator-api .

# 2. 運行容器，並傳入資料庫環境變數
docker run -p 8080:8080 \
  -e DB_HOST="your_db_host" \
  -e DB_PORT="5432" \
  -e DB_NAME="your_db_name" \
  -e DB_USERNAME="your_username" \
  -e DB_PASSWORD="your_password" \
  calculator-api
```
