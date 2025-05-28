package ru.sskier.tg_assistant_bot.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.sskier.tg_assistant_bot.exception.BotException;
import ru.sskier.tg_assistant_bot.mapper.UserMapperImpl;
import ru.sskier.tg_assistant_bot.repository.AssistantBotHistoryRepository;
import ru.sskier.tg_assistant_bot.repository.UserRepository;

import java.util.Optional;

@Slf4j
@Component
public class AssistantBot extends TelegramLongPollingBot {

    private final UserRepository userRepository;
    private final AssistantBotHistoryRepository botHistoryRepository;
    private final UserMapperImpl userMapper;


    private final static String START = "/start";
    private final static String USD = "/rates";
    private final static String HELP = "/help";

    @Value("${bot.username}")
    private String botUsername;

    @Autowired
    public AssistantBot(
            @Value("${bot.token}") String botToken,
            UserRepository userRepository,
            AssistantBotHistoryRepository botHistoryRepository, UserMapperImpl userMapper) {
        super(botToken);
        this.userRepository = userRepository;
        this.botHistoryRepository = botHistoryRepository;
        this.userMapper = userMapper;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(!update.hasMessage() || !update.getMessage().hasText()){
            return;
        }
        String message = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        var sender = userMapper.toAppUser(update.getMessage().getFrom());
        userRepository.save(sender);
        if(userRepository.findById(sender.getId()).isEmpty()){
            userRepository.save(sender);
        }

        switch (message){
            case START -> {
                String firstName = update.getMessage().getChat().getFirstName();
                startCommand(firstName,chatId);
            }
            case USD -> {

            }
        }

    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    private void startCommand(String firstName, Long chatId){

        var text = """
                Бот помощник приветствует тебя, %s!
                
                Мои команды могут тебе помочь:
                /rates - получить курсы валют от центрабанка на сегодняшний день
                
                /help - получить дополнительную справку по моей работе
                """;
        var formattedText = String.format(text, firstName);
        sendMessage(formattedText, chatId);
    }

    private void sendMessage(String message, Long chatId){
        String chatIdStr = String.valueOf(chatId);
        SendMessage sendMessage = new SendMessage(chatIdStr, message);
        try {
            execute(sendMessage);
        }catch (TelegramApiException e){
            throw new BotException("ошибка отправки сообщения в Телеграм", e);
        }
    }
}
