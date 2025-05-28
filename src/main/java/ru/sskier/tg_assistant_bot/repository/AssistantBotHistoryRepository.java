package ru.sskier.tg_assistant_bot.repository;

import org.springframework.data.repository.ListCrudRepository;
import ru.sskier.tg_assistant_bot.entity.AssistantBotHistory;

public interface AssistantBotHistoryRepository extends ListCrudRepository<AssistantBotHistory, Long> {


}
