package websocker;

import java.util.EventObject;

/**
 * 艾特 消息事件
 */
public class AtMessageEvent extends EventObject {
    private final Message message;

    public AtMessageEvent(Object source, Message message) {
        super(source);
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}