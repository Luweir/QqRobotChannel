package websocker;

import java.util.EventObject;

public class UserMessageEvent extends EventObject {
    private final Message message;

    /**
     * Constructs a prototypical websocker.Event.
     */
    public UserMessageEvent(Object source, Message message) {
        super(source);
        this.message = message;
    }
}
