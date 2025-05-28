package ru.sskier.tg_assistant_bot.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;


/**
 * Класс содержащий информацию о пользователях
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "assistant_bot", name = "users")
public class User {

    private static final String ID_FIELD = "id";
    private static final String FIRSTNAME_FIELD = "first_name";
    private static final String ISBOT_FIELD = "is_bot";
    private static final String LASTNAME_FIELD = "last_name";
    private static final String USERNAME_FIELD = "username";
    private static final String LANGUAGECODE_FIELD = "language_code";
    private static final String CANJOINGROUPS_FIELD = "can_join_groups";
    private static final String CANREADALLGROUPMESSAGES_FIELD = "can_read_all_group_messages";
    private static final String SUPPORTINLINEQUERIES_FIELD = "supports_inline_queries";
    private static final String ISPREMIUM_FIELD = "is_premium";
    private static final String ADDEDTOATTACHMENTMENU_FIELD = "added_to_attachment_menu";

    /**
     * Уникальный идентификатор пользователя, всегда передается из Телеграмм
     */
    @Id
    @JsonProperty(ID_FIELD)
    @NonNull
    private Long id;

    @Column(name = FIRSTNAME_FIELD)
    @NonNull
    private String firstName;
    /**
     * True, if this user is a bot
     */
    @Column(name = ISBOT_FIELD)
    @NonNull
    private Boolean isBot;
    /**
     * Optional. User‘s or bot’s last name
     */
    @Column(name = LASTNAME_FIELD)
    private String lastName;
    /**
     * Optional. User‘s or bot’s username
     */
    @Column(name = USERNAME_FIELD)
    private String userName;
    /**
     * Optional. IETF language tag of the user's language
     */
    @Column(name = LANGUAGECODE_FIELD)
    private String languageCode;
    /**
     * Optional. True, if the bot can be invited to groups. Returned only in getMe.
     */
    @Column(name = CANJOINGROUPS_FIELD)
    private Boolean canJoinGroups;
    /**
     * Optional. True, if privacy mode is disabled for the bot. Returned only in getMe.
     */
    @Column(name = CANREADALLGROUPMESSAGES_FIELD)
    private Boolean canReadAllGroupMessages;
    /**
     * Optional. True, if the bot supports inline queries. Returned only in getMe.
     */
    @Column(name = SUPPORTINLINEQUERIES_FIELD)
    private Boolean supportInlineQueries;
    /**
     * Optional. True, if this user is a Telegram Premium user
     */
    @Column(name = ISPREMIUM_FIELD)
    private Boolean isPremium;
    /**
     * Optional. True, if this user added the bot to the attachment menu
     */
    @Column(name = ADDEDTOATTACHMENTMENU_FIELD)
    private Boolean addedToAttachmentMenu;


}
