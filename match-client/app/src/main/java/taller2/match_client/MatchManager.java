package taller2.match_client;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/* Match Manager, manage user matches and conversations (can save and get) */
public class MatchManager implements MatchManagerInterface {
    /* Attributes */
    private ReentrantLock mutex_conversations;
    private ReentrantLock mutex_matches;
    private ConversationBuffer conversationBuffer;
    private HashMap<String, ChatConversation> conversations;
    private HashMap<String, JSONObject> matches;
    private Context androidContext = null;
    private String userEmail = "";
    private MatchList matchList;

    private static final String TAG = "MatchManager";

    MatchManager(String email, Context context) {
        conversations = new HashMap<String, ChatConversation>();
        conversationBuffer = new ConversationBuffer(context);
        matches = new HashMap<String, JSONObject>();
        mutex_matches = new ReentrantLock();
        mutex_conversations = new ReentrantLock();
        matchList = new MatchList(context);
        userEmail = email;
        androidContext = context;
    }

    /* Add match */
    public void addMatch(JSONObject matchData) throws JSONException {
        Log.d(TAG, "Add Match");
        String email = "";
        email = matchData.getString(androidContext.getResources().getString(R.string.email));

        mutex_matches.lock();
            if (matches.containsKey(email)) {
                mutex_matches.unlock();
                Log.w(TAG, "Try to add match that exists");
                return;
            }
        mutex_matches.unlock();

        ChatConversation conversation = new ChatConversation(androidContext, R.layout.right_msg_chat);

        mutex_conversations.lock();
            conversations.put(email, conversation);
        mutex_conversations.unlock();

        mutex_matches.lock();
            matches.put(email, matchData);
        mutex_matches.unlock();

        matchList.addItem(matchData);
    }

    /* Add a conversation with some match */
    public boolean addConversation(JSONObject completeConversation, boolean isNewConversation) {
        Log.d(TAG, "Add Conversation");
        JSONObject conversation = null;
        String matchEmail = null;
        try {
            conversation = completeConversation.getJSONObject(androidContext.getResources().getString(R.string.conversation));
            matchEmail = conversation.getString(androidContext.getResources().getString(R.string.email));
        } catch (JSONException e) {
            Log.w(TAG, "Can't decode conversation");
        }

        mutex_matches.lock();
            boolean containsKey = matches.containsKey(matchEmail);
        mutex_matches.unlock();

        if (!containsKey) {
            return false;
        }

        mutex_conversations.lock();
        try {
            ChatConversation chatConversation = conversations.get(matchEmail);
            JSONArray messages = conversation.getJSONArray(androidContext.getResources().getString(R.string.messages));
            for (int i = 0; i < messages.length(); ++i) {
                JSONObject message = messages.getJSONObject(i);

                if (isNewConversation) {
                    conversationBuffer.addConversation(matchEmail, message);
                }

                String emailSource = message.getString(androidContext.getResources().getString(R.string.send_from));
                String stringMessage = message.getString(androidContext.getResources().getString(R.string.msg));
                //int time = message.getInt("time");

                if (emailSource.compareTo(userEmail) == 0) {
                    chatConversation.add(new ChatMessage(true, stringMessage));  // true = userMsg
                } else {
                    chatConversation.add(new ChatMessage(false, stringMessage)); // false = matchMsg
                }
            }
        } catch (JSONException e) {
            Log.w(TAG, "Can't decode conversation messages");
        }
        mutex_conversations.unlock();
        return true;
    }

    /* Return the match list. */
    public List<JSONObject> getMatches() {
        Log.d(TAG, "Get Matches");
        List<JSONObject> matchList = new ArrayList<JSONObject>();
        mutex_matches.lock();
            for (JSONObject match : matches.values()) {
                matchList.add(match);
            }
        mutex_matches.unlock();
        return matchList;
    }

    /* Return match */
    public JSONObject getMatch(String matchEmail) {
        Log.d(TAG, "Get Match");
        JSONObject matchData = null;
        mutex_matches.lock();
        if (matches.containsKey(matchEmail)) {
            matchData = matches.get(matchEmail);
        }
        mutex_matches.unlock();
        return matchData;
    }

    /* Return chat conversation of match */
    public ChatConversation getConversation(String matchEmail) {
        Log.d(TAG, "Get Conversation");
        ChatConversation conversation = null;
        mutex_conversations.lock();
            if (conversations.containsKey(matchEmail)) {
                conversation = conversations.get(matchEmail);
            }
        mutex_conversations.unlock();
        return conversation;
    }

    /* Return Match List */
    public MatchList getMatchList() {
        return matchList;
    }

    /* Return email */
    public String getEmail() {
        return userEmail;
    }

    /* Update Conversations */
    public void updateConversationInFile(String fileName) {
        Log.d(TAG, "Update conversations in file");
        conversationBuffer.writeConversations(fileName);
    }
}
