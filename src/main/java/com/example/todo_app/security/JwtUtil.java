package com.example.todo_app.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct; // 記得加這個
import org.springframework.beans.factory.annotation.Value; // 記得加這個
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // 1. 改用 @Value 注入設定檔裡的 jwt.secret
    @Value("${jwt.secret}")
    private String secret;

    private Key key;

    // 2. 初始化方法：當 Spring 建立這個物件，且把 secret 填好之後，會自動執行這個方法
    @PostConstruct
    public void init() {
        // 把讀到的字串轉成密鑰物件
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // 3. 產生 Token (原本的 static 變數 KEY 改成 this.key)
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(this.key, SignatureAlgorithm.HS256) // 使用實體變數 key
                .compact();
    }

    // 4. 驗證 Token
    public String validateTokenAndGetUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(this.key) // 使用實體變數 key
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}