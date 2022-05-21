package websocker;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import common.Message;
import common.User;
import org.java_websocket.enums.ReadyState;

public class Event {
    private Timer timer;
    private String sessionId;
    private Client client;
    private User me;

    public void onClientClose(int code, String reason, boolean remote) {
        System.out.println("5秒后开始尝试恢复连接...");
        try {
            Thread.sleep(5000);
            new Thread(() -> reconnect(code)).start();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("重新连接失败,请检查网络!");
        }
    }

    private void reconnect(int code) {
        System.out.println("正在重新连接...");
        if (code != 4009) {
            sessionId = null;
        }
        client.reconnect();
    }

    public void setClient(Client client) {
        this.client = client;
    }

    // op=0 即收到服务端信息 进行处理
    public void onDispatch(Payload payload) {
        String e = payload.getT();
        switch (e) {
            // 鉴权成功 收到的回复信息
            case "READY":
                Ready ready = JSON.toJavaObject((JSONObject) payload.getD(), Ready.class);
                sessionId = ready.getSessionId();
                client.getEventHandler().setMe(ready.getUser());
                me = ready.getUser();
                System.out.println("鉴权成功, 机器人已上线!");
                break;
            // 处理 @机器人发送的信息
            case "AT_MESSAGE_CREATE":
                Message atMessage = JSON.toJavaObject((JSONObject) payload.getD(), Message.class);
                System.out.printf("[AtMessage]: 频道(%s) 子频道(%s) %s (%s): %s \n",
                        atMessage.getGuildId(),
                        atMessage.getChannelId(),
                        atMessage.getAuthor().getUsername(),
                        atMessage.getAuthor().getId(),
                        atMessage.getContent());
                if (client.getEventHandler().isRemoveAt()) {
                    atMessage.setContent(atMessage.getContent().replaceAll("<@!" + me.getId() + "> ", ""));
                    atMessage.setContent(atMessage.getContent().replaceAll("<@!" + me.getId() + ">", ""));
                }
                AtMessageEvent atMessageEvent = new AtMessageEvent(this, atMessage);
                client.getEventHandler().onAtMessage(atMessageEvent);

                break;
            // 发送消息事件，代表频道内的全部消息，而不只是 at 机器人的消息。内容与 AT_MESSAGE_CREATE 相同
            case "MESSAGE_CREATE":
                Message userMessage = JSON.toJavaObject((JSONObject) payload.getD(), Message.class);
                System.out.printf("[UserMessage]: 频道(%s) 子频道(%s) %s (%s): %s \n",
                        userMessage.getGuildId(),
                        userMessage.getChannelId(),
                        userMessage.getAuthor().getUsername(),
                        userMessage.getAuthor().getId(),
                        userMessage.getContent()
                );
                UserMessageEvent userMessageEvent = new UserMessageEvent(this, userMessage);
                client.getEventHandler().onUserMessage(userMessageEvent);
                break;
            case "RESUMED":
                System.out.println("恢复连接成功, 离线消息已处理!");
                break;
            default:
                System.out.println("未知事件: " + e);
        }
    }

    // op=7
    public void onReconnect() {
        System.out.println("服务端通知客户端重连 !");
    }

    // op=9
    public void onInvalidSession() {
        System.out.println("鉴权失败!");
    }

    // op=10 进行鉴权
    public void onHello(Payload payload) {
        Hello hello = JSON.toJavaObject((JSONObject) payload.getD(), Hello.class);
        if (sessionId == null || sessionId.isEmpty()) {
            System.out.println("正在发送鉴权...");
            sendIdentify();
        } else {
            System.out.println("正在发送恢复...");
            sendResumed();
        }
        if (timer != null) {
            timer.cancel();
        }
        // 心跳机制
        startHeartbeatTimer(hello.getHeartbeatInterval());
    }

    private void startHeartbeatTimer(Integer i) {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (client.getReadyState() == ReadyState.OPEN) {
                    Payload payload = new Payload();
                    payload.setOp(1);
                    payload.setD(client.getSeq());
                    System.out.println("向服务端发送心跳.");
                    client.send(JSON.toJSONString(payload));
                }
            }
        };
        // 每 i ms 发送一次
        timer.schedule(task, i, i);
    }

    private void sendResumed() {
        JSONObject data = new JSONObject();
        data.put("token", client.getToken());
        data.put("session_id", sessionId);
        data.put("seq", client.getSeq());
        Payload payload = new Payload();
        payload.setOp(6);
        payload.setD(data);
        client.send(JSON.toJSONString(payload));
    }

    private void sendIdentify() {
        Identify identify = new Identify();
        Integer shard = client.getShard();
        Integer totalShard = client.getTotalShard();
        if (shard != null && totalShard != null) {
            identify.setShard(Arrays.asList(shard, totalShard));
        }
        int intentsNum = 0;
        for (Integer intent : client.getIntents()) {
            intentsNum = intentsNum | intent;
        }
        identify.setToken(client.getToken());
        identify.setIntents(intentsNum);
        Payload identifyPayload = new Payload();
        identifyPayload.setOp(2);
        identifyPayload.setD(identify);
        client.send(JSON.toJSONString(identifyPayload));
    }

    // OP 11
    public void onHeartbeat() {
        System.out.println("已收到服务端心跳.");
    }
}
