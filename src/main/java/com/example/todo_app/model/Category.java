package com.example.todo_app.model; // 👈 如果你有分資料夾，請改成對應的 package (例如 .model)

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // ✨ 新增：圖示代碼 (例如 "work", "home", "star")
    // 給一個預設值 "folder"，避免舊資料壞掉
    @Column(nullable = false)
    private String icon = "folder";

    // 關聯：多個分類屬於一個使用者
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore // 重要：避免 JSON 無窮迴圈
    private User user;

    public Category() {}

    public Category(String name, String icon, User user) { // 建構子也可以更新一下
        this.name = name;
        this.icon = icon != null ? icon : "folder";
        this.user = user;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; } // ✨ Setter

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}