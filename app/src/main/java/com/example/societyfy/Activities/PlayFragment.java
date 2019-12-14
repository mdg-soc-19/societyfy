package com.example.societyfy.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.societyfy.Activities.Adapters.ChatsAdapter;
import com.example.societyfy.Activities.models.Chat;
import com.example.societyfy.Activities.models.UserRepo;
import com.example.societyfy.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PlayFragment extends Fragment {


    private static final String CURRENT_USER_KEY = "CURRENT_USER_KEY";
    private static final String CURRENT_USER_NAME = "CURRENT_USER_NAME";
    private static final String CURRENT_USER_IMAGE = "CURRENT_USER_IMAGE";

    private RecyclerView chats;
    private ChatsAdapter adapter;

    private String userId = "";
    private String username="";
    private String userImage="";

    private UserRepo userRepo;

    private EditText message;
    private ImageButton send;
    View v;

    int contextMenuIndexClicked;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_play, container, false);


        userRepo = new UserRepo(FirebaseFirestore.getInstance());




        userId = getCurrentUserKey();
        username=getCurrentUserName();
        userImage=getCurrentUserImage();

        initUI();
        showChatMessages();



        return v;
    }

    private String getCurrentUserKey() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        return preferences.getString(CURRENT_USER_KEY, "");
    }

    private String getCurrentUserName() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        return preferences.getString(CURRENT_USER_NAME, "");
    }

    private String getCurrentUserImage() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        return preferences.getString(CURRENT_USER_IMAGE, "");
    }


    private void initUI() {
        message = v.findViewById(R.id.message_text);
        send = v.findViewById(R.id.send_message);
        chats = v.findViewById(R.id.chats);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setReverseLayout(true);
        chats.setLayoutManager(manager);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (message.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(),
                            getString(R.string.error_empty_message),
                            Toast.LENGTH_SHORT
                    ).show();
                } else {
                    addMessageToChatRoom();
                    showChatMessages();
                }
            }
        });
    }


    private void addMessageToChatRoom() {
        String chatMessage = message.getText().toString();
        message.setText("");
        send.setEnabled(false);
        userRepo.addMessageToChatRoom(
                "Play",
                userId,
                userImage,
                username,
                chatMessage,
                new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        send.setEnabled(true);
                    }
                }
        );
    }

    private void showChatMessages() {
        userRepo.getChats("Play", new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot snapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("ChatRoomActivity", "Listen failed.", e);
                    return;
                }

                List<Chat> messages = new ArrayList<>();
                for (QueryDocumentSnapshot doc : snapshots) {
                    messages.add(
                            new Chat(
                                    doc.getId(),
                                    doc.getString("chat_room_name"),
                                    doc.getString("sender_id"),
                                    doc.getString("sender_image"),
                                    doc.getString("sender name"),
                                    doc.getString("message"),
                                    doc.getLong("sent")
                            )
                    );
                }

                adapter = new ChatsAdapter(messages,getContext(), userId);
                chats.setAdapter(adapter);
            }
        });
    }
}
