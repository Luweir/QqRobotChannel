package common;

public class AccessInfo {
    // AppID
    private Integer botAppId;
    // token
    private String botToken;
    // Secret
    private String botSecret;

    public AccessInfo() {
    }

    // Whether to use sandbox mode
    private Boolean useSandBoxMode = false;

    public AccessInfo(Integer botAppId, String botToken) {
        this.botAppId = botAppId;
        this.botToken = botToken;
    }

    public Integer getBotAppId() {
        return botAppId;
    }

    public void setBotAppId(Integer botAppId) {
        this.botAppId = botAppId;
    }

    public String getBotToken() {
        return botToken;
    }

    @Override
    public String toString() {
        return "common.AccessInfo{" +
                "botAppId=" + botAppId +
                ", botToken='" + botToken + '\'' +
                ", botSecret='" + botSecret + '\'' +
                ", useSandBoxMode=" + useSandBoxMode +
                '}';
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    public String getBotSecret() {
        return botSecret;
    }

    public void setBotSecret(String botSecret) {
        this.botSecret = botSecret;
    }

    public Boolean getUseSandBoxMode() {
        return useSandBoxMode;
    }


    // Use sandbox mode
    public void useSandBoxMode() {
        this.useSandBoxMode = true;
    }

    // 设置沙箱模式
    public void setUseSandBoxMode(Boolean useSandBoxMode) {
        this.useSandBoxMode = useSandBoxMode;
    }
}