import com.alibaba.fastjson.JSON;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.List;

public class Client extends WebSocketClient {
    private final Event event;

    private String token;

    private List<Integer> intents;

    public Event getEvent() {
        return event;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<Integer> getIntents() {
        return intents;
    }

    public void setIntents(List<Integer> intents) {
        this.intents = intents;
    }

    public EventHandler getEventHandler() {
        return eventHandler;
    }

    public void setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public Integer getShard() {
        return shard;
    }

    public void setShard(Integer shard) {
        this.shard = shard;
    }

    public Integer getTotalShard() {
        return totalShard;
    }

    public void setTotalShard(Integer totalShard) {
        this.totalShard = totalShard;
    }

    private EventHandler eventHandler;

    private Integer seq;

    private Integer shard;

    private Integer totalShard;

    public Client(URI uri) {
        super(uri);
        event = new Event();
        event.setClient(this);
    }

    public void setShard(Integer shard, Integer totalShard) {
        this.shard = shard;
        this.totalShard = totalShard;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        Thread.currentThread().setName("WebSocket");
        System.out.println("已连接至网关!");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("收到消息: " + message);
        Payload payload = JSON.parseObject(message, Payload.class);
        if (payload.getS() != null) {
            seq = payload.getS();
        }
        switch (payload.getOp()) {
            case 0:
                event.onDispatch(payload);
                break;
            case 7:
                event.onReconnect();
                break;
            case 9:
                event.onInvalidSession();
                break;
            case 10:
                event.onHello(payload);
                break;
            case 11:
                event.onHeartbeat();
                break;
            default:
                System.out.println("未知消息类型: OpCode " + payload.getOp());
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        event.onClientClose(code, reason, remote);
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("发生错误: " + ex.getMessage());
        ex.printStackTrace();
        eventHandler.onError(ex);
    }

    @Override
    public void send(String text) {
        super.send(text);
        System.out.println("发送消息: " + text);
    }


}