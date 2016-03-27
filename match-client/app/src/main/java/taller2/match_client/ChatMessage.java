package taller2.match_client;

public class ChatMessage {
    public boolean isUserCharMsg;
    public String message;

    public ChatMessage(boolean isUserCharMsg, String message) {
        super();
        this.isUserCharMsg = isUserCharMsg;
        this.message = message;
    }
}