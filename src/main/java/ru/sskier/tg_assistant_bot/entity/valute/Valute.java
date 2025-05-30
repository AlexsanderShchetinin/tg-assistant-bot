package ru.sskier.tg_assistant_bot.entity.valute;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;


/**
 * Узловой класс валюты для сохранения в БД
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "assistant_bot", name = "exchange_rates")
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
public class Valute {

    /**
     * Составной ключ идентификатора валюты
     */
    @EmbeddedId
    private CompositeValuteKey id;

    /**
     * трехзначный код валюты
     */
    @Column(name = "num_code")
    private Short numCode;

    /**
     * трехзначное обозначение валюты
     */
    @Column(name = "char_code")
    private String charCode;

    /**
     * Наименование валюты
     */
    @Column(name = "name")
    private String name;

    /**
     * курс валюты к 1 рублю
     */
    @Column(name = "value_unit_rate", scale = 4, precision = 19)
    private BigDecimal valueUnitRate;

}
