package com.example.todo_app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.todo_app.model.Todo;
import com.example.todo_app.model.User;
import com.example.todo_app.repository.TodoRepository;
import com.example.todo_app.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    // ... (getCurrentUser 方法保持不變) ...
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) authentication.getPrincipal();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // --- 原有的 CRUD (getAll, create, update, delete) 保持不變 --- 
    @GetMapping
    public List<Todo> getAllTodos() {
        User currentUser = getCurrentUser();
        return todoRepository.findByUser(currentUser);
    }
    
    @PostMapping
    public Todo createTodo(@RequestBody Todo todo) {
        User currentUser = getCurrentUser();
        todo.setUser(currentUser);
        return todoRepository.save(todo);
    }

    @PutMapping("/{id}")
    public Todo updateTodo(@PathVariable Long id, @RequestBody Todo todoDetails) {
        User currentUser = getCurrentUser();
        Todo todo = todoRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(currentUser.getId()))
                .orElseThrow(() -> new RuntimeException("Todo not found or not authorized"));

        todo.setTitle(todoDetails.getTitle());
        todo.setCompleted(todoDetails.isCompleted());
        // 記得也要允許更新這些欄位
        todo.setCategory(todoDetails.getCategory());
        todo.setPriority(todoDetails.getPriority());
        todo.setDueDate(todoDetails.getDueDate());

        return todoRepository.save(todo);
    }

    @DeleteMapping("/{id}")
    public String deleteTodo(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        Todo todo = todoRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(currentUser.getId()))
                .orElseThrow(() -> new RuntimeException("Todo not found or not authorized"));

        todoRepository.delete(todo);
        return "Todo deleted";
    }

    // ✨ 新增：搜尋接口
    // GET /api/todos/search?categoryId=1&priority=HIGH&date=2025-12-25
    @GetMapping("/search")
    public List<Todo> searchTodos(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        
        User currentUser = getCurrentUser();
        
        // 處理日期範圍：如果使用者選了一天，我們就找那一天 00:00 ~ 23:59 的所有任務
        LocalDateTime start = null;
        LocalDateTime end = null;
        
        if (date != null) {
            LocalDate localDate = date.toLocalDate();
            start = localDate.atStartOfDay();      // 2025-12-25 00:00:00
            end = localDate.atTime(LocalTime.MAX); // 2025-12-25 23:59:59.999
        }

        // 如果 priority 是空字串或 "ALL"，視為 null (不篩選)
        if (priority != null && (priority.isEmpty() || priority.equals("ALL"))) {
            priority = null;
        }

        return todoRepository.search(currentUser.getId(), categoryId, priority, start, end);
    }
}