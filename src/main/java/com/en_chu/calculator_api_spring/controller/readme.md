# 🎮 Controller Layer Architecture

此目錄存放應用程式的 API 入口點 (Entry Points)。
Controller 的主要職責是 **處理 HTTP 請求、驗證參數 (Validation)、路由至對應的 Service**，以及 **封裝回應 (Response)**。

## 🏗️ 核心設計原則 (Design Principles)

為了避免 `UserController` 變成巨大的 God Class，且為了保持程式碼的清晰度，我們採用以下架構原則：

### 1. 職責分離 (Separation of Concerns)
我們根據 **「業務領域」** 與 **「敏感程度」** 將 Controller 拆分：
* **`UserController`**: 負責一般業務資料的讀取與寫入 (Profile, Career)。
* **`UserSecurityController`**: 專門負責高風險、高敏感度的操作 (密碼修改、帳號刪除、2FA)。

### 2. 讀寫策略 (Read/Write Strategy)
針對 Service 的調用，我們採用了輕量級的讀寫分離策略：
* **讀取 (READ/Aggregation)**:
    * 透過 `UserService` 進行資料整合。
    * *原因*：讀取 `/me` 時通常需要跨表 Join (User + Profile + Career)，由 `UserService` 統一組裝最合適。
* **寫入 (WRITE/Update)**:
    * Controller **直接呼叫** 子領域 Service (`UserProfileService`, `UserCareerService`)。
    * *原因*：單純的 Profile 更新不需要經過 `UserService` 過手 (避免 Middle Man Code Smell)。

### 3. 安全性 (Security)
* **UID 獲取**: 禁止前端在 Body 傳送 `uid`，一律透過 `SecurityUtils.getCurrentUserUid()` 從 Token 解析。
* **資料隱藏**: 依賴 Entity 的繼承結構 (`UserBaseEntity` + `@JsonIgnore`)，確保回傳 JSON 時自動過濾 `firebaseUid`。

---

## 📂 Controller 清單

### 1. UserController
> **Base Path:** `/api/v1/user`
> **描述:** 處理使用者的一般日常資料互動。

| Method | Path | Description | Service Strategy |
| :--- | :--- | :--- | :--- |
| `GET` | `/me` | 取得完整個人資料 (Init) | 呼叫 **UserService** (整合) |
| `PUT` | `/profile` | 更新基本資料卡片 | 直接呼叫 **UserProfileService** |
| `PUT` | `/career` | 更新職涯與薪資卡片 | 直接呼叫 **UserCareerService** |

**程式碼範例:**
```java
@Autowired private UserService userService;          // For GET
@Autowired private UserProfileService profileService; // For PUT
@Autowired private UserCareerService careerService;   // For PUT

@GetMapping("/me")
public ResponseEntity<UserFullDataRes> getMe() {
    String uid = SecurityUtils.getCurrentUserUid();
    return ResponseEntity.ok(userService.getFullUserData(uid));
}