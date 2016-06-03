package taller2.match_client;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.IOException;

/* Chat Tab is a Fragment that is used like a chat. It has a listView where we watch chat messages
 * and could send and receive chat messages from "match" person. */
public class ChatTab extends Fragment {
    /* Attributes */
    private MatchManager matchManager;
    private ChatConversation chatArrayAdapter;
    private ListView listView;
    private Button sendChat;
    private boolean userSide = true;    //true = right side
    private String matchEmail;

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
        matchManager = MatchManager.getInstance();

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

    /* Send Chat to Server */
    private class SendChatTask extends ClientToServerTask {
        @Override
        protected void onPostExecute(String dataGetFromServer){
            checkSendChatResponseFromServer(dataGetFromServer);
        }
    }
}