package com.example.todo_app;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore; // 記得加這個 import

@Entity
@Data
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private boolean completed;

    // --- 新增這段：設定這筆 Todo 屬於哪個 User ---
    @ManyToOne // 多對一：一個使用者可以有很多 Todo
    @JoinColumn(name = "user_id") // 資料庫欄位名稱叫 user_id
    @JsonIgnore // 重要！回傳 JSON 時，不要把使用者的密碼也一起回傳，避免無限迴圈
    private User user;
}