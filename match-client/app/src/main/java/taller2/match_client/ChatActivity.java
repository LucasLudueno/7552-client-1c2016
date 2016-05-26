package taller2.match_client;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Button;

/* Chat Activity have a chat conversation between user and a match. It send to Server every message
 * that the user send and request ask for match messages. */
public class ChatActivity extends AppCompatActivity {

    /* Attributes */
    private MatchManager matchManager;
    private ChatConversation chatArrayAdapter;
    private ListView listView;
    private Button sendChat;
    private boolean userSide = true;    //true = right

    /* On create Activity */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.chatToolbar);
        setSupportActionBar(toolbar);

        // Add back activity button in the toolbar
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Chat Array Adapter
        chatArrayAdapter = new ChatConversation(getApplicationContext(), R.layout.right_msg_chat);
        listView = (ListView) findViewById(R.id.chatMsgList);
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
        sendChat = (Button)findViewById(R.id.sendChatMsg);
        sendChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendChatClick(v);
            }
        });


        /*** Match Manager ***/
        matchManager = MatchManager.getInstance();
        String matchEmail = "";
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            matchEmail = bundle.getString(getResources().getString(R.string.email));
        }
        // Include conversation
        chatArrayAdapter = matchManager.getConversation(matchEmail);
        if (chatArrayAdapter == null) {
            this.finish();
        }
        listView.setAdapter(chatArrayAdapter);
    }

    /* Send Chat message to Server */
    private void sendChatMessage(EditText chatText, boolean side) {
        chatArrayAdapter.add(new ChatMessage(side, chatText.getText().toString()));
    }

    /* When Send button is pressed, the content of the ChatText is send */
    public void onSendChatClick(View v) {
        EditText chatMsg = (EditText) findViewById(R.id.userChatMsg);
        sendChatMessage(chatMsg, userSide);
        chatMsg.setText("");
    }

    /* Handle menu item click */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {    // Back to previus Activity
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


