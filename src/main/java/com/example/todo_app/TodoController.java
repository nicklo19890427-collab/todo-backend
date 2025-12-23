package com.example.todo_app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository; // 我們需要這個來找「現在是誰在操作」

    // --- 小工具：取得目前登入的使用者 ---
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) authentication.getPrincipal(); // 從身分證裡拿出帳號
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // --- 動作 1: 只查詢「自己」的待辦事項 ---
    @GetMapping
    public List<Todo> getAllTodos() {
        User currentUser = getCurrentUser();
        return todoRepository.findByUser(currentUser); // 只找這個人的
    }

    // --- 動作 2: 新增時，自動填入「主人」是誰 ---
    @PostMapping
    public Todo createTodo(@RequestBody Todo todo) {
        User currentUser = getCurrentUser();
        todo.setUser(currentUser); // 蓋上印章，標記這是誰的
        return todoRepository.save(todo);
    }

    // --- 動作 3: 修改時，確保是自己的才能改 ---
    @PutMapping("/{id}")
    public Todo updateTodo(@PathVariable Long id, @RequestBody Todo todoDetails) {
        User currentUser = getCurrentUser();
        
        // 找這筆資料，而且要確認「主人」是目前登入者
        Todo todo = todoRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(currentUser.getId())) // 資安關鍵：檢查 User ID 是否相符
                .orElseThrow(() -> new RuntimeException("Todo not found or not authorized"));

        todo.setTitle(todoDetails.getTitle());
        todo.setCompleted(todoDetails.isCompleted());

        return todoRepository.save(todo);
    }

    // --- 動作 4: 刪除時，確保是自己的才能刪 ---
    @DeleteMapping("/{id}")
    public String deleteTodo(@PathVariable Long id) {
        User currentUser = getCurrentUser();

        Todo todo = todoRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(currentUser.getId())) // 資安關鍵
                .orElseThrow(() -> new RuntimeException("Todo not found or not authorized"));

        todoRepository.delete(todo);
        return "Todo deleted";
    }
}