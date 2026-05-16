package com.acksha.healthassistant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class ChatAdapter extends ArrayAdapter<ChatMessage> {
    private final LayoutInflater inflater;

    public ChatAdapter(Context context, List<ChatMessage> items) {
        super(context, 0, items);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage item = getItem(position);
        if (item == null) return convertView;

        int layout = item.isUser() ? R.layout.chat_user : R.layout.chat_bot;
        convertView = inflater.inflate(layout, parent, false);

        TextView tv = convertView.findViewById(R.id.messageText);
        tv.setText(item.getMessage());

        return convertView;
    }
}