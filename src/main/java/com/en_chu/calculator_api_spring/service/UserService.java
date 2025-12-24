package com.en_chu.calculator_api_spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.en_chu.calculator_api_spring.mapper.UserMapper;
import com.en_chu.calculator_api_spring.model.UserCareerReq;
import com.en_chu.calculator_api_spring.model.UserCareerRes;
import com.en_chu.calculator_api_spring.model.UserFullDataRes;
import com.en_chu.calculator_api_spring.model.UserProfileReq;
import com.en_chu.calculator_api_spring.model.UserProfileRes;
import com.en_chu.calculator_api_spring.util.SecurityUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	private UserCareerService userCareerService;

	// ==========================================
	// 1. 讀取邏輯 (Read)
	// ==========================================

	/**
	 * 取得當前用戶的完整資料 (User + Profile + Career) 用於 /api/v1/user/me
	 */
	public UserFullDataRes getFullUserData() {
		// 1. 取得 Firebase UID (String)
		String uid = SecurityUtils.getCurrentUserUid();

		// 2. 使用 UID 直接做 JOIN 查詢 (Mapper 支援 String UID 查詢)
		UserFullDataRes fullData = userMapper.selectFullUserDataByFirebaseUid(uid);

		// 3. 處理空值
		if (fullData == null) {
			log.warn("User with UID {} not found in DB.", uid);
			return null;
		}

		return fullData;
	}

	/**
	 * 單獨取得 Profile
	 */
	public UserProfileRes getProfileOnly() {
		// 先轉成內部 Long ID
		Long userId = getInternalUserId(SecurityUtils.getCurrentUserUid());
		return userMapper.findProfileByUserId(userId);
	}

	/**
	 * 單獨取得 Career
	 */
	public UserCareerRes getCareerOnly() {
		// 先轉成內部 Long ID
		Long userId = getInternalUserId(SecurityUtils.getCurrentUserUid());
		return userMapper.findCareerByUserId(userId);
	}

	// ==========================================
	// 2. 寫入邏輯 (Write / Update)
	// ==========================================

	/**
	 * 建立 Profile
	 */
	@Transactional
	public void createProfile(UserProfileReq req) {
		// 雖然是 Create，但我們需要在 Profile 表中填入 user_id
		// 因為 UserProfileService 可能預期 req 裡有 ID 或自行處理，
		// 建議這裡還是要把 Context 裡的 User 資訊傳遞下去，或是讓 Controller 處理
		// 但為了保持 Service 封裝，我們這裡攔截：

		// 注意：Create Profile 通常不需要前端帶 ID，而是後端綁定 Current User
		// 這裡我們不需設定 req.id (因為那是 profile table 的 PK)，但需要綁定 user_id
		// *具體實作依 UserProfileService 而定，這裡假設它會自己去拿 SecurityUtils*

		// 修正：如果 UserProfileService 也是依賴 SecurityUtils.getCurrentUserUid()，那直接呼叫即可
		// 但如果它是依賴 Long ID，你需要傳進去。假設維持原案：
		userProfileService.createProfile(req);
	}

	/**
	 * 更新 Profile
	 */
	@Transactional
	public void updateProfile(UserProfileReq req) {
		// 1. 取得內部 User ID
		Long userId = getInternalUserId(SecurityUtils.getCurrentUserUid());

		// 2. 找出這個 User 真正的 Profile ID
		Long profileId = userMapper.findProfileIdByUserId(userId);

		if (profileId == null) {
			throw new IllegalArgumentException("尚未建立個人檔案，請先執行新增 (Create)");
		}

		// 3. 強制覆寫 ID，防止越權修改
		req.setId(profileId);

		// 4. 委派更新
		userProfileService.updateProfile(req);
	}

	/**
	 * 更新 Career (包含新增與修改)
	 */
	@Transactional
	public void updateCareer(UserCareerReq req) {
		// 1. 取得內部 User ID
		Long userId = getInternalUserId(SecurityUtils.getCurrentUserUid());

		// 2. 查詢該用戶是否已有 Career 資料 (取得 Career 的 PK)
		Long careerId = userMapper.findCareerIdByUserId(userId);

		// 3. 設定 ID (如果是 null 代表要 Insert，有值代表要 Update)
		req.setId(careerId);

		// 4. 委派處理 (傳入 userId 是為了 Insert 時能寫入 FK)
		userCareerService.updateCareer(req, userId);
	}

	// ==========================================
	// 3. 輔助與同步邏輯
	// ==========================================

	/**
	 * 帳號同步 (用於 Login Filter)
	 */
	@Transactional
	public void syncUser(String firebaseUid, String email) {
		boolean exists = userMapper.checkUserExists(firebaseUid);
		if (!exists) {
			log.info("New User Sync: creating user for {}", email);
			userMapper.insertUser(firebaseUid, email);
		} else {
			userMapper.updateLastLogin(firebaseUid);
		}
	}

	/**
	 * 私有方法：將 Firebase UID (String) 轉換為 Database ID (Long)
	 */
	private Long getInternalUserId(String firebaseUid) {
		Long userId = userMapper.selectIdByFirebaseUid(firebaseUid);
		if (userId == null) {
			// 理論上經過 Filter 同步後不該發生，除非同步失敗
			throw new RuntimeException("User not found in database: " + firebaseUid);
		}
		return userId;
	}
}