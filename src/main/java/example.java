import com.alibaba.fastjson.JSONArray;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

class WsClient extends WebSocketClient {

    public WsClient(URI serverUri, HashMap<String, String> headers) {
        super(serverUri, headers);
    }

    @Override
    public void onOpen(ServerHandshake arg0) {
        System.out.println("握手成功");

    }

    @Override
    public void onClose(int arg0, String arg1, boolean arg2) {
        System.out.println("连接关闭");
    }

    @Override
    public void onError(Exception arg0) {
        System.out.println("发生错误" + arg0);
    }

    @Override
    public void onMessage(String arg0) {
        System.out.println("收到信息");
        System.out.println("收到消息" + arg0);
    }
}

public class example {
    public static String doGet(String url) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Authorization", "Bot 102006259.ykj14TlmLKqbWHUyAYzbwxaMUTflTg94");
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.54 Safari/537.36");
        try {
            HttpResponse response = httpClient.execute(httpGet);
            System.out.println(response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                //返回json格式
                String res = EntityUtils.toString(response.getEntity(), Charset.defaultCharset());
                // 转换成json格式
//                JSONObject jsonObject = (JSONObject) JSON.parse(res);
//                System.out.println(jsonObject.get("url") );
                return res;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    // 消息处理
    public static void messageHandler() {

    }

    public static String ws() {
        String url = "https://api.sgroup.qq.com/gateway/bot";
        String response = doGet(url);
        return response;
    }

    // 监听事件
    public static void listenEvent() {
        // 调用 /gateway 或 /gateway/bot 接口获取网关地址 通过api获取websocket链接
        String wsAp = ws();
        System.out.println(wsAp);
        // 然后进行 websocket 连接
        String url = "wss://api.sgroup.qq.com/websocket";
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bot 102006259.ykj14TlmLKqbWHUyAYzbwxaMUTflTg94");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.54 Safari/537.36");
        try {
            WsClient myClient = new WsClient(new URI(url), headers);
            myClient.connect();
            // 判断是否连接成功，未成功后面发送消息时会报错
            while (!myClient.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
                System.out.println("连接中···请稍后");
                Thread.sleep(1000);
            }
            int intent = (1 << 30);
            // 鉴权
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("op", 2);
            HashMap<String, Object> temp = new HashMap<String, Object>();
            temp.put("token", "Bot 102006259.ykj14TlmLKqbWHUyAYzbwxaMUTflTg94");
            temp.put("intents", intent);
            temp.put("shard", new int[]{0, 4});
            hashMap.put("d", temp);

            myClient.send(JSONArray.toJSON(hashMap).toString());
            System.out.println("发送成功");

            // 监听
            hashMap = new HashMap<String, Object>();
            hashMap.put("op", 0);
            temp = new HashMap<String, Object>();
            temp.put("token", "Bot 102006259.ykj14TlmLKqbWHUyAYzbwxaMUTflTg94");
            temp.put("intents", intent);
            hashMap.put("d", temp);
            myClient.send(JSONArray.toJSON(hashMap).toString());
            // 鉴权成功之后，就需要按照周期进行心跳发送
//            while (true) {
//                HashMap<String, Object> hashMap1 = new HashMap<String, Object>();
//                hashMap1.put("op", 1);
//                hashMap1.put("d", null);
//                myClient.send(JSONArray.toJSON(hashMap1).toString());
//                HashMap<String, Object> hashMap2 = new HashMap<String, Object>();
//
//                hashMap2.put("op", 0);
//                hashMap2.put("intents", (1 << 12));
//                myClient.send(JSONArray.toJSON(hashMap2).toString());
//                System.out.println("事件订阅");
//                Thread.sleep(4000);
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void getMessages()
    {
        String channelId="18051574232702398972";
        String url = "https://api.sgroup.qq.com/channels/"+channelId+"/messages";
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Authorization", "Bot 102006259.ykj14TlmLKqbWHUyAYzbwxaMUTflTg94");
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.54 Safari/537.36");
        try {
            HttpResponse response = httpClient.execute(httpGet);
            System.out.println(response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                //返回json格式
                String res = EntityUtils.toString(response.getEntity(), Charset.defaultCharset());
                // 转换成json格式
//                JSONObject jsonObject = (JSONObject) JSON.parse(res);
//                System.out.println(jsonObject.get("url") );
                System.out.println(res);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        String domain = "https://api.sgroup.qq.com";
        String userMeURI = "/users/@me";
        String gatewayBotURI = "/gateway/bot";
        String getGuilds = "/users/@me/guilds";
//        System.out.println(doGet(domain + getGuilds));
        getMessages();
//        System.out.println(ws());
//        listenEvent();
//        String s = "{'op':2,'d':'123','in':{'ind':123}}";
//        JSONObject parse = (JSONObject) JSON.parse(s);
//        JSONObject pp = (JSONObject) parse.get("in");
//        System.out.println(pp.get("ind"));
    }
}
