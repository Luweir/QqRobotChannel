package websocker;

import common.User;

public class EventHandler {
    /**
     * 机器人本身的用户信息
     */
    protected User me;

    public User getMe() {
        return me;
    }

    public void setMe(User me) {
        this.me = me;
    }

    public boolean isRemoveAt() {
        return removeAt;
    }

    public void setRemoveAt(boolean removeAt) {
        this.removeAt = removeAt;
    }

    private boolean removeAt = true;

    protected void onError(Exception e) {

    }

    // 艾特处理
    public void onAtMessage(AtMessageEvent atMessageEvent) {
    }

    // 用户信息
    public void onUserMessage(UserMessageEvent userMessageEvent) {
    }
}