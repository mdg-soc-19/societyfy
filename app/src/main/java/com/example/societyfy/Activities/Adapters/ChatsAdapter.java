package com.example.societyfy.Activities.Adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.societyfy.Activities.models.Chat;
import com.example.societyfy.R;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.opencensus.internal.Utils;

public class ChatsAdapter extends RecyclerView.Adapter {
    private static final int SENT = 1;
    private static final int RECEIVED = 2;

    private Context mContext;
    private List<Chat> chats;
    private String userId;

    public ChatsAdapter(List<Chat> chats, Context mContext , String userId) {
        this.chats = chats;
        this.mContext = mContext;
        this.userId = userId;
    }

    @Override
    public  RecyclerView.ViewHolder  onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_sent_item, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_recieved_item, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Chat chat = chats.get(position);

        switch (holder.getItemViewType()) {
            case SENT:
                ((SentMessageHolder) holder).bind(chat);
                break;
            case RECEIVED:
                ((ReceivedMessageHolder) holder).bind(chat);
        }
    }



    @Override
    public int getItemViewType(int position) {
        if (chats.get(position).getSenderId().contentEquals(userId)) {
            return SENT;
        } else {
            return RECEIVED;
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, dateText;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            dateText = itemView.findViewById(R.id.text_message_date);
        }

        void bind(Chat chat) {
            messageText.setText(chat.getMessage());
            dateText.setText(DateUtils.formatDateTime(mContext,chat.getSent(),DateUtils.FORMAT_NO_YEAR));

            // Format the stored timestamp into a readable String using method.
            timeText.setText(DateUtils.formatDateTime(mContext,chat.getSent(),DateUtils.FORMAT_NO_YEAR|DateUtils.FORMAT_SHOW_TIME));
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText, dateText;
        CircleImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            dateText = itemView.findViewById(R.id.text_message_date);
            nameText = (TextView) itemView.findViewById(R.id.text_message_name);
            profileImage = itemView.findViewById(R.id.image_message_profile);
        }

        void bind(Chat chat) {
            messageText.setText(chat.getMessage());

            // Format the stored timestamp into a readable String using method.
            timeText.setText(DateUtils.formatDateTime(mContext,chat.getSent(),DateUtils.FORMAT_SHOW_TIME ));
            dateText.setText(DateUtils.formatDateTime(mContext,chat.getSent(),DateUtils.FORMAT_NO_YEAR));

            nameText.setText(chat.getSenderName());

            // Insert the profile image from the URL into the ImageView.
            Glide.with(mContext).load(chat.getUserImage()).into(profileImage);
        }
    }

}
