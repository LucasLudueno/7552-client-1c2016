package taller2.match_client;

import android.app.AlertDialog;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import taller2.match_client.Helpers.ActivityHelper;
import taller2.match_client.Match_Manage.ChatConversation;
import taller2.match_client.Match_Manage.MatchManagerProxy;
import taller2.match_client.Request.ClientToServerTask;

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
    private AlertDialog internetDisconnectWindow;
    private AlertDialog unavailableServiceWindow;
    protected static final int GET_CONVERSATION_SLEEP_TIME = 3000;  // 3 seg
    protected static final int GET_CONVERSATION_CODE = 2;
    private static final String TAG = "ChatTab";

    private List<ClientToServerTask> requests;

    public ChatTab() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        matchEmail = getArguments().getString(getResources().getString(taller2.match_client.R.string.email));
        return inflater.inflate(taller2.match_client.R.layout.activity_match_chat, container, false);
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
        listView = (ListView) getView().findViewById(taller2.match_client.R.id.chatMsgList);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        // internetDisconnectWindows
        internetDisconnectWindow = new AlertDialog.Builder(getContext()).create();
        internetDisconnectWindow.setTitle(getResources().getString(taller2.match_client.R.string.internet_disconnect_error_title_en));
        internetDisconnectWindow.setMessage(getResources().getString(taller2.match_client.R.string.internet_disconnect_error_en));

        // UnavailableServiceWindow
        unavailableServiceWindow = new AlertDialog.Builder(getContext()).create();
        unavailableServiceWindow.setTitle(getResources().getString(taller2.match_client.R.string.unavailable_service_title_en));
        unavailableServiceWindow.setMessage(getResources().getString(taller2.match_client.R.string.unavailable_service_error_en));

        // To scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });

        // Send Button
        sendChat = (Button) getView().findViewById(taller2.match_client.R.id.sendChatMsg);
        sendChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendChatClick(v);
            }
        });

        // Request List
        requests = new ArrayList<ClientToServerTask>();

        /* Timer */
        getConversationTimer = new Thread(new GetConversations());
        getConversationTimer.start();

        Log.i(TAG, "ChatTab is created");
    }

    /* Send Chat message to Server */
    private void sendChatMessage(EditText chatText, boolean side) {
        if (!ActivityHelper.checkConection(getContext())) {
            internetDisconnectWindow.show();
            return;
        }

        // Clean Message field
        String chatString = chatText.getText().toString();
        if (chatString.compareTo("") == 0) {
            return;
        }

        JSONObject conversation = new JSONObject();
        JSONArray messages = new JSONArray();
        JSONObject msg = new JSONObject();
        try {
            msg.put(getResources().getString(taller2.match_client.R.string.msg),chatString);
            messages.put(msg);
            conversation.put(getResources().getString(taller2.match_client.R.string.email_src),userEmail);
            conversation.put(getResources().getString(taller2.match_client.R.string.email_dst),matchEmail);
            conversation.put(getResources().getString(taller2.match_client.R.string.messages),messages);
        } catch (JSONException e) {
            Log.e(TAG, "Can't construct sendConversation Json Request");
        }

        //Conversation to MatchManager
        try {
            messages = new JSONArray();
            JSONObject conversationMM = new JSONObject();
            JSONObject conversationJson = new JSONObject();
            msg.put(getResources().getString(taller2.match_client.R.string.send_from),userEmail);
            messages.put(msg);
            conversationJson.put(getResources().getString(taller2.match_client.R.string.email), matchEmail);
            conversationJson.put(getResources().getString(taller2.match_client.R.string.messages), messages);
            conversationMM.put(getResources().getString(taller2.match_client.R.string.conversation), conversationJson);
            matchManager.addConversation(conversationMM, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Send conversation to Server
        JSONObject conversationToServer = new JSONObject();
        try {
            conversationToServer.put(getResources().getString(taller2.match_client.R.string.conversation), conversation);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = MainActivity.ipServer;//getResources().getString(R.string.server_ip); //TODO: SACAR
        String uri = getResources().getString(taller2.match_client.R.string.send_conversation_uri);;
        SendConversationTask sendConversation = new SendConversationTask();
        requests.add(sendConversation);
        sendConversation.execute("POST", url, uri, conversationToServer.toString());
        //MockServer.sendConversation(conversation.toString());                            // MOCK TEST
        Log.d(TAG, "Send Chat to Server: " + conversationToServer.toString());
    }

    /* When Send button is pressed, the content of the ChatText is send */
    public void onSendChatClick(View v) {
        EditText chatMsg = (EditText) getView().findViewById(taller2.match_client.R.id.userChatMsg);
        sendChatMessage(chatMsg, userSide);
        chatMsg.setText("");
    }

    /* Check SendChat response from Server */
    private void checkSendConversationResponseFromServer(String response) {
        Log.d(TAG, "Response of SendConversation from Server is received: " + response);

        String responseCode = response.split(":", 2)[0];
        String responseMsg = response.split(":", 2)[1];

        if (responseCode.compareTo(getResources().getString(taller2.match_client.R.string.ok_response_code_send_conversation)) != 0) {
            unavailableServiceWindow.show();
        }
    }

    /* Check response from Server after sending get conversation request. If new conversations are
     * received, MatchManager keep this ones. */
    private void checkGetConversationResponseFromServer(String response) {
        Log.d(TAG, "Get Conversation Response from Server is received: " + response);
        String responseCode = response.split(":", 2)[0];
        String conversation = response.split(":", 2)[1];

        if (responseCode.equals(getResources().getString(taller2.match_client.R.string.ok_response_code_get_conversation))) {
            JSONObject conversationJson = null;
            try {
                conversationJson = new JSONObject(conversation);
            } catch (JSONException e) {
                Log.w(TAG, "Can't process Matches Conversation Json received from Server");
            }
            matchManager.addConversation(conversationJson, true);
        }
    }

    /* Send a request asking if there are new conversations */
    private void sendGetConversationsRequestToServer() {
            JSONObject convRequest = new JSONObject();
            try {
                convRequest.put(getResources().getString(taller2.match_client.R.string.email_src), userEmail);
                convRequest.put(getResources().getString(taller2.match_client.R.string.email_dst), matchEmail);
            } catch (JSONException e) {
                Log.w(TAG, "Can't create GetConversation Json Request");
            }
            if (ActivityHelper.checkConection(getContext())) {
                Log.d(TAG, "Send GetConversation Request to Server: " + convRequest.toString());
                String url = MainActivity.ipServer;//getResources().getString(R.string.server_ip);            //TODO: SACAR
                String uri = getResources().getString(taller2.match_client.R.string.get_conversation_uri);;
                SendGetConversationTask getConversation = new SendGetConversationTask();
                requests.add(getConversation);
                getConversation.execute("POST", url, uri, convRequest.toString());
                //checkGetConversationResponseFromServer(MockServer.getConversation(convRequest.toString())); // MOCK TEST
            }
    }

    /* Send Conversation to Server */
    private class SendConversationTask extends ClientToServerTask {
        @Override
        protected void onPostExecute(String dataGetFromServer){
            checkSendConversationResponseFromServer(dataGetFromServer);
        }
    }

    /* Send GetConversation to Server */
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

        // Cancel all request
        for (int i = 0; i < requests.size(); ++i) {
            ClientToServerTask request = requests.get(i);
            request.cancel(true);
        }
        super.onDestroy();
    }
}