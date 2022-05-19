
public class Intent {
    /**
     * 频道相关事件
     */
    public int GUILDS = (1 << 0);
    /**
     * 频道成员相关事件
     */
    public int GUILD_MEMBERS = (1 << 1);
    /**
     * 用户消息事件(私域可用)
     */
    public int USER_MESSAGES = (1 << 9);
    /**
     * 消息表态相关事件
     */
    public int GUILD_MESSAGE_REACTIONS = (1 << 10);
    /**
     * 私聊消息相关事件
     */
    public int DIRECT_MESSAGE = (1 << 12);
    /**
     * 消息审核相关事件
     */
    public int MESSAGE_AUDIT = (1 << 27);
    /**
     * 论坛相关事件
     */
    public int FORUM_EVENT = (1 << 28);
    /**
     * 音频相关事件
     */
    public int AUDIO_ACTION = (1 << 29);
    /**
     * 艾特消息事件
     */
    public static int AT_MESSAGES = (1 << 30);

    private final Integer value;

    public Intent(Integer value) {
        this.value = value;
    }
}