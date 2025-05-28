package ru.sskier.tg_assistant_bot.repository;

import org.springframework.data.repository.ListCrudRepository;
import ru.sskier.tg_assistant_bot.entity.User;


public interface UserRepository extends ListCrudRepository<User, Long> {
}
