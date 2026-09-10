package com.example.sto.service;

import com.example.sto.model.ClientRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class TelegramService {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.chat.id}")
    private String chatId;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendBookingNotification(ClientRequest request) {
        String text = buildMessage(request);

        String url = UriComponentsBuilder
                .fromHttpUrl("https://api.telegram.org/bot" + botToken + "/sendMessage")
                .queryParam("chat_id", chatId)
                .queryParam("text", text)
                .queryParam("parse_mode", "HTML")
                .toUriString();

        restTemplate.getForObject(url, String.class);
    }

    private String buildMessage(ClientRequest r) {
        return "🔔 <b>Новая заявка на СТО</b>\n\n"
                + "👤 <b>Имя:</b> " + safe(r.getName()) + "\n"
                + "📞 <b>Телефон:</b> " + safe(r.getPhoneNumber()) + "\n"
                + "🚗 <b>Автомобиль:</b> " + safe(r.getCar()) + "\n"
                + "🛠 <b>Услуга:</b> " + safe(r.getCarService());
    }

    private String safe(String value) {
        if (value == null || value.isBlank()) return "—";
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}