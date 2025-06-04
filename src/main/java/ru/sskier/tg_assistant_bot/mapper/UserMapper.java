package ru.sskier.tg_assistant_bot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.sskier.tg_assistant_bot.entity.User;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    User toAppUser(org.telegram.telegrambots.meta.api.objects.User user);

    org.telegram.telegrambots.meta.api.objects.User toTelegramUser(User user);

}
