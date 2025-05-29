package ru.sskier.tg_assistant_bot.entity;

import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;

/**
 * История диалогов с ботом
 */
@Getter
@Builder
@AllArgsConstructor
@Entity
@Table(schema = "assistant_bot", name = "assistant_bot_history")
public class AssistantBotHistory {

    /**
     * Уникальный идентификатор истории чатов с ботом
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Пользователь переписывающийся с ботом
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Уникальный идентификатор чата
     */
    @Column(name = "chat_id")
    private Long chatId;

    /**
     * дата и время отправки сообщения
     */
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime created;

    /**
     * текст сообщения от пользователя боту
     */
    @Column(name = "text_message")
    private String textMessage;

}
