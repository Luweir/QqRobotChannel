public class ApiManager {
    /**
     * 访问信息
     */
    private AccessInfo accessInfo;

    public ApiManager(AccessInfo accessInfo) {
        this.accessInfo = accessInfo;
    }

    /**
     * 获取 消息API 实例
     */
    public MessageApi getMessageApi() {
        return new MessageApi(accessInfo);
    }
}
