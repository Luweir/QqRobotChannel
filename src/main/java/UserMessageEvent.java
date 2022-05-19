import java.util.EventObject;

public class UserMessageEvent extends EventObject {
    private final Message message;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public UserMessageEvent(Object source, Message message) {
        super(source);
        this.message = message;
    }
}
