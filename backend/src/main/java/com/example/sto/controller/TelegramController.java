package com.example.sto.controller;

import com.example.sto.model.ClientRequest;
import com.example.sto.service.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class TelegramController {

    @Autowired
    private TelegramService telegramService;

    @PostMapping("/booking")
    public ResponseEntity<String> handleBooking(@RequestBody ClientRequest request) {
        telegramService.sendBookingNotification(request);
        return ResponseEntity.ok("OK");
    }
}