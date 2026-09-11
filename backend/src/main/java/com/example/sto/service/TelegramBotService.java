package com.example.sto.service;

import com.example.sto.model.ClientRequest;
import com.example.sto.model.ServiceCategory;
import com.example.sto.model.ServiceItem;
import com.example.sto.repository.ServiceCategoryRepository;
import com.example.sto.repository.ServiceItemRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TelegramBotService {

    @Autowired
    private TelegramService telegramService;

    @Autowired
    private ServiceCategoryRepository categoryRepository;

    @Autowired
    private ServiceItemRepository itemRepository;

    @Value("${telegram.admin.ids}")
    private String adminIdsRaw;

    @Value("${telegram.bot.enabled:true}")
    private boolean enabled;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<Long> adminIds = new ArrayList<>();
    private final Map<Long, UserState> userStates = new HashMap<>();
    private long lastUpdateId = 0;

    private enum State {
        IDLE,
        AWAITING_CATEGORY_TITLE,
        AWAITING_SERVICE_CATEGORY,
        AWAITING_SERVICES_INPUT,
        EDIT_SERVICE_NAME,
        EDIT_SERVICE_PRICE,
        EDIT_SERVICE_DESCRIPTION,
        EDIT_CATEGORY_TITLE,
        USER_BOOK_NAME,
        USER_BOOK_PHONE,
        USER_BOOK_CAR,
        USER_BOOK_SERVICE
    }

    private static class UserState {
        State state = State.IDLE;
        Long pendingCategoryId;
        Long editingServiceId;
        Long editingCategoryId;
        ClientRequest pendingBooking;
    }

    @PostConstruct
    public void init() {
        for (String raw : adminIdsRaw.split(",")) {
            String trimmed = raw.trim();
            if (!trimmed.isEmpty()) {
                try {
                    adminIds.add(Long.parseLong(trimmed));
                } catch (NumberFormatException e) {
                    System.err.println("Некорректный admin id: " + trimmed);
                }
            }
        }
        System.out.println(">>> TelegramBotService запущен. Админов: " + adminIds.size());
    }

    @Scheduled(fixedDelay = 2000)
    public void pollUpdates() {
        if (!enabled) return;

        String json = telegramService.getUpdates(lastUpdateId + 1);
        if (json == null) return;

        try {
            JsonNode root = objectMapper.readTree(json);
            if (!root.path("ok").asBoolean(false)) return;

            JsonNode result = root.path("result");
            if (!result.isArray() || result.isEmpty()) return;

            for (JsonNode update : result) {
                long updateId = update.path("update_id").asLong();
                lastUpdateId = Math.max(lastUpdateId, updateId);

                JsonNode callbackQuery = update.path("callback_query");
                if (!callbackQuery.isMissingNode()) {
                    handleCallback(update.path("callback_query"));
                    continue;
                }

                JsonNode message = update.path("message");
                if (message.isMissingNode()) continue;

                long chatId = message.path("chat").path("id").asLong();
                String text = message.path("text").asText("");
                if (text.isEmpty()) continue;

                handleAnyMessage(chatId, text);
            }
        } catch (Exception e) {
            System.err.println("Ошибка обработки updates: " + e.getMessage());
        }
    }

    // ===== Callback =====
    private void handleCallback(JsonNode callbackQuery) {
        long chatId = callbackQuery.path("message").path("chat").path("id").asLong();
        String callbackId = callbackQuery.path("id").asText();
        String data = callbackQuery.path("data").asText("");

        telegramService.answerCallbackQuery(callbackId, null);

        UserState state = userStates.computeIfAbsent(chatId, k -> new UserState());
        boolean isAdmin = adminIds.contains(chatId);

        // Callback обычного пользователя
        if (data.startsWith("user:")) {
            if ("user:book".equals(data)) {
                startUserBooking(chatId, state);
                return;
            }
            if ("user:cancel".equals(data) || "user:menu".equals(data)) {
                resetState(state);
                sendUserMenu(chatId);
                return;
            }
            sendUserMenu(chatId);
            return;
        }

        // Все остальные callback — только для админов
        if (!isAdmin) {
            sendUserMenu(chatId);
            return;
        }

        // Кнопки главного меню админа
        if (data.startsWith("cmd:")) {
            String command = "/" + data.substring("cmd:".length());
            handleAdminCommand(chatId, command, state);
            return;
        }

        if ("cancel_action".equals(data) || "cancel_to_menu".equals(data)) {
            resetState(state);
            sendAdminMenu(chatId);
            return;
        }

        if ("back_to_menu".equals(data)) {
            resetState(state);
            sendAdminMenu(chatId);
            return;
        }

        if ("back_to_category_list".equals(data)) {
            resetState(state);
            state.state = State.AWAITING_SERVICE_CATEGORY;
            sendCategoryListForPick(chatId);
            return;
        }

        if (data.startsWith("show_category:")) {
            String key = data.substring("show_category:".length());
            listServicesInCategory(chatId, key);
            return;
        }

        if (data.startsWith("add_to_category:")) {
            String key = data.substring("add_to_category:".length());
            startAddingToCategory(chatId, key, state);
            return;
        }

        if (data.startsWith("cat_edit:")) {
            String key = data.substring("cat_edit:".length());
            startEditCategory(chatId, key, state);
            return;
        }

        if (data.startsWith("cat_del:")) {
            String key = data.substring("cat_del:".length());
            deleteCategoryByKey(chatId, key);
            return;
        }

        if (data.startsWith("svc_show:")) {
            Long id = parseId(data.substring("svc_show:".length()));
            if (id != null) showServiceDetails(chatId, id);
            return;
        }

        if (data.startsWith("svc_edit:")) {
            Long id = parseId(data.substring("svc_edit:".length()));
            if (id != null) startEditService(chatId, id, state);
            return;
        }

        if (data.startsWith("svc_desc:")) {
            Long id = parseId(data.substring("svc_desc:".length()));
            if (id != null) startEditDescription(chatId, id, state);
            return;
        }

        if (data.startsWith("svc_del:")) {
            Long id = parseId(data.substring("svc_del:".length()));
            if (id != null) showDeleteConfirmation(chatId, id);
            return;
        }

        if (data.startsWith("del_confirm:")) {
            String[] parts = data.substring("del_confirm:".length()).split(":");
            Long id = parts.length >= 1 ? parseId(parts[0]) : null;
            if (id != null) performDeleteService(chatId, id);
            return;
        }

        if (data.startsWith("del_cancel:")) {
            String categoryKey = data.substring("del_cancel:".length());
            listServicesInCategory(chatId, categoryKey);
            return;
        }
    }

    private Long parseId(String s) {
        try {
            return Long.parseLong(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void resetState(UserState state) {
        state.state = State.IDLE;
        state.pendingCategoryId = null;
        state.editingServiceId = null;
        state.editingCategoryId = null;
        state.pendingBooking = null;
    }

    // ===== Маршрутизация сообщений =====
    private void handleAnyMessage(long chatId, String text) {
        UserState state = userStates.computeIfAbsent(chatId, k -> new UserState());
        String trimmed = text.trim();
        boolean isAdmin = adminIds.contains(chatId);

        if (state.state != State.IDLE) {
            if (state.state == State.USER_BOOK_NAME
                    || state.state == State.USER_BOOK_PHONE
                    || state.state == State.USER_BOOK_CAR
                    || state.state == State.USER_BOOK_SERVICE) {
                handleUserBookingInput(chatId, trimmed, state);
                return;
            }
            if (isAdmin) {
                handleAdminStateInput(chatId, text, state);
                return;
            }
            resetState(state);
            sendUserMenu(chatId);
            return;
        }

        if (trimmed.startsWith("/start")
                || trimmed.startsWith("/menu")
                || trimmed.startsWith("/help")) {
            resetState(state);
            sendUserMenu(chatId);
            return;
        }

        if (trimmed.startsWith("/admin")) {
            if (isAdmin) {
                resetState(state);
                sendAdminMenu(chatId);
            } else {
                sendUserMenu(chatId);
            }
            return;
        }

        if (trimmed.startsWith("/cancel")) {
            resetState(state);
            telegramService.sendMessage(String.valueOf(chatId),
                    "<b>Отменено.</b>",
                    telegramService.backKeyboard());
            return;
        }

        if (isAdmin && isAdminCommand(trimmed)) {
            handleAdminCommand(chatId, trimmed, state);
            return;
        }

        sendUserMenu(chatId);
    }

    private boolean isAdminCommand(String text) {
        return text.startsWith("/add_category")
                || text.startsWith("/add_service")
                || text.startsWith("/list");
    }

    // ===== Меню пользователя =====
    private void sendUserMenu(long chatId) {
        telegramService.sendMessage(String.valueOf(chatId),
                "👋 <b>Добро пожаловать в СТО Вязовский!</b>\n\n" +
                        "Выберите действие:",
                telegramService.userMenuKeyboard());
    }

    private void startUserBooking(long chatId, UserState state) {
        resetState(state);
        state.state = State.USER_BOOK_NAME;
        state.pendingBooking = new ClientRequest();

        telegramService.sendMessage(String.valueOf(chatId),
                "📝 <b>Запись на сервис</b>\n\n" +
                        "Введите ваше <b>имя</b>:",
                telegramService.userCancelKeyboard());
    }

    private void handleUserBookingInput(long chatId, String text, UserState state) {
        switch (state.state) {
            case USER_BOOK_NAME:
                if (text.isEmpty() || text.length() > 100) {
                    telegramService.sendMessage(String.valueOf(chatId),
                            "⚠️ <b>Введите имя (1–100 символов):</b>",
                            telegramService.userCancelKeyboard());
                    return;
                }
                state.pendingBooking.setName(text);
                state.state = State.USER_BOOK_PHONE;
                telegramService.sendMessage(String.valueOf(chatId),
                        "📞 Введите ваш <b>телефон</b>:",
                        telegramService.userCancelKeyboard());
                break;

            case USER_BOOK_PHONE:
                if (text.replaceAll("\\D", "").length() < 10) {
                    telegramService.sendMessage(String.valueOf(chatId),
                            "⚠️ <b>Введите корректный номер телефона:</b>",
                            telegramService.userCancelKeyboard());
                    return;
                }
                state.pendingBooking.setPhoneNumber(text);
                state.state = State.USER_BOOK_CAR;
                telegramService.sendMessage(String.valueOf(chatId),
                        "🚗 Введите ваш <b>автомобиль</b> (марка, модель):",
                        telegramService.userCancelKeyboard());
                break;

            case USER_BOOK_CAR:
                if (text.isEmpty() || text.length() > 200) {
                    telegramService.sendMessage(String.valueOf(chatId),
                            "⚠️ <b>Введите марку и модель автомобиля:</b>",
                            telegramService.userCancelKeyboard());
                    return;
                }
                state.pendingBooking.setCar(text);
                state.state = State.USER_BOOK_SERVICE;
                telegramService.sendMessage(String.valueOf(chatId),
                        "🛠 Введите <b>услугу</b> (или отправьте <code>-</code>, чтобы пропустить):",
                        telegramService.userCancelKeyboard());
                break;

            case USER_BOOK_SERVICE:
                if ("-".equals(text)) {
                    text = "";
                }
                state.pendingBooking.setCarService(text);

                telegramService.sendBookingNotification(state.pendingBooking);

                telegramService.sendMessage(String.valueOf(chatId),
                        "✅ <b>Заявка отправлена!</b>\n\n" +
                                "Менеджер свяжется с вами в течение 15 минут.",
                        telegramService.userMenuKeyboard());
                resetState(state);
                break;

            default:
                resetState(state);
                sendUserMenu(chatId);
        }
    }

    // ===== Меню админа =====
    private void sendAdminMenu(long chatId) {
        telegramService.sendMessage(String.valueOf(chatId),
                "<b>📋 Меню администратора:</b>",
                telegramService.helpKeyboard());
    }

    private void handleAdminCommand(long chatId, String text, UserState state) {
        String trimmedText = text.trim();

        if (trimmedText.startsWith("/add_category")) {
            resetState(state);
            state.state = State.AWAITING_CATEGORY_TITLE;
            telegramService.sendMessage(String.valueOf(chatId),
                    "📝 <b>Введите название новой категории:</b>",
                    telegramService.cancelBackKeyboard());
            return;
        }

        if (trimmedText.startsWith("/add_service")) {
            resetState(state);
            state.state = State.AWAITING_SERVICE_CATEGORY;
            sendCategoryListForPick(chatId);
            return;
        }

        if (trimmedText.startsWith("/list")) {
            String[] parts = trimmedText.split("\\s+");
            if (parts.length >= 2) {
                listServicesInCategory(chatId, parts[1]);
            } else {
                listAllCategories(chatId);
            }
            return;
        }

        if (trimmedText.startsWith("/admin")) {
            resetState(state);
            sendAdminMenu(chatId);
        }
    }

    // ===== Ввод в диалоге админа =====
    private void handleAdminStateInput(long chatId, String text, UserState state) {
        switch (state.state) {
            case AWAITING_CATEGORY_TITLE:
                createCategory(chatId, text.trim(), state);
                break;

            case AWAITING_SERVICE_CATEGORY:
                pickCategoryForService(chatId, text.trim(), state);
                break;

            case AWAITING_SERVICES_INPUT:
                handleServicesInput(chatId, text, state);
                break;

            case EDIT_SERVICE_NAME:
                updateServiceName(chatId, text.trim(), state);
                break;

            case EDIT_SERVICE_PRICE:
                updateServicePrice(chatId, text.trim(), state);
                break;

            case EDIT_SERVICE_DESCRIPTION:
                updateServiceDescription(chatId, text, state);
                break;

            case EDIT_CATEGORY_TITLE:
                updateCategoryTitle(chatId, text.trim(), state);
                break;

            default:
                resetState(state);
                telegramService.sendMessage(String.valueOf(chatId),
                        "Начните с /admin.",
                        telegramService.backKeyboard());
        }
    }

    // ===== Категории =====
    private void createCategory(long chatId, String title, UserState state) {
        if (title.isEmpty() || title.length() > 200) {
            telegramService.sendMessage(String.valueOf(chatId),
                    "⚠️ <b>Название должно быть от 1 до 200 символов.</b>\n\nПопробуйте снова.",
                    telegramService.cancelBackKeyboard());
            return;
        }

        if (categoryRepository.existsByTitleIgnoreCase(title)) {
            telegramService.sendMessage(String.valueOf(chatId),
                    "⚠️ <b>Категория с таким названием уже существует.</b>",
                    telegramService.backKeyboard());
            resetState(state);
            return;
        }

        String key = generateNumericKey();

        ServiceCategory category = new ServiceCategory();
        category.setKey(key);
        category.setTitle(title);
        categoryRepository.save(category);

        telegramService.sendMessage(String.valueOf(chatId),
                "✅ <b>Категория добавлена</b>\n\n" +
                        "Название: <i>" + title + "</i>\n" +
                        "Номер: <code>" + key + "</code>",
                telegramService.backKeyboard());

        resetState(state);
    }

    private void startEditCategory(long chatId, String key, UserState state) {
        Optional<ServiceCategory> catOpt = categoryRepository.findByKey(key);
        if (catOpt.isEmpty()) {
            telegramService.sendMessage(String.valueOf(chatId),
                    "<i>Категория не найдена.</i>",
                    telegramService.backKeyboard());
            return;
        }
        ServiceCategory cat = catOpt.get();
        state.editingCategoryId = cat.getId();
        state.state = State.EDIT_CATEGORY_TITLE;

        telegramService.sendMessage(String.valueOf(chatId),
                "✏️ <b>Редактирование категории</b>\n\n" +
                        "Текущее название: <i>" + cat.getTitle() + "</i>\n\n" +
                        "Введите новое название:",
                telegramService.cancelBackKeyboard());
    }

    private void updateCategoryTitle(long chatId, String newTitle, UserState state) {
        if (newTitle.isEmpty() || newTitle.length() > 200) {
            telegramService.sendMessage(String.valueOf(chatId),
                    "⚠️ <b>Название должно быть от 1 до 200 символов.</b>",
                    telegramService.cancelBackKeyboard());
            return;
        }

        Optional<ServiceCategory> catOpt = categoryRepository.findById(state.editingCategoryId);
        if (catOpt.isEmpty()) {
            resetState(state);
            telegramService.sendMessage(String.valueOf(chatId),
                    "<i>Категория исчезла.</i>",
                    telegramService.backKeyboard());
            return;
        }

        ServiceCategory cat = catOpt.get();

        Optional<ServiceCategory> existing = categoryRepository.findByTitleIgnoreCase(newTitle);
        if (existing.isPresent() && !existing.get().getId().equals(cat.getId())) {
            telegramService.sendMessage(String.valueOf(chatId),
                    "⚠️ <b>Категория с таким названием уже существует.</b>",
                    telegramService.cancelBackKeyboard());
            return;
        }

        cat.setTitle(newTitle);
        categoryRepository.save(cat);

        telegramService.sendMessage(String.valueOf(chatId),
                "✅ <b>Категория обновлена</b>\n\n" +
                        "Новое название: <i>" + newTitle + "</i>",
                telegramService.backKeyboard());

        resetState(state);
    }

    private void deleteCategoryByKey(long chatId, String key) {
        Optional<ServiceCategory> catOpt = categoryRepository.findByKey(key);
        if (catOpt.isEmpty()) {
            telegramService.sendMessage(String.valueOf(chatId),
                    "<i>Категория не найдена.</i>",
                    telegramService.backKeyboard());
            return;
        }
        ServiceCategory cat = catOpt.get();
        String title = cat.getTitle();
        long count = itemRepository.findByCategoryId(cat.getId()).size();

        categoryRepository.delete(cat);

        telegramService.sendMessage(String.valueOf(chatId),
                "🗑 <b>Категория удалена</b>\n\n" +
                        "Название: <i>" + title + "</i>\n" +
                        "Удалено услуг: <code>" + count + "</code>",
                telegramService.backKeyboard());
    }

    // ===== Услуги =====
    private void pickCategoryForService(long chatId, String input, UserState state) {
        Optional<ServiceCategory> category = categoryRepository.findByKey(input);
        if (category.isEmpty()) {
            telegramService.sendMessage(String.valueOf(chatId),
                    "⚠️ <b>Категории с номером " + input + " нет.</b>\n\n" +
                            "Введите номер из списка.",
                    telegramService.cancelOnlyKeyboard());
            return;
        }

        state.pendingCategoryId = category.get().getId();
        state.state = State.AWAITING_SERVICES_INPUT;

        telegramService.sendMessage(String.valueOf(chatId),
                "📂 <b>Категория:</b> <i>" + category.get().getTitle() + "</i>\n\n" +
                        "<b>Формат:</b> Название — цена.\n" +
                        "Несколько услуг — разделите <u>пустой строкой</u>.\n\n" +
                        "<b>Пример:</b>\n" +
                        "<code>Замена масла 1500</code>\n\n" +
                        "<code>Диагностика 500</code>",
                telegramService.servicesInputKeyboard());
    }

    private void startAddingToCategory(long chatId, String categoryKey, UserState state) {
        Optional<ServiceCategory> categoryOpt = categoryRepository.findByKey(categoryKey);
        if (categoryOpt.isEmpty()) {
            telegramService.sendMessage(String.valueOf(chatId),
                    "<i>Категория не найдена.</i>",
                    telegramService.backKeyboard());
            return;
        }
        ServiceCategory category = categoryOpt.get();

        resetState(state);
        state.pendingCategoryId = category.getId();
        state.state = State.AWAITING_SERVICES_INPUT;

        telegramService.sendMessage(String.valueOf(chatId),
                "📂 <b>Категория:</b> <i>" + category.getTitle() + "</i>\n\n" +
                        "<b>Формат:</b> Название — цена.\n" +
                        "Несколько услуг — разделите <u>пустой строкой</u>.\n\n" +
                        "<b>Пример:</b>\n" +
                        "<code>Замена масла 1500</code>\n\n" +
                        "<code>Диагностика 500</code>",
                telegramService.servicesInputKeyboard());
    }

    private void handleServicesInput(long chatId, String text, UserState state) {
        if (state.pendingCategoryId == null) {
            resetState(state);
            telegramService.sendMessage(String.valueOf(chatId),
                    "<i>Ошибка состояния. Начните заново.</i>",
                    telegramService.backKeyboard());
            return;
        }

        ServiceCategory category = categoryRepository.findById(state.pendingCategoryId).orElse(null);
        if (category == null) {
            resetState(state);
            telegramService.sendMessage(String.valueOf(chatId),
                    "<i>Категория исчезла.</i>",
                    telegramService.backKeyboard());
            return;
        }

        List<String> blocks = splitIntoBlocks(text);

        List<String> added = new ArrayList<>();
        List<String> skipped = new ArrayList<>();

        for (String block : blocks) {
            String singleLine = block.replaceAll("\\s+", " ").trim();
            if (singleLine.isEmpty()) continue;

            ParsedService parsed = parseServiceLine(singleLine);
            if (parsed == null) {
                skipped.add(singleLine + " — не удалось распознать");
                continue;
            }

            if (itemRepository.existsByCategoryIdAndNameIgnoreCase(
                    category.getId(), parsed.name)) {
                skipped.add(singleLine + " — уже существует");
                continue;
            }

            ServiceItem item = new ServiceItem(parsed.name, normalizePrice(parsed.price));
            item.setCategory(category);
            itemRepository.save(item);

            added.add("• <b>" + parsed.name + "</b> — <i>" + item.getPrice() + "</i>");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("📂 <b>Категория:</b> <i>").append(category.getTitle()).append("</i>\n\n");

        if (added.size() == 1) {
            sb.append("✅ <b>Добавлена услуга:</b>\n");
        } else if (!added.isEmpty()) {
            sb.append("✅ <b>Добавлено услуг: ").append(added.size()).append("</b>\n");
        } else {
            sb.append("❌ <b>Ничего не добавлено.</b>\n");
        }

        for (String s : added) {
            sb.append(s).append("\n");
        }

        if (!skipped.isEmpty()) {
            sb.append("\n⚠️ <b>Пропущено: ").append(skipped.size()).append("</b>\n");
            for (String s : skipped) {
                sb.append("<i>• ").append(s).append("</i>\n");
            }
        }

        telegramService.sendMessage(String.valueOf(chatId), sb.toString(), telegramService.backKeyboard());

        resetState(state);
    }

    private List<String> splitIntoBlocks(String text) {
        List<String> blocks = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        String[] lines = text.split("\\r?\\n", -1);
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                if (current.length() > 0) {
                    blocks.add(current.toString());
                    current.setLength(0);
                }
            } else {
                if (current.length() > 0) current.append("\n");
                current.append(line);
            }
        }
        if (current.length() > 0) {
            blocks.add(current.toString());
        }

        return blocks;
    }

    private ParsedService parseServiceLine(String line) {
        if (line == null || line.isBlank()) return null;

        String cleaned = line.replaceFirst("^\\s*\\d+[.)\\]]?\\s*", "").trim();
        if (cleaned.isEmpty()) return null;

        java.util.regex.Pattern pricePattern = java.util.regex.Pattern.compile(
                "(?i)(от\\s+)?(\\d[\\d\\s]*\\d|\\d)\\s*(₽|руб\\.?|rub|р\\.)?\\s*$"
        );
        java.util.regex.Matcher m = pricePattern.matcher(cleaned);

        if (!m.find()) return null;

        String pricePart = cleaned.substring(m.start(), m.end()).trim();
        String namePart = cleaned.substring(0, m.start()).trim();

        namePart = namePart.replaceAll("[,\\-:—\\s]+$", "").trim();
        if (namePart.isEmpty()) return null;

        String priceNorm = pricePart.replaceAll("\\s+", " ").trim();

        ParsedService result = new ParsedService();
        result.name = namePart;
        result.price = priceNorm;
        return result;
    }

    private static class ParsedService {
        String name;
        String price;
    }

    // ===== Просмотр и редактирование услуги =====
    private void showServiceDetails(long chatId, Long id) {
        Optional<ServiceItem> itemOpt = itemRepository.findById(id);
        if (itemOpt.isEmpty()) {
            telegramService.sendMessage(String.valueOf(chatId),
                    "<i>Услуга не найдена.</i>",
                    telegramService.backKeyboard());
            return;
        }
        ServiceItem item = itemOpt.get();

        StringBuilder sb = new StringBuilder();
        sb.append("📋 <b>").append(item.getName()).append("</b>\n\n");
        sb.append("💰 Цена: <i>").append(item.getPrice()).append("</i>\n");
        sb.append("📝 Описание: <i>")
                .append(item.getDescription() == null || item.getDescription().isBlank()
                        ? "(не задано)" : item.getDescription())
                .append("</i>");

        telegramService.sendMessage(String.valueOf(chatId),
                sb.toString(),
                telegramService.serviceDetailsKeyboard(item.getId()));
    }

    private void startEditService(long chatId, Long id, UserState state) {
        Optional<ServiceItem> itemOpt = itemRepository.findById(id);
        if (itemOpt.isEmpty()) {
            telegramService.sendMessage(String.valueOf(chatId),
                    "<i>Услуга не найдена.</i>",
                    telegramService.backKeyboard());
            return;
        }
        ServiceItem item = itemOpt.get();
        state.editingServiceId = id;
        state.state = State.EDIT_SERVICE_NAME;

        telegramService.sendMessage(String.valueOf(chatId),
                "✏️ <b>Редактирование услуги</b>\n\n" +
                        "Текущее название: <i>" + item.getName() + "</i>\n\n" +
                        "Введите новое название:",
                telegramService.cancelBackKeyboard());
    }

    private void startEditDescription(long chatId, Long id, UserState state) {
        Optional<ServiceItem> itemOpt = itemRepository.findById(id);
        if (itemOpt.isEmpty()) {
            telegramService.sendMessage(String.valueOf(chatId),
                    "<i>Услуга не найдена.</i>",
                    telegramService.backKeyboard());
            return;
        }
        ServiceItem item = itemOpt.get();
        state.editingServiceId = id;
        state.state = State.EDIT_SERVICE_DESCRIPTION;

        String current = item.getDescription() == null || item.getDescription().isBlank()
                ? "(не задано)" : item.getDescription();

        telegramService.sendMessage(String.valueOf(chatId),
                "📝 <b>Редактирование описания</b>\n\n" +
                        "Услуга: <i>" + item.getName() + "</i>\n" +
                        "Текущее описание: <i>" + current + "</i>\n\n" +
                        "<b>Введите новое описание</b> (или отправьте <code>-</code>, чтобы очистить):",
                telegramService.cancelBackKeyboard());
    }

    private void updateServiceDescription(long chatId, String newDescription, UserState state) {
        String trimmed = newDescription.trim();

        if ("-".equals(trimmed)) {
            trimmed = "";
        }

        if (trimmed.length() > 2000) {
            telegramService.sendMessage(String.valueOf(chatId),
                    "⚠️ <b>Описание должно быть не длиннее 2000 символов.</b>",
                    telegramService.cancelBackKeyboard());
            return;
        }

        Optional<ServiceItem> itemOpt = itemRepository.findById(state.editingServiceId);
        if (itemOpt.isEmpty()) {
            resetState(state);
            telegramService.sendMessage(String.valueOf(chatId),
                    "<i>Услуга исчезла.</i>",
                    telegramService.backKeyboard());
            return;
        }
        ServiceItem item = itemOpt.get();
        item.setDescription(trimmed.isEmpty() ? null : trimmed);
        itemRepository.save(item);

        telegramService.sendMessage(String.valueOf(chatId),
                "✅ <b>Описание обновлено</b>\n\n" +
                        "Услуга: <i>" + item.getName() + "</i>\n" +
                        "Описание: <i>" + (trimmed.isEmpty() ? "(пусто)" : trimmed) + "</i>",
                telegramService.backKeyboard());

        resetState(state);
    }

    private void updateServiceName(long chatId, String newName, UserState state) {
        if (newName.isEmpty() || newName.length() > 200) {
            telegramService.sendMessage(String.valueOf(chatId),
                    "⚠️ <b>Название должно быть от 1 до 200 символов.</b>",
                    telegramService.cancelBackKeyboard());
            return;
        }
        Optional<ServiceItem> itemOpt = itemRepository.findById(state.editingServiceId);
        if (itemOpt.isEmpty()) {
            resetState(state);
            telegramService.sendMessage(String.valueOf(chatId),
                    "<i>Услуга исчезла.</i>",
                    telegramService.backKeyboard());
            return;
        }
        ServiceItem item = itemOpt.get();
        item.setName(newName);
        itemRepository.save(item);

        state.state = State.EDIT_SERVICE_PRICE;
        telegramService.sendMessage(String.valueOf(chatId),
                "✅ <b>Новое название сохранено</b>\n\n" +
                        "<i>" + newName + "</i>\n\n" +
                        "Теперь введите новую цену:",
                telegramService.cancelBackKeyboard());
    }

    private void updateServicePrice(long chatId, String newPrice, UserState state) {
        if (!isValidPrice(newPrice)) {
            telegramService.sendMessage(String.valueOf(chatId),
                    "⚠️ <b>Цена должна содержать число.</b>",
                    telegramService.cancelBackKeyboard());
            return;
        }
        Optional<ServiceItem> itemOpt = itemRepository.findById(state.editingServiceId);
        if (itemOpt.isEmpty()) {
            resetState(state);
            telegramService.sendMessage(String.valueOf(chatId),
                    "<i>Услуга исчезла.</i>",
                    telegramService.backKeyboard());
            return;
        }
        ServiceItem item = itemOpt.get();
        item.setPrice(normalizePrice(newPrice));
        itemRepository.save(item);

        telegramService.sendMessage(String.valueOf(chatId),
                "✅ <b>Услуга обновлена</b>\n\n" +
                        "Название: <i>" + item.getName() + "</i>\n" +
                        "Цена: <i>" + item.getPrice() + "</i>",
                telegramService.backKeyboard());

        resetState(state);
    }

    // ===== Удаление услуги (с подтверждением) =====
    private void showDeleteConfirmation(long chatId, Long id) {
        Optional<ServiceItem> itemOpt = itemRepository.findById(id);
        if (itemOpt.isEmpty()) {
            telegramService.sendMessage(String.valueOf(chatId),
                    "<i>Услуга не найдена.</i>",
                    telegramService.backKeyboard());
            return;
        }
        ServiceItem item = itemOpt.get();

        String categoryKey = "";
        if (item.getCategoryId() != null) {
            Optional<ServiceCategory> catOpt = categoryRepository.findById(item.getCategoryId());
            if (catOpt.isPresent()) {
                categoryKey = catOpt.get().getKey();
            }
        }

        telegramService.sendMessage(String.valueOf(chatId),
                "🗑 <b>Удалить услугу?</b>\n\n" +
                        "<i>" + item.getName() + "</i>\n" +
                        "Цена: <i>" + item.getPrice() + "</i>\n\n" +
                        "Это действие нельзя отменить.",
                telegramService.confirmDeleteKeyboard(id, categoryKey));
    }

    private void performDeleteService(long chatId, Long id) {
        Optional<ServiceItem> item = itemRepository.findById(id);
        if (item.isEmpty()) {
            telegramService.sendMessage(String.valueOf(chatId),
                    "<i>Услуга не найдена.</i>",
                    telegramService.backKeyboard());
            return;
        }
        String name = item.get().getName();
        itemRepository.deleteById(id);

        telegramService.sendMessage(String.valueOf(chatId),
                "🗑 <b>Услуга удалена</b>\n\n" +
                        "<i>" + name + "</i>",
                telegramService.backKeyboard());
    }

    // ===== Списки =====
    private void sendCategoryListForPick(long chatId) {
        List<ServiceCategory> categories = getAllCategoriesSorted();
        if (categories.isEmpty()) {
            telegramService.sendMessage(String.valueOf(chatId),
                    "<i>Категорий пока нет. Сначала создайте категорию.</i>",
                    telegramService.cancelOnlyKeyboard());
            return;
        }
        StringBuilder sb = new StringBuilder("<b>Выберите категорию:</b>\n\n");
        for (ServiceCategory c : categories) {
            sb.append("<code>").append(c.getKey()).append("</code> — <i>")
                    .append(c.getTitle()).append("</i>\n");
        }
        sb.append("\n<i>Введите номер категории.</i>");
        telegramService.sendMessage(String.valueOf(chatId), sb.toString(), telegramService.cancelOnlyKeyboard());
    }

    private void listAllCategories(long chatId) {
        List<ServiceCategory> categories = getAllCategoriesSorted();
        if (categories.isEmpty()) {
            telegramService.sendMessage(String.valueOf(chatId),
                    "<i>Список пуст.</i>",
                    telegramService.backKeyboard());
            return;
        }

        List<Map<String, String>> buttonsData = new ArrayList<>();
        for (ServiceCategory c : categories) {
            long count = itemRepository.findByCategoryId(c.getId()).size();
            Map<String, String> map = new HashMap<>();
            map.put("key", c.getKey());
            map.put("title", c.getTitle());
            map.put("count", String.valueOf(count));
            buttonsData.add(map);
        }

        telegramService.sendMessage(String.valueOf(chatId),
                "<b>Категории</b>\n<i>Нажмите на категорию для просмотра услуг.</i>",
                telegramService.categoryKeyboard(buttonsData));
    }

    private void listServicesInCategory(long chatId, String numberStr) {
        Optional<ServiceCategory> categoryOpt = categoryRepository.findByKey(numberStr.trim());
        if (categoryOpt.isEmpty()) {
            telegramService.sendMessage(String.valueOf(chatId),
                    "⚠️ <b>Категория не найдена.</b>",
                    telegramService.backKeyboard());
            return;
        }
        ServiceCategory category = categoryOpt.get();
        List<ServiceItem> items = itemRepository.findByCategoryId(category.getId());

        if (items.isEmpty()) {
            telegramService.sendMessage(String.valueOf(chatId),
                    "📂 <b>" + category.getTitle() + "</b>\n\n" +
                            "<i>В этой категории пока нет услуг.</i>",
                    telegramService.emptyCategoryKeyboard(category.getKey()));
            return;
        }

        List<Map<String, String>> servicesData = new ArrayList<>();
        for (ServiceItem item : items) {
            Map<String, String> map = new HashMap<>();
            map.put("id", String.valueOf(item.getId()));
            map.put("name", item.getName());
            map.put("price", item.getPrice());
            servicesData.add(map);
        }

        telegramService.sendMessage(String.valueOf(chatId),
                "📂 <b>" + category.getTitle() + "</b>\n" +
                        "<i>Всего услуг: " + items.size() + "</i>",
                telegramService.servicesKeyboard(category.getKey(), servicesData));
    }

    // ===== Утилиты =====
    private List<ServiceCategory> getAllCategoriesSorted() {
        List<ServiceCategory> categories = categoryRepository.findAll();
        categories.sort(Comparator.comparingInt(c -> parseIntSafe(c.getKey())));
        return categories;
    }

    private int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }

    private String generateNumericKey() {
        int max = 0;
        for (ServiceCategory c : categoryRepository.findAll()) {
            int num = parseIntSafe(c.getKey());
            if (num != Integer.MAX_VALUE && num > max) max = num;
        }
        int next = max + 1;
        while (categoryRepository.existsByKey(String.valueOf(next))) {
            next++;
        }
        return String.valueOf(next);
    }

    private boolean isValidPrice(String price) {
        if (price == null || price.isBlank()) return false;
        if (price.length() > 50) return false;
        for (char ch : price.toCharArray()) {
            if (Character.isDigit(ch)) return true;
        }
        return false;
    }

    private String normalizePrice(String input) {
        if (input == null) return null;
        String trimmed = input.trim();

        if (trimmed.contains("₽") || trimmed.toLowerCase().contains("от")) {
            return trimmed;
        }

        if (trimmed.matches("\\d[\\d\\s]*")) {
            return trimmed + " ₽";
        }

        return trimmed;
    }
}