package com.example.todo_app;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users") // 為了避免跟 SQL 的保留字 "user" 衝突，我們習慣把表名取為 "users" (複數)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 設定為 unique = true，代表帳號不能重複
    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    // 我們先簡單一點，不需要 Email，有帳號密碼就好
}