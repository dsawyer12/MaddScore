package com.example.dsawyer.maddscore.Social;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dsawyer.maddscore.Objects.ActiveRequest;
import com.example.dsawyer.maddscore.Objects.Message;
import com.example.dsawyer.maddscore.Objects.Notification;
import com.example.dsawyer.maddscore.Objects.User;
import com.example.dsawyer.maddscore.R;
import com.example.dsawyer.maddscore.Utils.MessageListRecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TAG";

    private DatabaseReference ref;
    private String UID;
    private User currentUser, recipient;

    private CircleImageView recipient_img;
    private TextView recipient_name;
    private ImageView send_btn, back_btn;
    private EditText message_box;

    private ArrayList<Message> messages;
    private User[] recipients;
    private MessageListRecyclerView adapter;
    private RecyclerView recyclerView;
    private  LinearLayoutManager linearLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recipient_img = view.findViewById(R.id.recipient_img);
        recipient_name = view.findViewById(R.id.recipient_name);
        back_btn = view.findViewById(R.id.back_btn);
        send_btn = view.findViewById(R.id.send_btn);
        recyclerView = view.findViewById(R.id.recyclerView);
        message_box = view.findViewById(R.id.message_box);

        back_btn.setOnClickListener(this);
        send_btn.setOnClickListener(this);

        ref = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            ref.child("users").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        currentUser = dataSnapshot.getValue(User.class);
                        recipient = getUserFromBundle();
                        if (recipient != null) {
                            getMessageActivity();
                            recipient_name.setText(recipient.getName());
                            if (recipient.getPhotoUrl() != null)
                                Glide.with(getActivity()).load(recipient.getPhotoUrl()).into(recipient_img);
                            else
                                Glide.with(getActivity()).load(R.mipmap.default_profile_img).into(recipient_img);
                        }
                        else
                            ; // popBackStack. i cant do anything without the recipient
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void getMessageActivity() {
        messages = new ArrayList<>();
        recipients = new User[2];
        recipients[0] = currentUser;
        recipients[1] = recipient;

        String key;
        if (UID.compareTo(recipient.getUserID()) < 0)
            key = UID + recipient.getUserID();
        else
            key = recipient.getUserID() + UID;

        Query query = ref.child("messages").child(key);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    messages.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Message message = ds.getValue(Message.class);
                        if (message != null) {
                            if (message.getSender().equals(UID))
                                message.setType(Message.TYPE_ONE);
                            else
                                message.setType(Message.TYPE_TWO);
                            messages.add(message);
                        }
                    }
                    if (adapter != null)
                        adapter.notifyDataSetChanged();
                    else {
                        adapter = new MessageListRecyclerView(getActivity(), recipients, messages);
                        recyclerView.setAdapter(adapter);
                        linearLayoutManager = new LinearLayoutManager(getActivity());
                        linearLayoutManager.setStackFromEnd(true);
                        recyclerView.setLayoutManager(linearLayoutManager);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public User getUserFromBundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null)
            return bundle.getParcelable("user");
        else
            return null;
    }

    public void sendMessage() {
        final String key, messageID, messageBody;
        messageBody = message_box.getText().toString().trim();
        if (messageBody.isEmpty())
            return;
        else {
            long date = System.currentTimeMillis();

            Message message = new Message(UID, recipient.getUserID(), date, messageBody);

            if (UID.compareTo(recipient.getUserID()) < 0)
                key = UID + recipient.getUserID();
            else
                key = recipient.getUserID() + UID;

            messageID = ref.child("messages").child(key).push().getKey();

            if (messageID != null) {
                ref.child("messages").child(key).child(messageID).setValue(message)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    message_box.getText().clear();
                                    linearLayoutManager.scrollToPosition(adapter.getItemCount() - 1);
//                                    sendNotification(dateString, messageBody);
                                }
                            }
                        });
            }
        }
    }

    public void sendNotification(long date, String snippet) {

        final String key = ref.child("notifications").push().getKey();
        if (key != null) {
            Notification notification = new Notification(
                    key,
                    UID,
                    currentUser.getName(),
                    currentUser.getPhotoUrl(),
                    recipient.getUserID(),
                    Notification.MESSAGE,
                    date,
                    snippet);

            ref.child("notifications")
                    .child(recipient.getUserID())
                    .child(key)
                    .setValue(notification)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                Log.d(TAG, "onComplete: message notification sent");
                            else
                                Toast.makeText(getActivity(), "Failed to Send Request", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else
            Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case(R.id.back_btn):
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                break;

            case(R.id.send_btn):
                sendMessage();
                break;
        }
    }
}















