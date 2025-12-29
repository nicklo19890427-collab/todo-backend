package com.example.todo_app.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "todos") // 👈 記得補回這個，對應資料庫表格名稱
@Data // 👈 Lombok 會自動幫你產生 Getter/Setter，不用自己寫
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private boolean completed;

    // --- 關聯 User ---
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    // --- 關聯 Category ---
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // --- 新增欄位 ---

    // 優先級 (預設 LOW)
    @Column(nullable = false)
    private String priority = "LOW"; 

    // 截止日期
    private LocalDate dueDate;

    // 🎉 沒了！Getter 和 Setter 都不用寫，Lombok 幫你搞定。
}