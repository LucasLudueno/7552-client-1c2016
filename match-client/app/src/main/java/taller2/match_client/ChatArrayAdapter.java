package taller2.match_client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/* This class represent a Chat Array that is taken by a ListView to show the Chat Messages */
public class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {
    /* Attributes */
    private List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
    private TextView chatText;
    private Context context;

    @Override
    public void add(ChatMessage chatMessage) {
        chatMessageList.add(chatMessage);
        super.add(chatMessage);
    }

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public ChatMessage getItem(int index) {
        return this.chatMessageList.get(index);
    }


    /* Get a View that displays the data at the specified position in the data set. In this case a chatMsg
    *  LayoutInflater instantiates a layout XML file into its corresponding View */
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage chatMessage = getItem(position);
        View chatMsgView;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (chatMessage.isUserCharMsg) {
            chatMsgView = inflater.inflate(R.layout.right_msg_chat, parent, false);
        }else{
            chatMsgView = inflater.inflate(R.layout.left_msg_chat, parent, false);
        }
        chatText = (TextView) chatMsgView.findViewById(R.id.msgr);
        chatText.setText(chatMessage.message);
        return chatMsgView;
    }
}
