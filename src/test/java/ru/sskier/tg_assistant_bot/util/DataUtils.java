package ru.sskier.tg_assistant_bot.util;


import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

public class DataUtils {


    public static int generateRandomInt(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("min must be less than or equal to max");
        }
        return (int) ((Math.random() * (max - min + 1)) + min);
    }

    public static Update createUpdateWithSimplyMessage(String text, String botFirstName, Long botId, Long chatId) {
        Message message = new Message();
        message.setText(text);
        Chat chat = new Chat();
        chat.setId(chatId);
        message.setChat(chat);
        message.setFrom(new User(botId, botFirstName, true));
        Update update = new Update();
        update.setMessage(message);
        return update;
    }
}
