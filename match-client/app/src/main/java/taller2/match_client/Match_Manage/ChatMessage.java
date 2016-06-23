package taller2.match_client.Match_Manage;

/* This class represent a Chat Message that has a message and a boolean value that indicates if
 * the message was send from actual user or from a match */
public class ChatMessage {
    /* Attributes */
    private boolean isUserChatMsg;
    private String message;

    public ChatMessage(boolean isUserChatMsg, String message) {
        super();
        this.isUserChatMsg = isUserChatMsg;
        this.message = message;
    }

    public boolean isUserChatMsg() {
        return isUserChatMsg;
    }

    public String getMessage() {
        return message;
    }
}