package ru.sskier.tg_assistant_bot.service;

import ru.sskier.tg_assistant_bot.entity.AssistantBotHistory;
import ru.sskier.tg_assistant_bot.entity.User;

public interface AssistantBotService {

    String getExchangeRates();

    void saveUser(User user);

    void saveUserHistory(AssistantBotHistory assistantBotHistory);
}
