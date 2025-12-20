package com.en_chu.calculator_api_spring.security;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 這就是你的 verifyIdToken 函數的 Spring Boot 版本 (Middleware) 每個 Request 進來都會先經過這裡
 */
public class FirebaseTokenFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {

		// 1. 取得 Header (對應你的 !idToken 檢查)
		String header = request.getHeader("Authorization");

		// 2. 檢查是否為 Bearer Token
		if (header == null || !header.startsWith("Bearer ")) {
			// 如果沒帶 Token，就直接放行 (讓後面的 SecurityConfig 決定要不要擋 403)
			// 或是你也可以在這裡直接 throw Exception
			filterChain.doFilter(request, response);
			return;
		}

		// 3. 去掉 'Bearer ' (對應你的 replace)
		String token = header.substring(7);

		try {
			// 4. 呼叫 Firebase 驗證 (對應 this.auth.verifyIdToken)
			FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);

			// 5. 驗證成功，建立 "Authentication" 物件 (這就是 Java 裡的身分證)
			// decodedToken.getUid() 就是使用者的 ID
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					decodedToken.getUid(), // Principal (主角 ID)
					decodedToken, // Credentials (詳細憑證資料)
					new ArrayList<>() // Authorities (權限角色，目前先留空)
			);

			// 6. 把身分證存入 SecurityContext (全域變數，Controller 隨時可取用)
			SecurityContextHolder.getContext().setAuthentication(authentication);

		} catch (FirebaseAuthException e) {
			// 對應你的 "向開發人員抱怨Q_Q"
			// 這裡會導致 401 Unauthorized
			System.err.println("Firebase Token 驗證失敗: " + e.getMessage());
		}

		// 7. 繼續往下走 (進入 Controller)
		filterChain.doFilter(request, response);
	}
}