package websocker;

import java.util.*;

public class Identify {
    private String token;
    private Integer intents;
    private List<Integer> shard = new ArrayList<Integer>();

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getIntents() {
        return intents;
    }

    public void setIntents(Integer intents) {
        this.intents = intents;
    }

    public List<Integer> getShard() {
        return shard;
    }

    public void setShard(List<Integer> shard) {
        this.shard = shard;
    }
}