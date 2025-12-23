package com.example.todo_app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil; // 注入我們剛剛寫好的發證機器

    // --- 動作 1: 註冊 (Register) ---
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return "註冊失敗：帳號已存在";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "註冊成功！";
    }

    // --- 動作 2: 登入 (Login) ---
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody User user) {
        // 1. 找找看有沒有這個帳號
        User dbUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("登入失敗：帳號不存在"));

        // 2. 檢查密碼對不對 (因為資料庫是加密的，所以要用 matches 方法比對)
        if (!passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
            throw new RuntimeException("登入失敗：密碼錯誤");
        }

        // 3. 帳密都對了！發一張身分證 (Token) 給他
        String token = jwtUtil.generateToken(dbUser.getUsername());

        // 4. 回傳 JSON 格式: { "token": "eyJhGcF..." }
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("username", dbUser.getUsername());
        
        return response;
    }
}