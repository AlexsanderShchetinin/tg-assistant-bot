package ru.sskier.tg_assistant_bot.mapper;

import org.mapstruct.*;
import ru.sskier.tg_assistant_bot.entity.valute.Valute;
import ru.sskier.tg_assistant_bot.entity.valute.ValuteCbr;

import java.time.LocalDate;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ValuteMapper {

    @Mappings({
            @Mapping(target = "id.date", source = "date"),
            @Mapping(target = "id.id", source = "valuteCbr.id")
    })
    Valute toValute(ValuteCbr valuteCbr, LocalDate date);


}
