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

    public Message() {
    }

    public Message(String id, String channelId, String guildId, String content, Boolean mentionEveryone, User author, String srcGuildId) {
        this.id = id;
        this.channelId = channelId;
        this.guildId = guildId;
        this.content = content;
        this.mentionEveryone = mentionEveryone;
        this.author = author;
        this.srcGuildId = srcGuildId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getGuildId() {
        return guildId;
    }

    public void setGuildId(String guildId) {
        this.guildId = guildId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getMentionEveryone() {
        return mentionEveryone;
    }

    public void setMentionEveryone(Boolean mentionEveryone) {
        this.mentionEveryone = mentionEveryone;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getSrcGuildId() {
        return srcGuildId;
    }

    public void setSrcGuildId(String srcGuildId) {
        this.srcGuildId = srcGuildId;
    }
}