package com.example.todo_app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // 1. 告訴 Spring 這是一個 API 入口，回傳的是 JSON 資料
@RequestMapping("/api/todos") // 2. 設定這家店的地址是 http://localhost:8080/api/todos
public class TodoController {

    @Autowired // 3. 自動把剛剛寫好的倉庫 (Repository) 注入進來
    private TodoRepository todoRepository;

    // --- 動作 1: 查詢所有待辦事項 (GET) ---
    @GetMapping
    public List<Todo> getAllTodos() {
        // 呼叫倉庫，把所有資料拿出來
        return todoRepository.findAll();
    }

    // --- 動作 2: 新增一個待辦事項 (POST) ---
    @PostMapping
    public Todo createTodo(@RequestBody Todo todo) {
        // 1. 前端傳來的 JSON 會自動變成 todo 物件
        // 2. 呼叫倉庫，把資料存進資料庫
        return todoRepository.save(todo);
    }

    // --- 動作 3: 修改待辦事項 (PUT) ---
    // 網址會像這樣: /api/todos/1 (代表要改 ID 為 1 的那一筆)
    @PutMapping("/{id}")
    public Todo updateTodo(@PathVariable Long id, @RequestBody Todo todoDetails) {
        // 1. 先去倉庫找找看有沒有這筆 ID
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found with id: " + id));

        // 2. 找到了！更新內容
        todo.setTitle(todoDetails.getTitle());
        todo.setCompleted(todoDetails.isCompleted());

        // 3. 存回倉庫 (Save 既能新增也能更新)
        return todoRepository.save(todo);
    }

    // --- 動作 4: 刪除待辦事項 (DELETE) ---
    // 網址會像這樣: /api/todos/1
    @DeleteMapping("/{id}")
    public String deleteTodo(@PathVariable Long id) {
        // 1. 先去倉庫找找看有沒有這筆 ID
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found with id: " + id));

        // 2. 狠心刪除
        todoRepository.delete(todo);

        return "Todo with id " + id + " has been deleted!";
    }
}