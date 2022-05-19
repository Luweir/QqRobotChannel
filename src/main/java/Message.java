public class Message {
    /**
     * 消息ID
     */
    private String id;
    /**
     * 子频道ID
     */
    private String channelId;
    /**
     * 频道ID
     */
    private String guildId;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 是否为 @全体成员 消息
     */
    private Boolean mentionEveryone;
    /**
     * 消息创建人
     */
    private User author;

    private String srcGuildId;
}