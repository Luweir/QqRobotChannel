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
}