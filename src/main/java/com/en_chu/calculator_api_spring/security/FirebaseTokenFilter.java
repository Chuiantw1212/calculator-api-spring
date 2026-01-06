package com.en_chu.calculator_api_spring.security;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FirebaseTokenFilter extends OncePerRequestFilter {

	private static final String AUTH_HEADER = "Authorization";
	private static final String TOKEN_PREFIX = "Bearer ";

	// 這個 Key 必須跟 FirebaseAuthenticationEntryPoint 裡讀取的 Key 一致
	private static final String EXCEPTION_ATTR_NAME = "firebase_exception";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// 1. 從 Header 取得 Token
		String header = request.getHeader(AUTH_HEADER);

		// 2. 初步檢查：如果沒有帶 Token，或是格式不對，直接放行
		// (Spring Security 後面的 authorizeHttpRequests 會決定這個路徑是否允許匿名存取)
		if (!StringUtils.hasText(header) || !header.startsWith(TOKEN_PREFIX)) {
			// log.debug("🔍 [Auth] No valid Authorization header found, passing to next
			// filter.");
			filterChain.doFilter(request, response);
			return;
		}

		// 3. 開始驗證流程
		String token = header.substring(TOKEN_PREFIX.length());

		try {
			// 呼叫 Firebase SDK 驗證 (這是最關鍵的一步，會連網或查 Cache)
			FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);

			// 取得 UID
			String uid = decodedToken.getUid();

			// 4. 建立 Authentication 物件
			// principal = uid (方便 Controller 直接拿)
			// credentials = decodedToken (如果需要 Email 或其他資訊可以從這裡拿)
			// authorities = empty list (如果您有做 RBAC 角色權限，要在這裡塞入 authorities)
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(uid,
					decodedToken, new ArrayList<>());

			// 5. 將驗證結果放入 SecurityContext
			SecurityContextHolder.getContext().setAuthentication(authentication);

			// log.debug("✅ [Auth Success] User: {}", uid);

		} catch (FirebaseAuthException e) {
			// 情境 A: Token 過期、簽名錯誤、被撤銷 (這是使用者的問題)
			log.warn("⚠️ [Auth Fail] Firebase Token 無效: {} (Code: {})", e.getMessage(), e.getAuthErrorCode());

			// 將具體錯誤原因放入 Request，讓 EntryPoint 回傳給前端
			request.setAttribute(EXCEPTION_ATTR_NAME, "Token 驗證失敗: " + e.getMessage());

			// 確保 Context 是乾淨的
			SecurityContextHolder.clearContext();

		} catch (Exception e) {
			// 情境 B: 程式碼炸裂、NullPointerException、網路斷線 (這是系統的問題)
			// 🔥 重點：這裡用 log.error 並且傳入 e，這樣 Console 才會印出 Stack Trace
			log.error("💥 [System Error] Auth Filter 發生未預期錯誤", e);

			// 資安考量：不要把 e.getMessage() 回傳給前端，避免洩漏程式結構
			request.setAttribute(EXCEPTION_ATTR_NAME, "系統內部驗證錯誤，請聯繫管理員");

			SecurityContextHolder.clearContext();
		}

		// 6. 繼續執行下一個 Filter
		filterChain.doFilter(request, response);
	}
}	