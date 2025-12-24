package com.example.todo_app.repository;

import com.example.todo_app.model.Category; // 引入 model
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    // 查詢某個使用者的所有分類
    List<Category> findByUserId(Long userId);
}