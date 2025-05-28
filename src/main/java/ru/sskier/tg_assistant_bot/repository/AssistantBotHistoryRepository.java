package ru.sskier.tg_assistant_bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sskier.tg_assistant_bot.entity.AssistantBotHistory;

public interface AssistantBotHistoryRepository extends JpaRepository<AssistantBotHistory, Long> {


}
