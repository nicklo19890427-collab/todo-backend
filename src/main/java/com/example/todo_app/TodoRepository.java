package com.example.todo_app;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List; // 記得 import List

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    // 新增功能：找出「某個特定使用者」的所有待辦事項
    List<Todo> findByUser(User user);
}