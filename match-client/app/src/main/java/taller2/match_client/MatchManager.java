package taller2.match_client;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/* Match Manager, manage usar matches and conversations with th */
public class MatchManager {
    /* Attributes */
    private static final MatchManager matchManager = new MatchManager();
    private ReentrantLock mutex_conversations;
    private ReentrantLock mutex_matches;
    private HashMap<String, ChatConversation> conversations;
    private HashMap<String, JSONObject> matches;
    private HashMap<String, JSONObject> possibleMatches;
    private Context androidContext;
    private String userEmail;
    private MatchListAdapter matchListAdapter; // TODO: PENSAR SI LA USAMOS...

    MatchManager() {
        conversations = new HashMap<String, ChatConversation>();
        matches = new HashMap<String, JSONObject>();
        possibleMatches = new HashMap<String, JSONObject>();
        mutex_matches = new ReentrantLock();
        mutex_conversations = new ReentrantLock();
    }

    /* Return an instance */
    public static MatchManager getInstance() {
        return matchManager;
    }

    /* Initizalize. Charge matches and conversation saved in files. */
    public void setData(Context androidContext, String matchFile, String conversationsFile, String userMail) {
        this.androidContext = androidContext;
        matchListAdapter = new MatchListAdapter(androidContext);
        userEmail = userMail;
        String matchList = "";
        String conversationsList = "";
        try {
            // read files
            matchList = FileManager.readFile(matchFile, androidContext);
            conversationsList = FileManager.readFile(conversationsFile, androidContext);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            // add matches
            JSONObject matchData = new JSONObject(matchList);
            JSONArray matchesArray = matchData.getJSONArray("matches");
            for (int i = 0; i < matchesArray.length(); ++i) {
                JSONObject match = matchesArray.getJSONObject(i);
                addMatch(match);
            }

            // add conversations
            JSONObject matchConversations = new JSONObject(conversationsList);
            JSONArray conversationsArray = matchConversations.getJSONArray("conversations");
            for (int i = 0; i < conversationsArray.length(); ++i) {
                JSONObject conversation = conversationsArray.getJSONObject(i);
                addConversation(conversation);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /* Add match */
    public void addMatch(JSONObject matchData) throws JSONException {
        String email = "";
        email = matchData.getString(androidContext.getResources().getString(R.string.email));
        ChatConversation conversation = new ChatConversation(androidContext, R.layout.right_msg_chat);

        mutex_conversations.lock();
            conversations.put(email, conversation);
        mutex_conversations.unlock();

        mutex_matches.lock();
            matches.put(email, matchData);
        mutex_matches.unlock();

        matchListAdapter.addItem(matchData);
    }

    /* Add possible match */
    public void addPossibleMatches(JSONArray possibleMatchList) throws JSONException {
        for (int i = 0; i < possibleMatchList.length(); ++i) {
            JSONObject possibleMatch = possibleMatchList.getJSONObject(i);
            String email = possibleMatch.getString(androidContext.getResources().getString(R.string.email));
            possibleMatches.put(email, possibleMatch);
        }
    }

    /* Remove possible match */
    public boolean removePossibleMatch(String pmEmail) {
        if (possibleMatches.containsKey(pmEmail)) {
            possibleMatches.remove(pmEmail);
            return true;
        }
        return false;
    }

    /* Return possible match */
    public JSONObject getPossibleMatch() {
        if (possibleMatches.isEmpty()) {
            return null;
        }
        Set<String> posMatches = possibleMatches.keySet();
        List<String> list = new ArrayList<String>(posMatches);
        int random = (int)(Math.random()*(list.size()));;
        String posMatchEmailRandom = list.get(random);
        return possibleMatches.get(posMatchEmailRandom);
    }

    /* Add a conversation with some match */
    public boolean addConversation(JSONObject completeConversation) {
        JSONObject conversation = null;
        String matchEmail = null;
        try {
            conversation = completeConversation.getJSONObject(androidContext.getResources().getString(R.string.conversation));
            matchEmail = conversation.getString(androidContext.getResources().getString(R.string.email));
        } catch (JSONException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        } finally {
            mutex_conversations.unlock();
        }
        return true;
    }

    /* Return the match list. */
    public List<JSONObject> getMatches() {
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
        ChatConversation conversation = null;
        mutex_conversations.lock();
            if (conversations.containsKey(matchEmail)) {
                conversation = conversations.get(matchEmail);
            }
        mutex_conversations.unlock();
        return conversation;
    }

    /* Return Match List */
    public MatchListAdapter getMatchListAdapter() {
        return matchListAdapter;
    }

}
