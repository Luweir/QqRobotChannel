package common;

import com.alibaba.fastjson.*;
import common.AccessInfo;
import common.ApiManager;
import okhttp3.*;
import websocker.Client;
import websocker.EventHandler;
import websocker.Gateway;
import websocker.Intent;

import java.net.URI;
import java.util.*;

public class BotCore {
    private final List<Integer> intents = new ArrayList<Integer>();
    private final AccessInfo accessInfo;

    public void setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    /**
     * 事件监听器
     */
    private EventHandler eventHandler = new EventHandler();

    public BotCore(AccessInfo accessInfo) {
        this.accessInfo = accessInfo;
    }

    private Gateway getGateway() {
        String token = getToken();
        String apiBase = getApiBase();

        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
                .url(apiBase + "/gateway/bot")
                .header("Authorization", token)
                .get()
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            ResponseBody body = response.body();
            if (body == null) {
                System.exit(1);
            }
            String result = body.string();
            System.out.println(result);
            return JSON.parseObject(result, Gateway.class);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    private String getToken() {
        return "Bot " + accessInfo.getBotAppId() + "." + accessInfo.getBotToken();
    }

    private String getApiBase() {
        String apiBase = "https://api.sgroup.qq.com";
        if (accessInfo.getUseSandBoxMode()) {
            apiBase = "https://sandbox.api.sgroup.qq.com";
        }
        return apiBase;
    }

    /**
     * 获取 API管理器 实例
     */
    public ApiManager getApiManager() {
        return new ApiManager(accessInfo);
    }

    /**
     * 注册 AT消息 事件
     */
    public void registerAtMessageEvent() {
        intents.add(Intent.AT_MESSAGES);
    }

    /**
     * 启动机器人 单例
     */
    public void start() {
        start(null, null);
    }

    /**
     * 启动机器人 指定分片
     *
     * @param shard      当前连接的分片数
     * @param totalShard 总分片数
     */
    public void start(Integer shard, Integer totalShard) {
        Gateway gateway = getGateway();
        if (gateway.getCode() == null) {
            String url = gateway.getUrl();
            System.out.println("网关地址: " + url + ", 建议分片数: " + gateway.getShards());
            try {
                Client client = new Client(new URI(url));
                client.setToken(getToken());
                client.setIntents(intents);
                client.setEventHandler(eventHandler);
                client.setShard(shard, totalShard);
                client.setConnectionLostTimeout(0);
                client.connect();
            } catch (Exception e) {
                System.out.println("WebSocket 连接地址错误!");
                System.exit(1);
            }
        } else {
            System.out.println("获取 websocker.Gateway 失败! " + gateway.getCode() + gateway.getMessage());
            System.exit(gateway.getCode());
        }
    }
}
