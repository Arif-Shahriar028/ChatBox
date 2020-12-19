package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    private EditText setProfileName, setStatus;
    private Button nextButton;
    private ImageView camera;

    private String currentUId;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();

        setProfileName = (EditText) findViewById(R.id.set_profile_name_id);
        setStatus = (EditText) findViewById(R.id.set_profile_status_id);

        nextButton = (Button) findViewById(R.id.nextButtonId);

        camera = (ImageView) findViewById(R.id.editProfileImageId);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettings();
            }
        });

        camera.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1 );
            }
        });
    }

    private void updateSettings(){
        String name = setProfileName.getText().toString();
        String status = setStatus.getText().toString();

        if(TextUtils.isEmpty(name)){
            toast("Please write your user name first...");
        }
        if(TextUtils.isEmpty(status)){
            toast("Please write your status");
        }
        else{
            ProgressDialog pd = new ProgressDialog(SettingsActivity.this);
            pd.setMessage("loading");
            pd.show();

            HashMap<String , String> profileMap = new HashMap<>();
            profileMap.put("uid", currentUId);
            profileMap.put("name", name);
            profileMap.put("status", status);

            rootRef.child("UserProfile").child(currentUId).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                toast("Profile updated Successfully");
                                pd.dismiss();
                                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                            else{
                                String message = task.getException().toString();
                                toast(message);
                            }
                        }
                    });
        }
    }

    protected void onStart() {

        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        retrieveData();
    }

    private void retrieveData(){
        ProgressDialog pd = new ProgressDialog(SettingsActivity.this);
        pd.setMessage("loading");
        pd.show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("UserProfile").child(currentUId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String name = snapshot.child("name").getValue().toString();
                    String status = snapshot.child("status").getValue().toString();

                    setProfileName.setText(name);
                    setStatus.setText(status);

                    pd.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void toast(String s){
        Toast.makeText(SettingsActivity.this, s, Toast.LENGTH_SHORT).show();
    }
}