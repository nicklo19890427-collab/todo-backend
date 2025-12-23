package com.example.todo_app;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 從 HTTP Header 取得 "Authorization" 欄位
        String authHeader = request.getHeader("Authorization");

        // 2. 檢查 Header 是否以 "Bearer " 開頭 (這是 JWT 的標準格式)
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // 去掉 "Bearer " 前面的 7 個字元，剩下就是 Token 本體

            try {
                // 3. 驗證 Token 是否有效，並取出裡面的帳號 (Username)
                // 如果 Token 是偽造或過期的，這裡會直接拋出錯誤，跳到 catch
                String username = jwtUtil.validateTokenAndGetUsername(token);

                // 4. 如果驗證成功，就告訴 Spring Security：「這個人通過驗證了！」
                // (這裡我們暫時給他空的權限列表 Collections.emptyList()，因為還沒做角色管理)
                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
                
                // 把身分證放到「保險箱 (SecurityContext)」裡，讓後面的程式可以使用
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                // 如果驗證失敗 (例如 Token 過期)，就什麼都不做，讓請求繼續往下走
                // 因為沒有設 Authentication，後面的保全系統會自動把它擋下來
                System.out.println("JWT 驗證失敗: " + e.getMessage());
            }
        }

        // 5. 繼續執行下一個過濾器 (放行)
        filterChain.doFilter(request, response);
    }
}