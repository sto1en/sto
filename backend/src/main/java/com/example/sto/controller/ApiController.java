package com.example.sto.controller;

import com.example.sto.model.ServiceCategory;
import com.example.sto.model.ServiceItem;
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
    public List<ServiceCategory> getServices() {
        return List.of(
                new ServiceCategory(
                        "engine",
                        "Двигатель внутреннего сгорания",
                        List.of(
                                new ServiceItem("Замена масла и фильтра", "от 1 500 ₽", ""),
                                new ServiceItem("Диагностика двигателя", "от 1 000 ₽", ""),
                                new ServiceItem("Замена ремня ГРМ", "от 6 500 ₽", ""),
                                new ServiceItem("Замена свечей зажигания", "от 1 800 ₽", ""),
                                new ServiceItem("Промывка форсунок", "от 3 500 ₽", ""),
                                new ServiceItem("Капитальный ремонт ДВС", "от 45 000 ₽", "")
                        )
                ),
                new ServiceCategory(
                        "body",
                        "Кузов",
                        List.of(
                                new ServiceItem("Полировка кузова", "от 4 500 ₽", ""),
                                new ServiceItem("Удаление вмятин без покраски", "от 2 500 ₽", ""),
                                new ServiceItem("Локальная покраска детали", "от 6 000 ₽", ""),
                                new ServiceItem("Замена бампера", "от 3 500 ₽", ""),
                                new ServiceItem("Антикоррозийная обработка", "от 8 000 ₽", "")
                        )
                ),
                new ServiceCategory(
                        "to",
                        "Техническое обслуживание",
                        List.of(
                                new ServiceItem("ТО-1 (базовое)", "от 4 500 ₽", ""),
                                new ServiceItem("ТО-2 (расширенное)", "от 8 000 ₽", ""),
                                new ServiceItem("Замена воздушного фильтра", "от 800 ₽", ""),
                                new ServiceItem("Замена салонного фильтра", "от 1 200 ₽", ""),
                                new ServiceItem("Замена тормозной жидкости", "от 2 000 ₽", ""),
                                new ServiceItem("Замена охлаждающей жидкости", "от 2 500 ₽", "")
                        )
                ),
                new ServiceCategory(
                        "transmission",
                        "Трансмиссия",
                        List.of(
                                new ServiceItem("Замена масла в АКПП", "от 6 500 ₽", ""),
                                new ServiceItem("Замена масла в МКПП", "от 2 500 ₽", ""),
                                new ServiceItem("Замена сцепления", "от 12 000 ₽", ""),
                                new ServiceItem("Ремонт АКПП", "от 35 000 ₽", ""),
                                new ServiceItem("Замена приводов (ШРУС)", "от 4 500 ₽", "")
                        )
                ),
                new ServiceCategory(
                        "suspension",
                        "Ходовая",
                        List.of(
                                new ServiceItem("Диагностика подвески", "от 800 ₽", ""),
                                new ServiceItem("Замена амортизаторов", "от 5 000 ₽", ""),
                                new ServiceItem("Замена пружин", "от 4 000 ₽", ""),
                                new ServiceItem("Замена рычагов", "от 3 500 ₽", ""),
                                new ServiceItem("Замена шаровых опор", "от 2 500 ₽", ""),
                                new ServiceItem("Развал-схождение", "от 2 500 ₽", "")
                        )
                )
        );
    }
}