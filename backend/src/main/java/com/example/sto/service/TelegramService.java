package com.example.sto.service;

import com.example.sto.model.ClientRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TelegramService {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.chat.id}")
    private String chatId;

    @Value("${telegram.proxy.host:}")
    private String proxyHost;

    @Value("${telegram.proxy.port:0}")
    private int proxyPort;

    @Value("${telegram.proxy.type:SOCKS}")
    private String proxyType;

    @Value("${sto.site.url:https://mousy-herald-womanless.ngrok-free.dev/}")
    private String siteUrl;

    @Value("${sto.telegram.main:https://t.me/STOVyazovskiy}")
    private String mainAccountUrl;

    private final RestTemplate restTemplate;

    public TelegramService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        if (proxyHost != null && !proxyHost.isEmpty() && proxyPort > 0) {
            Proxy.Type type = "HTTP".equalsIgnoreCase(proxyType) ? Proxy.Type.HTTP : Proxy.Type.SOCKS;
            factory.setProxy(new Proxy(type, new InetSocketAddress(proxyHost, proxyPort)));
        }
        factory.setConnectTimeout(15000);
        factory.setReadTimeout(15000);
        this.restTemplate = new RestTemplate(factory);
    }

    public void sendBookingNotification(ClientRequest request) {
        String text = buildMessage(request);
        sendMessage(chatId, text, null);
    }

    public void sendMessage(String chatId, String text) {
        sendMessage(chatId, text, null);
    }

    public void sendMessage(String chatId, String text, Map<String, Object> replyMarkup) {
        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";
        Map<String, Object> body = new HashMap<>();
        body.put("chat_id", chatId);
        body.put("text", text);
        body.put("parse_mode", "HTML");
        body.put("disable_web_page_preview", true);

        if (replyMarkup != null) {
            body.put("reply_markup", replyMarkup);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        try {
            restTemplate.postForEntity(url, entity, String.class);
        } catch (Exception e) {
            System.err.println("Ошибка отправки сообщения в Telegram: " + e.getMessage());
        }
    }

    /** Главное меню админа — 3 команды кнопками */
    public Map<String, Object> helpKeyboard() {
        Map<String, Object> addCategoryBtn = new HashMap<>();
        addCategoryBtn.put("text", "➕ Добавить категорию");
        addCategoryBtn.put("callback_data", "cmd:add_category");

        Map<String, Object> addServiceBtn = new HashMap<>();
        addServiceBtn.put("text", "➕ Добавить услугу");
        addServiceBtn.put("callback_data", "cmd:add_service");

        Map<String, Object> listBtn = new HashMap<>();
        listBtn.put("text", "📋 Все категории");
        listBtn.put("callback_data", "cmd:list");

        List<List<Map<String, Object>>> rows = new ArrayList<>();
        rows.add(List.of(addCategoryBtn));
        rows.add(List.of(addServiceBtn));
        rows.add(List.of(listBtn));

        Map<String, Object> keyboard = new HashMap<>();
        keyboard.put("inline_keyboard", rows);
        return keyboard;
    }

    /** Главное меню обычного пользователя */
    public Map<String, Object> userMenuKeyboard() {
        Map<String, Object> bookBtn = new HashMap<>();
        bookBtn.put("text", "📝 Записаться");
        bookBtn.put("callback_data", "user:book");

        Map<String, Object> accountBtn = new HashMap<>();
        accountBtn.put("text", "👤 Основной аккаунт");
        accountBtn.put("url", mainAccountUrl);

        Map<String, Object> siteBtn = new HashMap<>();
        siteBtn.put("text", "🌐 На сайт");
        siteBtn.put("url", siteUrl);

        List<List<Map<String, Object>>> rows = new ArrayList<>();
        rows.add(List.of(bookBtn));
        rows.add(List.of(accountBtn));
        rows.add(List.of(siteBtn));

        Map<String, Object> keyboard = new HashMap<>();
        keyboard.put("inline_keyboard", rows);
        return keyboard;
    }

    /** Клавиатура отмены для обычного пользователя */
    public Map<String, Object> userCancelKeyboard() {
        Map<String, Object> cancelBtn = new HashMap<>();
        cancelBtn.put("text", "✖ Отменить");
        cancelBtn.put("callback_data", "user:cancel");

        Map<String, Object> keyboard = new HashMap<>();
        keyboard.put("inline_keyboard", List.of(List.of(cancelBtn)));
        return keyboard;
    }

    /** Одна кнопка «▲ Назад» — ведёт в главное меню */
    public Map<String, Object> backKeyboard() {
        Map<String, Object> backButton = new HashMap<>();
        backButton.put("text", "▲ Назад");
        backButton.put("callback_data", "back_to_menu");

        Map<String, Object> keyboard = new HashMap<>();
        keyboard.put("inline_keyboard", List.of(List.of(backButton)));
        return keyboard;
    }

    /** Одна кнопка «✖ Отменить» — ведёт в меню админа */
    public Map<String, Object> cancelBackKeyboard() {
        Map<String, Object> cancelBtn = new HashMap<>();
        cancelBtn.put("text", "✖ Отменить");
        cancelBtn.put("callback_data", "cancel_to_menu");

        Map<String, Object> keyboard = new HashMap<>();
        keyboard.put("inline_keyboard", List.of(List.of(cancelBtn)));
        return keyboard;
    }

    /** Одна кнопка «✖ Отменить» — ведёт в меню админа */
    public Map<String, Object> cancelOnlyKeyboard() {
        Map<String, Object> cancelBtn = new HashMap<>();
        cancelBtn.put("text", "✖ Отменить");
        cancelBtn.put("callback_data", "cancel_to_menu");

        Map<String, Object> keyboard = new HashMap<>();
        keyboard.put("inline_keyboard", List.of(List.of(cancelBtn)));
        return keyboard;
    }

    /**
     * Клавиатура для диалога выбора категории (при добавлении услуги):
     *   [ ✖ Отменить ] [ ▲ Назад ]
     * Отменить — в меню админа, Назад — к списку категорий.
     */
    public Map<String, Object> categoryPickKeyboard() {
        Map<String, Object> cancelBtn = new HashMap<>();
        cancelBtn.put("text", "✖ Отменить");
        cancelBtn.put("callback_data", "cancel_to_menu");

        Map<String, Object> backBtn = new HashMap<>();
        backBtn.put("text", "▲ Назад");
        backBtn.put("callback_data", "back_to_category_list");

        List<Map<String, Object>> rowList = new ArrayList<>();
        rowList.add(cancelBtn);
        rowList.add(backBtn);

        Map<String, Object> keyboard = new HashMap<>();
        keyboard.put("inline_keyboard", List.of(rowList));
        return keyboard;
    }

    /**
     * Клавиатура для диалога ввода услуг:
     *   [ ✖ Отменить ] [ ▲ Назад ]
     * Отменить — в меню админа, Назад — к выбору категории.
     */
    public Map<String, Object> servicesInputKeyboard() {
        Map<String, Object> cancelBtn = new HashMap<>();
        cancelBtn.put("text", "✖ Отменить");
        cancelBtn.put("callback_data", "cancel_to_menu");

        Map<String, Object> backBtn = new HashMap<>();
        backBtn.put("text", "▲ Назад");
        backBtn.put("callback_data", "back_to_category_list");

        List<Map<String, Object>> rowList = new ArrayList<>();
        rowList.add(cancelBtn);
        rowList.add(backBtn);

        Map<String, Object> keyboard = new HashMap<>();
        keyboard.put("inline_keyboard", List.of(rowList));
        return keyboard;
    }

    /** Клавиатура подтверждения удаления услуги */
    public Map<String, Object> confirmDeleteKeyboard(Long serviceId, String categoryKey) {
        Map<String, Object> cancelBtn = new HashMap<>();
        cancelBtn.put("text", "✖ Отменить");
        cancelBtn.put("callback_data", "del_cancel:" + categoryKey);

        Map<String, Object> confirmBtn = new HashMap<>();
        confirmBtn.put("text", "✓ Подтвердить");
        confirmBtn.put("callback_data", "del_confirm:" + serviceId + ":" + categoryKey);

        List<Map<String, Object>> rowList = new ArrayList<>();
        rowList.add(cancelBtn);
        rowList.add(confirmBtn);

        Map<String, Object> keyboard = new HashMap<>();
        keyboard.put("inline_keyboard", List.of(rowList));
        return keyboard;
    }

    /** Клавиатура со списком категорий */
    public Map<String, Object> categoryKeyboard(List<Map<String, String>> categories) {
        List<List<Map<String, Object>>> rows = new ArrayList<>();
        for (Map<String, String> cat : categories) {
            String key = cat.get("key");
            String title = cat.get("title");
            String count = cat.get("count");

            Map<String, Object> showBtn = new HashMap<>();
            showBtn.put("text", key + " — " + title + " (" + count + ")");
            showBtn.put("callback_data", "show_category:" + key);

            Map<String, Object> editBtn = new HashMap<>();
            editBtn.put("text", "✏ Редактировать");
            editBtn.put("callback_data", "cat_edit:" + key);

            Map<String, Object> delBtn = new HashMap<>();
            delBtn.put("text", "🗑 Удалить");
            delBtn.put("callback_data", "cat_del:" + key);

            rows.add(List.of(showBtn));
            rows.add(List.of(editBtn, delBtn));
        }

        Map<String, Object> backButton = new HashMap<>();
        backButton.put("text", "▲ Назад");
        backButton.put("callback_data", "back_to_menu");
        rows.add(List.of(backButton));

        Map<String, Object> keyboard = new HashMap<>();
        keyboard.put("inline_keyboard", rows);
        return keyboard;
    }

    /**
     * Клавиатура со списком услуг.
     * Внизу: [ ➕ Добавить услугу ] и [ ▲ Назад ]
     */
    public Map<String, Object> servicesKeyboard(String categoryKey, List<Map<String, String>> services) {
        List<List<Map<String, Object>>> rows = new ArrayList<>();
        for (Map<String, String> svc : services) {
            String id = svc.get("id");
            String name = svc.get("name");
            String price = svc.get("price");

            Map<String, Object> showBtn = new HashMap<>();
            showBtn.put("text", name + " — " + price);
            showBtn.put("callback_data", "svc_show:" + id);

            Map<String, Object> editBtn = new HashMap<>();
            editBtn.put("text", "✏");
            editBtn.put("callback_data", "svc_edit:" + id);

            Map<String, Object> delBtn = new HashMap<>();
            delBtn.put("text", "🗑");
            delBtn.put("callback_data", "svc_del:" + id);

            rows.add(List.of(showBtn));
            rows.add(List.of(editBtn, delBtn));
        }

        Map<String, Object> addBtn = new HashMap<>();
        addBtn.put("text", "➕ Добавить услугу");
        addBtn.put("callback_data", "add_to_category:" + categoryKey);
        rows.add(List.of(addBtn));

        Map<String, Object> backBtn = new HashMap<>();
        backBtn.put("text", "▲ Назад");
        backBtn.put("callback_data", "back_to_menu");
        rows.add(List.of(backBtn));

        Map<String, Object> keyboard = new HashMap<>();
        keyboard.put("inline_keyboard", rows);
        return keyboard;
    }

    /**
     * Клавиатура для пустой категории:
     *   [ ➕ Добавить услугу ]
     *   [ ▲ Назад ]
     */
    public Map<String, Object> emptyCategoryKeyboard(String categoryKey) {
        Map<String, Object> addBtn = new HashMap<>();
        addBtn.put("text", "➕ Добавить услугу");
        addBtn.put("callback_data", "add_to_category:" + categoryKey);

        Map<String, Object> backBtn = new HashMap<>();
        backBtn.put("text", "▲ Назад");
        backBtn.put("callback_data", "back_to_menu");

        List<List<Map<String, Object>>> rows = new ArrayList<>();
        rows.add(List.of(addBtn));
        rows.add(List.of(backBtn));

        Map<String, Object> keyboard = new HashMap<>();
        keyboard.put("inline_keyboard", rows);
        return keyboard;
    }

    /** Клавиатура для карточки услуги */
    public Map<String, Object> serviceDetailsKeyboard(Long serviceId) {
        Map<String, Object> editBtn = new HashMap<>();
        editBtn.put("text", "✏ Редактировать");
        editBtn.put("callback_data", "svc_edit:" + serviceId);

        Map<String, Object> descBtn = new HashMap<>();
        descBtn.put("text", "📝 Описание");
        descBtn.put("callback_data", "svc_desc:" + serviceId);

        Map<String, Object> delBtn = new HashMap<>();
        delBtn.put("text", "🗑 Удалить");
        delBtn.put("callback_data", "svc_del:" + serviceId);

        Map<String, Object> backBtn = new HashMap<>();
        backBtn.put("text", "▲ Назад");
        backBtn.put("callback_data", "back_to_menu");

        List<List<Map<String, Object>>> rows = new ArrayList<>();
        rows.add(List.of(editBtn, descBtn));
        rows.add(List.of(delBtn));
        rows.add(List.of(backBtn));

        Map<String, Object> keyboard = new HashMap<>();
        keyboard.put("inline_keyboard", rows);
        return keyboard;
    }

    public void answerCallbackQuery(String callbackQueryId, String text) {
        String url = "https://api.telegram.org/bot" + botToken + "/answerCallbackQuery";
        Map<String, Object> body = new HashMap<>();
        body.put("callback_query_id", callbackQueryId);
        if (text != null) body.put("text", text);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        try {
            restTemplate.postForEntity(url, entity, String.class);
        } catch (Exception e) {
            System.err.println("Ошибка answerCallbackQuery: " + e.getMessage());
        }
    }

    public String getUpdates(long offset) {
        String url = "https://api.telegram.org/bot" + botToken + "/getUpdates"
                + "?offset=" + offset
                + "&timeout=5"
                + "&allowed_updates=[\"message\",\"callback_query\"]";
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Ошибка getUpdates: " + e.getMessage());
            return null;
        }
    }

    private String buildMessage(ClientRequest r) {
        return "🔔 <b>Новая запись!</b>\n\n"
                + "👤 <b>Имя:</b> <i>" + escape(r.getName()) + "</i>\n"
                + "📞 <b>Телефон:</b> <i>" + escape(normalizePhone(r.getPhoneNumber())) + "</i>\n"
                + "🚗 <b>Автомобиль:</b> <i>" + escape(r.getCar()) + "</i>\n"
                + "🛠 <b>Услуга:</b> <i>" + escape(r.getCarService()) + "</i>";
    }

    private String normalizePhone(String input) {
        if (input == null || input.isBlank()) return "—";
        String digits = input.replaceAll("\\D", "");

        if (digits.length() == 11) {
            if (digits.startsWith("7")) return "+" + digits;
            if (digits.startsWith("8")) return "+7" + digits.substring(1);
            return input.trim() + " (с опечаткой)";
        }
        if (digits.length() == 10) {
            if (digits.startsWith("9")) return "+7" + digits;
            return input.trim() + " (с опечаткой)";
        }
        return input.trim() + " (с опечаткой)";
    }

    private String escape(String value) {
        if (value == null || value.isBlank()) return "—";
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}