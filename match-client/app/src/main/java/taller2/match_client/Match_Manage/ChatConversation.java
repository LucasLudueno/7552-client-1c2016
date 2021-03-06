package taller2.match_client.Match_Manage;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import taller2.match_client.R;

/* This class represent a Chat Array that is taken by a ListView to show the Chat Messages */
public class ChatConversation extends ArrayAdapter<ChatMessage> {
    /* Attributes */
    private List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
    private ReentrantLock mutex;
    private static final String TAG = "ChatConversation";

    public ChatConversation(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        mutex = new ReentrantLock();
    }

    @Override
    public void add(ChatMessage chatMessage) {
        Log.d(TAG, "Add Chat Message");
        mutex.lock();
            chatMessageList.add(chatMessage);
        mutex.unlock();
        super.add(chatMessage);
    }

    /* Return item count */
    public int getCount() {
        mutex.lock();
            int size = chatMessageList.size();
        mutex.unlock();
        return size;
    }

    /* Return item */
    public ChatMessage getItem(int index) {
        mutex.lock();
            ChatMessage chatMessage = chatMessageList.get(index);
        mutex.unlock();
        return chatMessage;
    }

    /* Get a View that displays the data at the specified position in the data set. In this case a chatMsg
    *  LayoutInflater instantiates a layout XML file into its corresponding View */
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "Get Chat View");
        ChatMessage chatMessage = getItem(position);
        View chatMsgView;
        mutex.lock();
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (chatMessage.isUserChatMsg()) {
                chatMsgView = inflater.inflate(R.layout.right_msg_chat, parent, false);
            }else{
                chatMsgView = inflater.inflate(R.layout.left_msg_chat, parent, false);
            }
            TextView chatText = (TextView) chatMsgView.findViewById(R.id.chatMessage);
            chatText.setText(chatMessage.getMessage());
        mutex.unlock();
        return chatMsgView;
    }
}
