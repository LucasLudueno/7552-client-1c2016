package taller2.match_client;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/* Chat Tab is a Fragment that is used like a chat. It has a listView where we watch chat messages
 * and could send and receive chat messages from "match" person. */
public class ChatTab extends Fragment {
    /* Attributes */
    private MatchManagerProxy matchManager;
    private ChatConversation chatArrayAdapter;
    private ListView listView;
    private Button sendChat;
    private boolean userSide = true;    //true = right side
    private String matchEmail;
    private String userEmail;
    private Thread getConversationTimer;
    //private Thread sendConversationTimer;
    protected static final int GET_CONVERSATION_SLEEP_TIME = 3000;  // 3 seg
    protected static final int GET_CONVERSATION_CODE = 2;
    private static final String TAG = "ChatTab";

    public ChatTab() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        matchEmail = getArguments().getString(getResources().getString(R.string.email));
        return inflater.inflate(R.layout.activity_match_chat, container, false);
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        Log.i(TAG, "Create ChatTab");
        super.onActivityCreated(savedInstanceState);

        /*** Match Manager ***/
        matchManager = MatchManagerProxy.getInstance();
        userEmail = matchManager.getEmail();

        // Chat Conversation
        chatArrayAdapter = matchManager.getConversation(matchEmail);
        listView = (ListView) getView().findViewById(R.id.chatMsgList);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        // To scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });

        // Send Button
        sendChat = (Button) getView().findViewById(R.id.sendChatMsg);
        sendChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendChatClick(v);
            }
        });

        /* Timer */
        getConversationTimer = new Thread(new GetConversations());
        getConversationTimer.start();

        Log.i(TAG, "ChatTab is created");
    }

    /* Send Chat message to Server */
    private void sendChatMessage(EditText chatText, boolean side) {
        String chatString = chatText.getText().toString();
        if (chatString.compareTo("") != 0) {
            chatArrayAdapter.add(new ChatMessage(side, chatText.getText().toString()));
        }
        //TODO: Mandar chat al Server.
        //Log.d(TAG, "Send Chat to Server: " + chatString);
    }

    /* When Send button is pressed, the content of the ChatText is send */
    public void onSendChatClick(View v) {
        EditText chatMsg = (EditText) getView().findViewById(R.id.userChatMsg);
        sendChatMessage(chatMsg, userSide);
        chatMsg.setText("");
    }

    /* Check SendChat response from Server */
    private void checkSendChatResponseFromServer(String response) {
        Log.d(TAG, "Response code from Server is received: " + response);

        String responseCode = response.split(":", 2)[0];
        String responseMsg = response.split(":", 2)[1];

        if (responseCode.equals(getResources().getString(R.string.ok_response_code_login))) {

        } else {

        }
    }

    /* Check response from Server after sending get conversation request. If new conversations are
     * received, MatchManager keep this ones. */
    private void checkGetConversationResponseFromServer(String response) {
        Log.d(TAG, "Get Conversation Response from Server is received: " + response);
        String responseCode = response.split(":", 2)[0];
        String conversation = response.split(":", 2)[1];

        if (responseCode.equals(getResources().getString(R.string.ok_response_code_login))) {
            JSONObject conversationJson = null;
            try {
                conversationJson = new JSONObject(conversation);
            } catch (JSONException e) {
                Log.w(TAG, "Can't process Matches Conversation Json received from Server");
            }
            matchManager.addConversation(conversationJson);
        }
    }

    /* Send a request asking if there are new conversations */
    private void sendGetConversationsRequestToServer() {
        List<JSONObject> matches = matchManager.getMatches();
        for (int i = 0; i < matches.size(); ++i) {
            JSONObject match = matches.get(i);
            String matchEmail = "";
            JSONObject convRequest = new JSONObject();
            try {
                matchEmail = match.getString(getResources().getString(R.string.email));
                convRequest.put(getResources().getString(R.string.email_src),
                        matchEmail);
                convRequest.put(getResources().getString(R.string.email_dst),
                        userEmail);
            } catch (JSONException e) {
                Log.w(TAG, "Can't create GetConversation Json Request");
            }
            //if (ActivityHelper.checkConection(getApplicationContext())) {
            Log.d(TAG, "Send GetConversation Request to Server: " + convRequest.toString());
            //SendGetConversationTask getMatchs = new SendGetConversationTask();
            checkGetConversationResponseFromServer(
                    MockServer.getConversation(convRequest.toString()));
            //} else {

            // }
        }
    }


    /* Send Conversation to Server */
    private class SendConversationTask extends ClientToServerTask {
        @Override
        protected void onPostExecute(String dataGetFromServer){
            checkSendChatResponseFromServer(dataGetFromServer);
        }
    }

    /* Get Conversation to Server */
    private class SendGetConversationTask extends ClientToServerTask {
        @Override
        protected void onPostExecute(String dataGetFromServer){
            checkGetConversationResponseFromServer(dataGetFromServer);
        }
    }


    /* Thread Handler, handle get and send conversation events */
    Handler conversationHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_CONVERSATION_CODE: // Send Get conversation request to Server
                    sendGetConversationsRequestToServer();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /* Get Conversations Thread. After a time send get conversation request to Server */
    class GetConversations implements Runnable {
        public void run() {
            while (! Thread.currentThread().isInterrupted()) {
                Log.i(TAG, "Get Conversations Thread wake up");
                Message message = new Message();
                message.what = GET_CONVERSATION_CODE;
                ChatTab.this.conversationHandler.sendMessage(message);

                try {
                    Thread.sleep(GET_CONVERSATION_SLEEP_TIME);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        try {
            getConversationTimer.interrupt();
            getConversationTimer.join();
        } catch (InterruptedException e) {
            Log.w(TAG, "Can't join threads");
        }
        super.onDestroy();
    }
}