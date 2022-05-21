package websocker;

public class Gateway {
    private Integer code;
    private String message;
    private String url;
    private Integer shards;
    private SessionStartLimit sessionStartLimit;

    public static class SessionStartLimit {
        private Integer total;
        private Integer remaining;
        private Integer resetAfter;
        private Integer maxConcurrency;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getShards() {
        return shards;
    }

    public void setShards(Integer shards) {
        this.shards = shards;
    }

    public SessionStartLimit getSessionStartLimit() {
        return sessionStartLimit;
    }

    public void setSessionStartLimit(SessionStartLimit sessionStartLimit) {
        this.sessionStartLimit = sessionStartLimit;
    }
}