package taller2.match_client;

import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;


/*  */
public class MatchManager {
    private static final MatchManager matchManager = new MatchManager();
    private Lock mutex;
    private HashMap<String, ChatConversation> conversations;
    private HashMap<String, JSONObject> matches;
    private HashMap<String, JSONObject> possibleMatches;
    private Context androidContext;
    private String userEmail;

    MatchManager() {
        conversations = new HashMap<String, ChatConversation>();
        matches = new HashMap<String, JSONObject>();
        possibleMatches = new HashMap<String, JSONObject>();
    }

    /*  */
    public static MatchManager getInstance() {
        return matchManager;
    }

    /*  */
    public void setData(Context context, String matchFile, String conversationsFile, String userMail) {
        androidContext = context;
        userEmail = userMail;
        FileManager fileManager = new FileManager(androidContext);
        String matchList = "";
        String conversationsList = "";
        try {
            // read files
            matchList = fileManager.readFile(matchFile);
            conversationsList = fileManager.readFile(conversationsFile);
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

    public String getUserEmail() {
        return userEmail;
    }

    /*  */
    public void addMatch(JSONObject matchData) throws JSONException {
        String email = "";
        email = matchData.getString(androidContext.getResources().getString(R.string.email));
        ChatConversation conversation = new ChatConversation(androidContext, R.layout.right_msg_chat);
        conversations.put(email, conversation);
        matches.put(email, matchData);
    }

    /*  */
    public void addPossibleMatches(JSONArray possibleMatchList) throws JSONException {
        for (int i = 0; i < possibleMatchList.length(); ++i) {
            JSONObject possibleMatch = possibleMatchList.getJSONObject(i);
            String email = possibleMatch.getString(androidContext.getResources().getString(R.string.email));
            possibleMatches.put(email, possibleMatch);
        }
    }

    /*  */
    public boolean removePossibleMatch(String pmEmail) {
        if (possibleMatches.containsKey(pmEmail)) {
            possibleMatches.remove(pmEmail);
            return true;
        }
        return false;
    }

    /*  */
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

    /*  */
    public boolean addConversation(JSONObject conversation) {
        try {
            String matchEmail = conversation.getString("email");
            if (!matches.containsKey(matchEmail)) {
                return false;
            }

            /*if (!conversations.containsKey(matchEmail)) { //CONTEMPLADO
                ChatConversation chatConversation = new ChatConversation(androidContext, R.layout.right_msg_chat);
                conversation.put(matchEmail,chatConversation);
            }*/

            ChatConversation chatConversation = conversations.get(matchEmail);
            JSONArray messages = conversation.getJSONArray("messages");
            for (int i = 0; i < messages.length(); ++i) {
                JSONObject message = messages.getJSONObject(i);
                String emailSource = message.getString("sendFrom");
                String stringMessage = message.getString("msg");
                //int time = message.getInt("time");

                if (emailSource.compareTo(userEmail) == 0) {
                    chatConversation.add(new ChatMessage(true, stringMessage));  // true = userMsg
                } else {
                    chatConversation.add(new ChatMessage(false, stringMessage)); // false = matchMsg
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    /*  */
    public List<JSONObject> getMatches() {
        List<JSONObject> matchList = new ArrayList<JSONObject>();
        for (JSONObject match : matches.values()) {
            matchList.add(match);
        }
        return matchList;
    }

    /*  */
    public ChatConversation getConversation(String matchEmail) {
        if (conversations.containsKey(matchEmail)) {
            return conversations.get(matchEmail);
        }
        return null;
    }

}
