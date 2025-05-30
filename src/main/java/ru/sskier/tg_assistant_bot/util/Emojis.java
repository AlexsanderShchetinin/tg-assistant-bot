package ru.sskier.tg_assistant_bot.util;

import com.vdurmont.emoji.EmojiParser;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Emojis {

    BLUSH(EmojiParser.parseToUnicode(":blush:")),
    CONFUSED(EmojiParser.parseToUnicode(":confused:"));

    private final String emojiName;

    @Override
    public String toString() {
        return emojiName;
    }

}
