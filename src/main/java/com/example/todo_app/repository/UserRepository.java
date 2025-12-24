package com.example.todo_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.todo_app.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 我們需要一個額外功能：透過 "username" 找到使用者
    // Spring Data JPA 很聰明，只要方法名稱寫對，它會自動幫你產生 SQL
    Optional<User> findByUsername(String username);
}

