package com.example.todo_app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
import java.util.stream.Collectors;
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

    @GetMapping("/search")
    public List<Todo> searchTodos(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date,
            // ✨ 新增排序參數 (預設依 priority 排序)
            @RequestParam(defaultValue = "priority") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
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
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }

        // ✨ 處理排序邏輯
        if ("priority".equals(sortBy)) {
            // 如果是依優先級排序，先用 ID 排序取出資料，再於記憶體中排序
            // 因為 DB 中的 HIGH/MEDIUM/LOW 字母順序不符合邏輯 (H < L < M ? 不對)
            List<Todo> todos = todoRepository.search(currentUser.getId(), keyword, categoryId, priority, start, end, Sort.by(Sort.Direction.DESC, "id"));
            
            return todos.stream().sorted((t1, t2) -> {
                int p1 = getPriorityValue(t1.getPriority());
                int p2 = getPriorityValue(t2.getPriority());
                // desc: 高 -> 低 (3 -> 1)
                return "desc".equalsIgnoreCase(direction) ? Integer.compare(p2, p1) : Integer.compare(p1, p2);
            }).collect(Collectors.toList());
        } else {
            // 其他欄位 (如 dueDate, id) 直接交給資料庫排序
            Sort.Direction dir = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
            Sort sort = Sort.by(dir, sortBy);
            return todoRepository.search(currentUser.getId(), keyword, categoryId, priority, start, end, sort);
        }
    }

    // 輔助方法：將優先級字串轉為數字 (數字越大優先級越高)
    private int getPriorityValue(String priority) {
        if ("HIGH".equals(priority)) return 3;
        if ("MEDIUM".equals(priority)) return 2;
        if ("LOW".equals(priority)) return 1;
        return 0;
    }
}