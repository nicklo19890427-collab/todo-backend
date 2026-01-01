package com.example.todo_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.todo_app.model.Todo;
import com.example.todo_app.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    
    // 原有的方法
    List<Todo> findByUser(User user);

    // ✨ 新增：智慧搜尋查詢
    // 邏輯：如果傳入的參數是 NULL，該條件就會被忽略 (比如 categoryId IS NULL，就代表不限分類)
    @Query("SELECT t FROM Todo t WHERE t.user.id = :userId " +
        "AND (:categoryId IS NULL OR t.category.id = :categoryId) " +
        "AND (:priority IS NULL OR t.priority = :priority) " +
        "AND (:start IS NULL OR t.dueDate >= :start) " +
        "AND (:end IS NULL OR t.dueDate <= :end)")
    List<Todo> search(@Param("userId") Long userId, 
                    @Param("categoryId") Long categoryId, 
                    @Param("priority") String priority, 
                    @Param("start") LocalDateTime start, 
                    @Param("end") LocalDateTime end);
}