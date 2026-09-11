package com.example.sto.controller;

import com.example.sto.dto.ServiceCategoryDto;
import com.example.sto.dto.ServiceItemDto;
import com.example.sto.model.ServiceCategory;
import com.example.sto.repository.ServiceCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class ApiController {

    @Autowired
    private ServiceCategoryRepository categoryRepository;

    @GetMapping("/info")
    public Map<String, String> getStoInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("name", "СТО Вязовский");
        info.put("schedule", "Удобный график работы");
        info.put("reviewsCount", "250+");
        info.put("rating", "5.0");
        return info;
    }

    @Transactional(readOnly = true)
    @GetMapping("/services")
    public List<ServiceCategoryDto> getServices() {
        return categoryRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    private ServiceCategoryDto toDto(ServiceCategory category) {
        List<ServiceItemDto> itemDtos = category.getItems().stream()
                .map(item -> new ServiceItemDto(
                        item.getId(),
                        item.getName(),
                        item.getPrice(),
                        item.getDescription()
                ))
                .toList();

        return new ServiceCategoryDto(
                category.getId(),
                category.getKey(),
                category.getTitle(),
                itemDtos
        );
    }
}