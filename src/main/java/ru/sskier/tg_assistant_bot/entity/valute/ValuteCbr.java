package ru.sskier.tg_assistant_bot.entity.valute;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


/**
 * Узловой класс валюты для парсинга XML с https://cbr.ru/scripts/XML_daily.asp
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public final class ValuteCbr {

    /**
     * Идентификатор валюты
     */
    private String id;

    /**
     * трехзначный код валюты
     */
    private Byte numCode;

    /**
     * трехзначное обозначение валюты
     */
    private String charCode;

    /**
     * Отношение количества валюты к 1 рублю
     */
    private Integer nominal;

    /**
     * Наименование валюты
     */
    private String name;

    /**
     * Относительный курс (по отношению к nominal)
     */
    private BigDecimal value;

    /**
     * курс валюты к 1 рублю
     */
    private BigDecimal valueUnitRate;


}
