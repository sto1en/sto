package com.example.sto.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    // Простой GET-запрос
    @GetMapping("/hello")
    public String hello() {
        return "Привет из Spring Boot!";
    }

    // Возвращаем JSON-объект
    @GetMapping("/info")
    public Map<String, String> info() {
        return Map.of(
                "name", "Sto",
                "version", "1.0.0",
                "status", "online"
        );
    }

    // Возвращаем список
    @GetMapping("/users")
    public List<Map<String, String>> users() {
        return List.of(
                Map.of("id", "1", "name", "Иван", "email", "ivan@example.com"),
                Map.of("id", "2", "name", "Мария", "email", "maria@example.com"),
                Map.of("id", "3", "name", "Петр", "email", "petr@example.com")
        );
    }
}