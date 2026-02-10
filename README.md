# 財務計算機後端 API (Calculator API)

本專案是開源財務計算機的後端 API 服務，使用 Java 和 Spring Boot 框架開發。

## 核心技術棧

- **框架**: Spring Boot 3.3.2
- **語言**: Java 17
- **資料庫**: PostgreSQL
- **資料存取**: MyBatis 3
- **認證**: Firebase Authentication
- **API 文件**: SpringDoc (Swagger UI)

## 環境設定與啟動

### 1. 本地開發 (Local Development)

#### 資料庫設定

本專案的資料庫連線資訊，是透過 `application-local.yaml` 這個**不受 Git 版本控制**的檔案來設定的。

1.  在 `src/main/resources/` 路徑下，手動建立一個名為 `application-local.yaml` 的檔案。
2.  將以下內容複製進去，並填寫你自己的本地資料庫連線資訊：

    ```yaml
    # 本地開發專用設定 - 此檔案不受 Git 追蹤
    spring:
      datasource:
        url: jdbc:postgresql://localhost:5432/your_local_db_name
        username: your_local_username
        password: your_local_password
    ```

#### 啟動應用程式

1.  在你的 IDE (例如 IntelliJ IDEA) 中，找到 `CalculatorApiSpringApplication` 這個主類別。
2.  在啟動配置 (Run/Debug Configurations) 中，將 "Active profiles" 設定為 `dev`。
3.  點擊「運行」按鈕。應用程式將會以 `dev` 模式啟動，並載入 `application-dev.yaml` 和 `application-local.yaml` 的配置。

### 2. 生產環境部署 (Production Deployment)

本專案已針對 Google Cloud Run 進行了優化。

#### 環境變數

在部署到 Cloud Run 時，你需要在服務的 "Variables & Secrets" 中，設定以下環境變數：

-   `DB_HOST`: 生產環境資料庫的主機位址。
-   `DB_PORT`: 生產環境資料庫的 Port (例如 `5432`)。
-   `DB_NAME`: 生產環境資料庫的名稱。
-   `DB_USERNAME`: 資料庫使用者名稱。
-   `DB_PASSWORD`: 資料庫密碼 (強烈建議使用 Secret Manager 來儲存)。
-   `GOOGLE_APPLICATION_CREDENTIALS`: 指向 Firebase Admin 服務帳號金鑰檔案的路徑 (Cloud Build 通常會自動處理)。

#### ⚠️ 重要的部署注意事項

在像 Cloud Run 這樣的反向代理環境下，有一個非常關鍵的設定，用以解決 API 文件 (Swagger UI) 無法正常發出請求的問題。

關於這個問題的詳細解釋和解決方案，請參考專案內的技術文件：
[**`src/main/resources/DEPLOYMENT_NOTES.md`**](./src/main/resources/DEPLOYMENT_NOTES.md)
