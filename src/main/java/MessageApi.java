import java.util.*;

public class MessageApi extends BaseApi {

    protected MessageApi(AccessInfo accessInfo) {
        super(accessInfo);
    }

    /**
     * 发送文本消息
     *
     * @param channelId 子频道ID
     * @param content   文本内容
     * @param messageId 消息ID
     * @return {@link Message} 对象
     */
    public Message sendMessage(String channelId, String content, String messageId) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("content", content);
        data.put("msg_id", messageId);
        return sendMessage(channelId, data);
    }

    /**
     * 发送消息
     *
     * @param channelId 子频道ID
     * @param data      消息数据
     * @return {@link Message} 对象
     */
    private Message sendMessage(String channelId, Map<String, Object> data) {
        return post("/channels/" + channelId + "/messages", data, Message.class);
    }
}
