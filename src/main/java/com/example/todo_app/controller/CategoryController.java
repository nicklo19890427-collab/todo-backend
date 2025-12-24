package com.example.todo_app.controller;

import com.example.todo_app.model.Category;
import com.example.todo_app.model.User;
import com.example.todo_app.repository.CategoryRepository;
import com.example.todo_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:5173") // 允許前端存取
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    // 取得當前使用者的所有分類
    @GetMapping
    public List<Category> getAllCategories() {
        User currentUser = getCurrentUser();
        return categoryRepository.findByUserId(currentUser.getId());
    }

    // 新增分類
    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        User currentUser = getCurrentUser();
        category.setUser(currentUser); // 設定主人
        return categoryRepository.save(category);
    }

    // 輔助方法：取得當前登入的使用者 (跟 TodoController 一樣)
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}