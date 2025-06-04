package ru.sskier.tg_assistant_bot.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.sskier.tg_assistant_bot.entity.AssistantBotHistory;
import ru.sskier.tg_assistant_bot.exception.BotException;
import ru.sskier.tg_assistant_bot.mapper.UserMapperImpl;
import ru.sskier.tg_assistant_bot.service.AssistantBotService;
import ru.sskier.tg_assistant_bot.util.Emojis;

@Slf4j
@Component
public class AssistantBot extends TelegramLongPollingBot {

    private final UserMapperImpl userMapper;
    private final AssistantBotService assistantBotService;

    private final static String START = "/start";
    private final static String RATES = "/rates";
    private final static String HELP = "/help";
    private final String botUsername;


    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Autowired
    public AssistantBot(@Value("${assistant_bot.token}") String botToken,
                        @Value("${assistant_bot.username}") String botUsername,
                        UserMapperImpl userMapper,
                        AssistantBotService assistantBotService) {
        super(botToken);
        this.botUsername = botUsername;
        this.userMapper = userMapper;
        this.assistantBotService = assistantBotService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        String message = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        var sender = userMapper.toAppUser(update.getMessage().getFrom());
        // save user if his first using bot
        assistantBotService.saveUser(sender);
        // save history
        AssistantBotHistory assistantBotHistory = AssistantBotHistory.builder()
                .user(sender)
                .chatId(chatId)
                .textMessage(message)
                .build();
        assistantBotService.saveUserHistory(assistantBotHistory);

        switch (message) {
            case START -> {
                String firstName = update.getMessage().getFrom().getFirstName();
                startCommand(firstName, chatId);
            }
            case RATES -> {
                sendListRates(chatId);
                repeatCommand(chatId);
            }
            case HELP -> helpCommand(chatId);
            default -> sendMessage("К сожалению я не знаю такой команды " + Emojis.CONFUSED, chatId);
        }
    }


    private void startCommand(String firstName, Long chatId) {
        var text = """
                Привет %s!
                Я экспериментальный Бот помощник который пока что может:
                /rates - получить курсы валют от центрабанка на сегодняшний день
                /help - получить дополнительную справку по моей работе
                """;
        var formattedText = String.format(text, firstName);
        sendMessage(formattedText, chatId);
    }

    private void sendListRates(Long chatId) {
        String exchangeRates = assistantBotService.getExchangeRates();
        sendMessage(exchangeRates, chatId);
    }

    private void repeatCommand(Long chatId) {
        sendMessage("""
                \n
                Мои команды:
                /rates - получить текущие на день курсы валют от центрабанка
                /help - получить дополнительную справку по моей работе
                """, chatId);
    }

    private void helpCommand(Long chatId) {
        var text = """
                Дополнительное объяснение моих команд:
                - При вводе команды /rates будет выведен отсортированный в алфавитном порядке список всех курсов валют от ЦБ РФ (всего 43 валюты).
                Данные по валютам обновляются каждый день.
                Поэтому особого смысла несколько раз на дню вызывать команду /rates нет :)
                                
                Если у тебя есть предложения по улучшению или вводу дополнительного функционала,
                то предлагаю написать моему создателю @Sskier
                                
                Надеюсь я понравился тебе %s, удачи!
                """;
        sendMessage(String.format(text, Emojis.BLUSH), chatId);
    }

    private void sendMessage(String message, Long chatId) throws BotException {
        String chatIdStr = String.valueOf(chatId);
        SendMessage sendMessage = new SendMessage(chatIdStr, message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new BotException("ошибка отправки сообщения в Телеграм", e);
        }
    }
}