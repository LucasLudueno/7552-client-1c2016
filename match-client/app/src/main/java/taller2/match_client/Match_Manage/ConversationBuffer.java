package taller2.match_client.Match_Manage;

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

import taller2.match_client.Helpers.FileManager;
import taller2.match_client.R;

/* This class represent a conversation buffer. Can add conversation with matches and then write
 * this conversations into a file. */
public class ConversationBuffer {
    private String userEmail;
    private Context context;
    private ReentrantLock mutex;
    private HashMap<String, List<JSONObject>> buffer;
    private static final String TAG = "ConversationBuffer";

    ConversationBuffer(Context context) {
        buffer = new HashMap<String, List<JSONObject>>();
        mutex = new ReentrantLock();
        this.context = context;
    }

    /* Add conversation */
    public void addConversation(String email, JSONObject message) {
        Log.d(TAG, "Add conversation");
        mutex.lock();
            if (!buffer.containsKey(email)) {
                List<JSONObject> messages = new ArrayList<>();
                buffer.put(email,messages);
            }
            buffer.get(email).add(message);
        mutex.unlock();
    }

    /* Write conversations into buffer and clean it */
    public void writeConversations(String fileName) {
        Log.d(TAG, "Write conversations in file");

        JSONObject newConversationFile = new JSONObject();
        JSONArray newConversationArray = new JSONArray();
        String conversationsList = null;
        try {
            conversationsList = FileManager.readFile(fileName, context);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject matchConversations = null;
        JSONArray conversationsArray = null;
        try {
            matchConversations = new JSONObject(conversationsList);
            conversationsArray = matchConversations.getJSONArray(context.getResources().getString(R.string.conversations));
        } catch (JSONException e) {
            Log.w(TAG, "Error while construct conversation Json");
        }
        mutex.lock();
            try {
                // combine messages between buffer and file
                for (int i = 0; i < conversationsArray.length(); ++i) {
                    JSONObject conversationStructure = conversationsArray.getJSONObject(i);
                    JSONObject conversation = conversationStructure.getJSONObject(context.getResources().getString(R.string.conversation));
                    String matchEmail = conversation.getString(context.getResources().getString(R.string.email));
                    JSONArray messages = conversation.getJSONArray(context.getResources().getString(R.string.messages));
                    if (buffer.containsKey(matchEmail)) {
                        for (int j = 0; j < buffer.get(matchEmail).size(); ++j) {
                            messages.put(buffer.get(matchEmail).get(j));
                        }
                        buffer.remove(matchEmail);
                    }
                    JSONObject newConversation = new JSONObject();
                    newConversation.put(context.getResources().getString(R.string.email), matchEmail);
                    newConversation.put(context.getResources().getString(R.string.messages), messages);
                    JSONObject conversationJson = new JSONObject();
                    conversationJson.put(context.getResources().getString(R.string.conversation), newConversation);
                    newConversationArray.put(conversationJson);
                }

                // load messages that are still in buffer
                for (String matchEmail : buffer.keySet()) {
                    JSONArray messages = new JSONArray();
                    for (int j = 0; j < buffer.get(matchEmail).size(); ++j) {
                        messages.put(buffer.get(matchEmail).get(j));
                    }
                    JSONObject newConversation = new JSONObject();
                    newConversation.put(context.getResources().getString(R.string.email), matchEmail);
                    newConversation.put(context.getResources().getString(R.string.messages), messages);
                    JSONObject conversationJson = new JSONObject();
                    conversationJson.put(context.getResources().getString(R.string.conversation), newConversation);
                    newConversationArray.put(conversationJson);
                }
                buffer.clear();

                // Write file
                newConversationFile.put(context.getResources().getString(R.string.conversations), newConversationArray);
                try {
                    FileManager.writeFile(fileName, newConversationFile.toString(), context);
                } catch (IOException e) {
                    Log.w(TAG, "Can't write conversation file");
                }
            } catch (JSONException e) {
                Log.w(TAG, "Error while construct conversation Json");
            }
        mutex.unlock();
    }
}
