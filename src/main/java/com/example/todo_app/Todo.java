package com.example.todo_app;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity // 1. 告訴 Spring 這是一個要存入資料庫的實體
@Data   // 2. Lombok 魔法：自動幫我們產生 Getter, Setter, toString 等方法
public class Todo {

    @Id // 3. 這是主鍵 (Primary Key)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 4. ID 自動遞增 (Auto Increment)
    private Long id;

    private String title;       // 待辦事項標題
    private boolean completed;  // 是否完成
}