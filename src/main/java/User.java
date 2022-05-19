public class User {
    /**
     * 用户ID
     */
    private String id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 用户头像地址
     */
    private String avatar;
    /**
     * 是否是机器人
     */
    private Boolean bot;
    /**
     * 特殊关联应用的 openid
     */
    private String unionOpenId;
    /**
     * 机器人关联的互联应用的用户信息
     */
    private String unionUserAccount;

    public User() {
    }

    public User(String id, String username, String avatar, Boolean bot, String unionOpenId, String unionUserAccount) {
        this.id = id;
        this.username = username;
        this.avatar = avatar;
        this.bot = bot;
        this.unionOpenId = unionOpenId;
        this.unionUserAccount = unionUserAccount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Boolean getBot() {
        return bot;
    }

    public void setBot(Boolean bot) {
        this.bot = bot;
    }

    public String getUnionOpenId() {
        return unionOpenId;
    }

    public void setUnionOpenId(String unionOpenId) {
        this.unionOpenId = unionOpenId;
    }

    public String getUnionUserAccount() {
        return unionUserAccount;
    }

    public void setUnionUserAccount(String unionUserAccount) {
        this.unionUserAccount = unionUserAccount;
    }
}