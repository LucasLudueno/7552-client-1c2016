package taller2.match_client;

/* This class represent a Chat Message that will be inserted in the chatList inside ChatActivity */
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