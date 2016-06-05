package taller2.match_client;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

/*  */
public class MatchContainer {
    private HashMap<String, ChatConversation> conversations;
    private ReentrantLock mutex_conversations;
}
