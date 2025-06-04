package ru.sskier.tg_assistant_bot.bot;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.sskier.tg_assistant_bot.TestContainerConfig;
import ru.sskier.tg_assistant_bot.client.CbrClient;
import ru.sskier.tg_assistant_bot.entity.AssistantBotHistory;
import ru.sskier.tg_assistant_bot.entity.User;
import ru.sskier.tg_assistant_bot.entity.valute.Valute;
import ru.sskier.tg_assistant_bot.mapper.UserMapperImpl;
import ru.sskier.tg_assistant_bot.repository.AssistantBotHistoryRepository;
import ru.sskier.tg_assistant_bot.repository.UserRepository;
import ru.sskier.tg_assistant_bot.repository.ValuteRepository;
import ru.sskier.tg_assistant_bot.service.AssistantBotService;
import ru.sskier.tg_assistant_bot.util.DataUtils;
import ru.sskier.tg_assistant_bot.util.Emojis;
import ru.sskier.tg_assistant_bot.util.TestDatabaseCleaner;

import java.util.Comparator;
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
    private final AssistantBotHistoryRepository historyRepository;
    private final ValuteRepository valuteRepository;

    @MockitoBean
    private final CbrClient cbrClient;

    // список таблиц БД в том порядке, в котором необходимо очищать схему
    private final List<String> tables = List.of("assistant_bot_history", "exchange_rates", "users");


    @BeforeEach
    @DisplayName("Предварительный тест инициализации (очистка БД)")
    public void init() {
        databaseCleaner.cleanTablesWithOrder("assistant_bot", tables);
    }

    @Test
    @DisplayName("Тест команд /start и /help сделанных от одного и того же пользователя")
    void onUpdateStartAndHelpCommandsReceivedRequestFromOneUser() throws Exception {
        // мокаем реальный класс бота
        AssistantBot bot = Mockito.spy(
                new AssistantBot(assistantBotToken, assistantBotUsername, userMapper, assistantBotService));
        // start command
        // Создаём фиктивное обновление с сообщением
        Update startUpdate = DataUtils.createUpdateWithSimplyMessage("/start", testBotFirstName, userId, chatId);
        // Вызываем метод обработки
        bot.onUpdateReceived(startUpdate);
        // создаем фиктивный SendMessage
        var startText = """
                Привет %s!
                Я экспериментальный Бот помощник который пока что может:
                /rates - получить курсы валют от центрабанка на сегодняшний день
                /help - получить дополнительную справку по моей работе
                """;
        var formattedStartText = String.format(startText, testBotFirstName);
        String chatIdStr = String.valueOf(chatId);
        SendMessage mockStartSendMessage = new SendMessage(chatIdStr, formattedStartText);

        // Проверяем, что execute был вызван с нужным сообщением
        Mockito.verify(bot, Mockito.times(1))
                .execute(mockStartSendMessage);
        // обращаемся в БД и проверяем user в БД
        User user = userRepository.findById(userId).orElseGet(User::new);
        Assertions.assertEquals(user.getId(), userId);
        // Проверяем запись в истории бота в БД
        Assertions.assertEquals(historyRepository.findByUserId(userId).size(), 1);

        // help command
        // Создаём фиктивное обновление с сообщением
        Update helpUpdate = DataUtils.createUpdateWithSimplyMessage("/help", testBotFirstName, userId, chatId);
        // Вызываем метод обработки
        bot.onUpdateReceived(helpUpdate);
        // создаем фиктивный SendMessage
        var helpText = """
                Дополнительное объяснение моих команд:
                - При вводе команды /rates будет выведен отсортированный в алфавитном порядке список всех курсов валют от ЦБ РФ (всего 43 валюты).
                Данные по валютам обновляются каждый день.
                Поэтому особого смысла несколько раз на дню вызывать команду /rates нет :)
                                
                Если у тебя есть предложения по улучшению или вводу дополнительного функционала,
                то предлагаю написать моему создателю @Sskier
                                
                Надеюсь я понравился тебе %s, удачи!
                """;
        var formattedHelpText = String.format(helpText, Emojis.BLUSH);
        SendMessage mockHelpSendMessage = new SendMessage(chatIdStr, formattedHelpText);

        // Проверяем, что execute был вызван с нужным сообщением
        Mockito.verify(bot, Mockito.times(1))
                .execute(mockHelpSendMessage);
        // обращаемся в БД и проверяем user в БД
        List<User> users = userRepository.findAll();
        Assertions.assertEquals(users.size(), 1); // один и тот же пользователь не должен перезаписаться
        // Проверяем запись в истории бота в БД
        Assertions.assertEquals(historyRepository.findByUserId(userId).size(), 2);
    }

    @Test
    @DisplayName("Тест работы команды /rates от одного пользователя")
    void onUpdateGetExchangeRatesCommandReceivedRequestFromOneUser() throws Exception {
        // given
        // мокаем реальный класс бота
        AssistantBot bot = Mockito.spy(
                new AssistantBot(assistantBotToken, assistantBotUsername, userMapper, assistantBotService));
        // мокируем метод клиента по получению xml с валютами от центробанка
        Mockito.when(cbrClient.getCurrentRatesXml())
                .thenReturn(DataUtils.getTestXml());
        // Создаём фиктивное обновление с сообщением
        Update exchangeRatesUpdate = DataUtils.createUpdateWithSimplyMessage("/rates", testBotFirstName, userId, chatId);

        // when
        // Вызываем метод обработки
        bot.onUpdateReceived(exchangeRatesUpdate);

        // then
        // получаем из БД валюты и заносим в текст
        List<Valute> exchangeRates = valuteRepository.findAll();
        Assertions.assertFalse(exchangeRates.isEmpty());
        exchangeRates = exchangeRates.stream()
                .sorted(Comparator.comparing(Valute::getName))
                .toList();
        // создаем фиктивный SendMessage
        StringBuilder sb = new StringBuilder();
        sb.append("Курсы валют на 02.06.2025 составляют:\n \n"); // захардкодили дату, потому что она должна совпадать из XML из DataUtils.getXml()
        for (Valute exchangeRate : exchangeRates) {
            sb.append(exchangeRate.getName())
                    .append(" ( /").append(exchangeRate.getCharCode()).append("): ")
                    .append(exchangeRate.getValueUnitRate())
                    .append("\n");
        }
        String chatIdStr = String.valueOf(chatId);
        SendMessage mockExchangeRatesSendMessage = new SendMessage(chatIdStr, sb.toString());

        // Проверяем, что execute был вызван с нужным сообщением
        Mockito.verify(bot, Mockito.times(1))
                .execute(mockExchangeRatesSendMessage);

        SendMessage mockRepeatSendMessage = new SendMessage(chatIdStr, """
                \n
                Мои команды:
                /rates - получить текущие на день курсы валют от центрабанка
                /help - получить дополнительную справку по моей работе
                """);
        Mockito.verify(bot, Mockito.times(1))
                .execute(mockRepeatSendMessage);

        // проверяем запись в историю
        List<AssistantBotHistory> historyList = historyRepository.findByUserId(userId);
        Assertions.assertEquals(historyList.size(), 1);
        Assertions.assertEquals(historyList.getFirst().getTextMessage(), "/rates");
    }

    @Test
    @DisplayName("Тест работы default команды от одного пользователя")
    void onUpdateGetDefaultReceivedRequestFromOneUser() throws Exception {
        // given
        // мокаем реальный класс бота
        AssistantBot bot = Mockito.spy(
                new AssistantBot(assistantBotToken, assistantBotUsername, userMapper, assistantBotService));
        // Создаём фиктивное обновление с сообщением
        Update defaultUpdate = DataUtils
                .createUpdateWithSimplyMessage("Абракадабра эээ!", testBotFirstName, userId, chatId);

        // when
        // Вызываем метод обработки
        bot.onUpdateReceived(defaultUpdate);

        // then
        String chatIdStr = String.valueOf(chatId);
        SendMessage mockDefaultSendMessage = new SendMessage(chatIdStr,
                "К сожалению я не знаю такой команды " + Emojis.CONFUSED);
        Mockito.verify(bot, Mockito.times(1))
                .execute(mockDefaultSendMessage);

        // проверяем запись в историю
        List<AssistantBotHistory> historyList = historyRepository.findByUserId(userId);
        Assertions.assertEquals(historyList.size(), 1);
        Assertions.assertEquals(historyList.getFirst().getTextMessage(), "Абракадабра эээ!");
    }

}