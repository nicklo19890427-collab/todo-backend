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

    // ✨ 修改搜尋接口：加入 keyword 參數
    @GetMapping("/search")
    public List<Todo> searchTodos(
            @RequestParam(required = false) String keyword, // 👈 新增
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        
        User currentUser = getCurrentUser();
        
        LocalDateTime start = null;
        LocalDateTime end = null;
        
        if (date != null) {
            LocalDate localDate = date.toLocalDate();
            start = localDate.atStartOfDay();
            end = localDate.atTime(LocalTime.MAX);
        }

        if (priority != null && (priority.isEmpty() || priority.equals("ALL"))) {
            priority = null;
        }

        // 處理 keyword：如果是空字串就轉成 null
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }

        // 呼叫 Repository (記得傳入 keyword)
        return todoRepository.search(currentUser.getId(), keyword, categoryId, priority, start, end);
    }
}