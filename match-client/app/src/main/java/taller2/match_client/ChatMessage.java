package taller2.match_client;

/* This class represent a Chat Message */
public class ChatMessage {

    /* Attributes */
    public boolean isUserCharMsg;
    public String message;

    public ChatMessage(boolean isUserCharMsg, String message) {
        super();
        this.isUserCharMsg = isUserCharMsg;
        this.message = message;
    }
}