package com.example.todo_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.todo_app.model.Todo;
import com.example.todo_app.model.User;

import java.util.List; // 記得 import List

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    // 新增功能：找出「某個特定使用者」的所有待辦事項
    List<Todo> findByUser(User user);
}

