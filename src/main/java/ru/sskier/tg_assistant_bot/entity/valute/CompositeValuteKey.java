package ru.sskier.tg_assistant_bot.entity.valute;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Составной ключ идентификатора валюты получаемый из id валюты и даты
 * id валюты получаем из поля ID каждого <a href="https://cbr.ru/scripts/XML_daily.asp">курса валют ЦБ РФ</a>"
 */
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CompositeValuteKey implements Serializable {

    private String id;
    private LocalDate date;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompositeValuteKey that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date);
    }
}

