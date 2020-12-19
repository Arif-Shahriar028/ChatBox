package com.example.chatbox;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

public class updateUID {

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private StorageReference userPicRef;

    private DatabaseReference RootRef;
    String currentUserId, downloadImageUrl;

    public updateUID(){
        currentUserId = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.child("Users").child(currentUserId).setValue("");
    }
}
