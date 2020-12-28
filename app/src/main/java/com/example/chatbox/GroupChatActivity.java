package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.icu.text.Edits;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Group;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView messageText;
    private EditText writeMessage;
    private ImageButton sendMessageButton;
    private ScrollView scrollView;
    private String currentGroupName, currentUserId , currentDate, currentTime;
    private FirebaseAuth mAuth;
    private DatabaseReference groupRef, userRef, groupMessageKeyRef;
    String currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        currentGroupName = getIntent().getExtras().get("groupName").toString();

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);
        userRef = FirebaseDatabase.getInstance().getReference().child("UserProfile");

        toolbar = (Toolbar) findViewById(R.id.groupChatAppBarId);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(currentGroupName);

        initialize();
        getUserInfo();

        //scrollView.fullScroll(ScrollView.FOCUS_DOWN);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadMessage();
                writeMessage.setText("");

                scrollView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                },1000);
            }
        });

    }

    private void initialize(){
        messageText = (TextView) findViewById(R.id.messageTextId);
        writeMessage = (EditText) findViewById(R.id.writeMessageId);
        sendMessageButton = (ImageButton) findViewById(R.id.sendButton);
        scrollView = (ScrollView) findViewById(R.id.scrollViewId);

    }

    private void getUserInfo(){
        userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    currentUserName = snapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void uploadMessage(){
        String message = writeMessage.getText().toString();
        String messageKey = groupRef.push().getKey();

        if(TextUtils.isEmpty(message)){
            Toast.makeText(GroupChatActivity.this, "Write message first", Toast.LENGTH_SHORT).show();
        }
        else{
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = currentDateFormat.format(calendar.getTime());

            Calendar calendarForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(calendarForTime.getTime());

            HashMap<String, Object> groupMessageKey = new HashMap<>();
            groupRef.updateChildren(groupMessageKey);

            groupMessageKeyRef = groupRef.child(messageKey);

            HashMap<String, Object> messageInfoMap = new HashMap<>();

            messageInfoMap.put("message", message);
            messageInfoMap.put("date", currentDate);
            messageInfoMap.put("time", currentTime);
            messageInfoMap.put("userName", currentUserName);

            groupMessageKeyRef.updateChildren(messageInfoMap);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        groupRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    retrieveMessage(snapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    retrieveMessage(snapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void retrieveMessage(DataSnapshot snapshot){
        Iterator iterator = snapshot.getChildren().iterator();

        while(iterator.hasNext()){
            String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatName = (String) ((DataSnapshot)iterator.next()).getValue();

            messageText.append(chatName + ":\n" + chatMessage +"\n"+ "("+chatTime + ", "+ chatDate +")"+ "\n\n\n" );

            scrollView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            },1000);

        }

    }
}