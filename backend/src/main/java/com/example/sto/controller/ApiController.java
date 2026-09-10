package com.example.sto.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000") // Разрешаем запросы с React
public class ApiController {

    @GetMapping("/info")
    public Map<String, String> getStoInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("name", "СТО Вязовский");
        info.put("schedule", "Удобный график работы");
        info.put("reviewsCount", "250+");
        info.put("rating", "5.0");
        return info;
    }

    @GetMapping("/services")
    public String[] getServices() {
        return new String[]{"Диагностика", "Ремонт", "Записаться", "Отзывы"};
    }
}