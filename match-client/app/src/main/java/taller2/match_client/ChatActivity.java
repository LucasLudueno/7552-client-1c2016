package taller2.match_client;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Button;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.List;

/* Chat Activity have a chat conversation between user and a match. It send to Server every message
 * that the user send and request ask for match messages. */
public class ChatActivity extends AppCompatActivity {

    /* Attributes */
    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private Button sendChat;
    private boolean side = false;

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
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right_msg_chat);
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
    }

    /* Send Chat message to Server */
    private void sendChatMessage(EditText chatText) {
        chatArrayAdapter.add(new ChatMessage(side, chatText.getText().toString()));
        side = !side;
    }

    /* When Send button is pressed, the content of the ChatText is send */
    public void onSendChatClick(View v) {
        EditText chatMsg = (EditText) findViewById(R.id.userChatMsg);
        sendChatMessage(chatMsg);
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


