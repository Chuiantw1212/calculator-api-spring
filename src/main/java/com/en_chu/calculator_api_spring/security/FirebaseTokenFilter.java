package com.en_chu.calculator_api_spring.security;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
/**
 * 這就是你的 verifyIdToken 函數的 Spring Boot 版本 (Middleware) 每個 Request 進來都會先經過這裡
 */
import org.springframework.stereotype.Component; // 1. 記得 import
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
		System.out.println("🔍 [Filter] 請求進入: " + path);

		// 1. 檢查 Header
		String header = request.getHeader("Authorization");
		System.out.println("🔍 [Filter] Authorization Header: " + header);

		if (header == null || !header.startsWith("Bearer ")) {
			System.out.println("❌ [Filter] 沒帶 Token 或格式錯誤 (沒有 Bearer )，放行給 Security 處理 (預期會 401)");
			filterChain.doFilter(request, response);
			return;
		}

		// 2. 解析 Token
		String token = header.substring(7);
		try {
			System.out.println("🔍 [Filter] 開始驗證 Firebase Token...");
			FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);

			String uid = decodedToken.getUid();
			System.out.println("✅ [Filter] 驗證成功! UID: " + uid);

			// 3. 設定身分
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(uid,
					decodedToken, new ArrayList<>());
			SecurityContextHolder.getContext().setAuthentication(authentication);
			System.out.println("✅ [Filter] SecurityContext 已設定完成");

		} catch (FirebaseAuthException e) {
			System.err.println("💥 [Filter] Firebase 驗證失敗: " + e.getMessage());
			// 這裡不需要 throw，因為 SecurityContext 沒設定，後面自然會 401
		} catch (Exception e) {
			System.err.println("💥 [Filter] 未知錯誤: " + e.getMessage());
			e.printStackTrace();
		}

		filterChain.doFilter(request, response);
	}
}