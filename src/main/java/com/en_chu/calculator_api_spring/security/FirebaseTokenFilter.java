package com.en_chu.calculator_api_spring.security;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FirebaseTokenFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String path = request.getRequestURI();
	    System.out.println("🔍 [Filter] 請求進入: " + path); // 除錯用，上線可註解

		String header = request.getHeader("Authorization");

		// 1. 若沒帶 Token，直接放行 (讓 SecurityConfig 決定是否擋下)
		if (header == null || !header.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		// 2. 解析 Token
		String token = header.substring(7);
		try {
			FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);

			String uid = decodedToken.getUid();

			// 3. 設定身分
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(uid,
					decodedToken, new ArrayList<>());
			SecurityContextHolder.getContext().setAuthentication(authentication);

		} catch (FirebaseAuthException e) {
			System.err.println("💥 [Filter] Firebase 驗證失敗: " + e.getMessage());

			// --- [關鍵新增] 將錯誤訊息存入 Request，讓 EntryPoint 可以讀取 ---
			request.setAttribute("firebase_exception", "Firebase 驗證失敗: " + e.getMessage());

			// 清除 Context 確保安全 (雖然預設就是空的，但保險起見)
			SecurityContextHolder.clearContext();

		} catch (Exception e) {
			System.err.println("💥 [Filter] Token 解析發生未知錯誤: " + e.getMessage());

			// --- [關鍵新增] ---
			request.setAttribute("firebase_exception", "Token 無效或解析錯誤");
			SecurityContextHolder.clearContext();
		}

		// 繼續往後走，因為 SecurityContext 是空的，Spring Security 會在後續拋出 401
		filterChain.doFilter(request, response);
	}
}