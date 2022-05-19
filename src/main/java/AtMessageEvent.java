import java.util.EventObject;

/**
 * 艾特消息事件
 *
 * @author 真心
 * @since 2021/12/9 12:08
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