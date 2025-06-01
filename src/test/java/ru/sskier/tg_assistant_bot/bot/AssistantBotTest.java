package ru.sskier.tg_assistant_bot.bot;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.sskier.tg_assistant_bot.TestContainerConfig;
import ru.sskier.tg_assistant_bot.entity.User;
import ru.sskier.tg_assistant_bot.mapper.UserMapperImpl;
import ru.sskier.tg_assistant_bot.repository.UserRepository;
import ru.sskier.tg_assistant_bot.service.AssistantBotService;
import ru.sskier.tg_assistant_bot.util.DataUtils;
import ru.sskier.tg_assistant_bot.util.TestDatabaseCleaner;

import java.util.List;

@ExtendWith({SpringExtension.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class AssistantBotTest extends TestContainerConfig {

    private final TestDatabaseCleaner databaseCleaner;
    @Value("${assistant_bot.token}")
    private String assistantBotToken;
    @Value("${assistant_bot.username}")
    private String assistantBotUsername;
    @Value("${test1_bot.firstName}")
    private String testBotFirstName;
    @Value("${test_chat.id}")
    private Long chatId;
    @Value("${me.id}")
    private Long userId;

    private final UserMapperImpl userMapper;
    private final AssistantBotService assistantBotService;
    private final UserRepository userRepository;

    // список таблиц БД в том порядке, в котором необходимо очищать схему
    private final List<String> tables = List.of("assistant_bot_history", "exchange_rates", "users");


    @BeforeEach
    public void init() {
        databaseCleaner.cleanTablesWithOrder("assistant_bot", tables);
    }

    @Test
    void onUpdateStartCommandReceived() throws Exception {
        // мокаем реальный класс бота
        AssistantBot bot = Mockito.spy(
                new AssistantBot(assistantBotToken,assistantBotUsername,userMapper,assistantBotService));
        // Создаём фиктивное обновление с сообщением
        Update update = DataUtils.createUpdateWithSimplyMessage("/start", testBotFirstName, userId, chatId);
        // Вызываем метод обработки
        bot.onUpdateReceived(update);
        // создаем фиктивный SendMessage
        var text = """
                Привет %s!
                Я экспериментальный Бот помощник который пока что может:
                /rates - получить курсы валют от центрабанка на сегодняшний день
                /help - получить дополнительную справку по моей работе
                """;
        var formattedText = String.format(text, testBotFirstName);
        String chatIdStr = String.valueOf(chatId);
        SendMessage mockSendMessage = new SendMessage(chatIdStr, formattedText);

        // Проверяем, что execute был вызван с нужным сообщением
        Mockito.verify(bot, Mockito.times(1))
                .execute(mockSendMessage);

        // обращаемся в БД и проверяем user в БД
        User user = userRepository.findById(userId).orElseGet(User::new);
        Assertions.assertEquals(user.getId(), userId);


    }
}