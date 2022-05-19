import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import okio.Buffer;

import java.io.IOException;
import java.util.*;

public abstract class BaseApi {
    private final String api;
    private final String token;

    private final OkHttpClient client = new OkHttpClient.Builder().build();
    private final MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

    protected BaseApi(AccessInfo accessInfo) {
        Integer botAppId = accessInfo.getBotAppId();
        String botToken = accessInfo.getBotToken();
        Boolean isUseSandBoxMode = accessInfo.getUseSandBoxMode();
        if (isUseSandBoxMode) {
            api = "https://sandbox.api.sgroup.qq.com";
        } else {
            api = "https://api.sgroup.qq.com";
        }
        this.token = "Bot " + botAppId + "." + botToken;
    }

    protected <T> T get(String url, Class<T> tClass) {
//        log.debug("GET Url: {}", url);
        Request request =
                new Request.Builder()
                        .url(api + url)
                        .header("Authorization", token)
                        .get()
                        .build();
        return result(request, tClass);
    }

    protected <T> T post(String path, Map<String, Object> data, Class<T> tClass) {
//        log.debug("POST Data: {}", JSON.toJSONString(data));
        RequestBody body = RequestBody.create(JSON.toJSONString(data), mediaType);
        Request request =
                new Request.Builder()
                        .url(api + path)
                        .header("Authorization", token)
                        .post(body)
                        .build();
        return result(request, tClass);
    }

    private <T> T result(Request request, Class<T> tClass) {
        Call call = client.newCall(request);
        try {
            Response response = null;
            try {
                response = call.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            int status = response.code();
//            log.debug("API请求: 状态码 {}", status);
            if (status == 204) {
//                log.debug("API请求成功: 无Body");
                return null;
            }
            ResponseBody body = response.body();
            if (body == null) {
                return null;
            }
            String bodyStr = body.string();
            if (status == 201 || status == 202) {
//                log.warn("API异步请求成功: {}", bodyStr);
                return null;
            }
            if (status == 200) {
//                log.debug("API请求成功: {}", bodyStr);
                if (tClass == JSONArray.class) {
                    //noinspection unchecked
                    return (T) JSON.parseArray(bodyStr);
                }

                if (tClass != null) {
                    return JSON.parseObject(bodyStr, tClass);
                } else {
                    return null;
                }
            }
            JSONObject obj = JSON.parseObject(bodyStr);
            Integer code = obj.getInteger("code");
            String message = obj.getString("message");
            String traceId = response.header("X-Tps-Trace-Id");
//            log.error("API请求错误!");
//            log.error("TraceId: {}", traceId);
//            log.error("请求地址: {}", request.url().encodedPath());
//            log.error("状态码: {}", status);
//            log.error("返回内容: {}", bodyStr);
            // 解析requestBody
            RequestBody requestBody = request.body();
            if (requestBody != null) {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);
//                log.error("请求体: {}", buffer.readUtf8());
            }
//            exception(code, message, traceId);
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}