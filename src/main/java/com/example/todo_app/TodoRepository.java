package com.example.todo_app;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// <Todo, Long> 的意思是：這個倉庫管理 "Todo" 資料，且它的主鍵 ID 是 "Long" 類型的
@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    // 這裡裡面什麼都不用寫！
    // Spring Boot 已經自動幫你變出了 save(), findAll(), findById(), delete() 等方法
}