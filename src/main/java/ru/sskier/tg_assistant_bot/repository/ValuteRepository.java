package ru.sskier.tg_assistant_bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sskier.tg_assistant_bot.entity.valute.CompositeValuteKey;
import ru.sskier.tg_assistant_bot.entity.valute.Valute;

import java.time.LocalDate;
import java.util.List;


public interface ValuteRepository extends JpaRepository<Valute, CompositeValuteKey> {

    List<Valute> findAllByIdDate(LocalDate date);

}
