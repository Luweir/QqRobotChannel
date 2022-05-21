package websocker;

import common.User;

import java.util.List;

public class Ready {
    private Integer version;
    private String sessionId;
    private User user;
    private List<Integer> shard;

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Integer> getShard() {
        return shard;
    }

    public void setShard(List<Integer> shard) {
        this.shard = shard;
    }
}