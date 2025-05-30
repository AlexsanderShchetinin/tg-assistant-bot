package ru.sskier.tg_assistant_bot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;


/**
 * Класс содержащий информацию о пользователях telegram
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "assistant_bot", name = "users")
@EqualsAndHashCode(of = {"id", "firstName"})
@ToString(of = {"id", "firstName"})
public class User {

    /**
     * Уникальный идентификатор пользователя, всегда передается из Телеграмм
     */
    @Id
    @NonNull
    private Long id;

    @Column(name = "first_name")
    @NonNull
    private String firstName;
    /**
     * True, if this user is a bot
     */
    @Column(name = "is_bot")
    @NonNull
    private Boolean isBot;
    /**
     * Optional. User‘s or bot’s last name
     */
    @Column(name = "last_name")
    private String lastName;
    /**
     * Optional. User‘s or bot’s username
     */
    @Column(name = "username")
    private String userName;
    /**
     * Optional. IETF language tag of the user's language
     */
    @Column(name = "language_code")
    private String languageCode;
    /**
     * Optional. True, if the bot can be invited to groups. Returned only in getMe.
     */
    @Column(name = "can_join_groups")
    private Boolean canJoinGroups;
    /**
     * Optional. True, if privacy mode is disabled for the bot. Returned only in getMe.
     */
    @Column(name = "can_read_all_group_messages")
    private Boolean canReadAllGroupMessages;
    /**
     * Optional. True, if the bot supports inline queries. Returned only in getMe.
     */
    @Column(name = "supports_inline_queries")
    private Boolean supportInlineQueries;
    /**
     * Optional. True, if this user is a Telegram Premium user
     */
    @Column(name = "is_premium")
    private Boolean isPremium;
    /**
     * Optional. True, if this user added the bot to the attachment menu
     */
    @Column(name = "added_to_attachment_menu")
    private Boolean addedToAttachmentMenu;


}
