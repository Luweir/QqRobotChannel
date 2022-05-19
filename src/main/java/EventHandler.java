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

    /**
     * 是否去除消息中的@机器人
     */
    private boolean removeAt = true;

    protected void onError(Exception e) {

    }

    // 进行相应处理
    // 我这里是健康打卡  包括：7杯水喝水打卡；运动打卡；视力打卡；午睡打卡； 3+1+1+1 6位搞定
    public void onAtMessage(AtMessageEvent atMessageEvent) {
        String content = atMessageEvent.getMessage().getContent();
        System.out.printf("消息内容：%s \n", content);
        int deltaState = 0;
        if (content.contains("喝水打卡")) {
            deltaState += (1 << 3);
        }
        if (content.contains("运动打卡")) {
            deltaState += (1 << 2);
        }
        if (content.contains("视力打卡")) {
            deltaState += (1 << 1);
        }
        if (content.contains("午睡打卡")) {
            deltaState += 1;
        }
        // 从数据库中拿到 state

        // 更新state状态


        // 发送信息给用户
    }

    public void onUserMessage(UserMessageEvent userMessageEvent) {
    }
}