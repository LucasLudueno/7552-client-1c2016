package taller2.match_client;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

/* Chat Tab is a Fragment that is used like a chat. It has a listView where we watch chat messages
 * and could send and receive chat messages from "match" person. */
public class ChatTab extends Fragment {
    /* Attributes */
    private MatchManager matchManager;
    private ChatConversation chatArrayAdapter;
    private ListView listView;
    private Button sendChat;
    private boolean userSide = true;    //true = right
    private String matchEmail;

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
    }

    /* Send Chat message to Server */
    private void sendChatMessage(EditText chatText, boolean side) {
        chatArrayAdapter.add(new ChatMessage(side, chatText.getText().toString()));
    }

    /* When Send button is pressed, the content of the ChatText is send */
    public void onSendChatClick(View v) {
        EditText chatMsg = (EditText) getView().findViewById(R.id.userChatMsg);
        sendChatMessage(chatMsg, userSide);
        chatMsg.setText("");
    }
}