package ru.sskier.tg_assistant_bot.exception;

public class BotException extends RuntimeException {

    public BotException(String message, Throwable cause) {
        super(message, cause);
    }
}
