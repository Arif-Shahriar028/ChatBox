package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button signUp, signIn;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private EditText email, password;
    private TextView forgetPassword;
    private String currentUserId;
    private DatabaseReference RootRef;
    private FirebaseDatabase database;
    LinearLayout loginLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.emailId);
        password = findViewById(R.id.passwordId);
        signIn = findViewById(R.id.signInId);
        progressBar = findViewById(R.id.progressBarId2);
        signUp = (Button) findViewById(R.id.signUpId);
        loginLayout = (LinearLayout) findViewById(R.id.loginLayoutId);
        forgetPassword = (TextView) findViewById(R.id.forgetPasswordId);

        password.setTransformationMethod(new PasswordTransformationMethod()); // to hide password

        signUp.setOnClickListener(this);
        signIn.setOnClickListener(this);
        forgetPassword.setOnClickListener(this);
    }



    @Override
    public void onClick(View view) {

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(loginLayout.getWindowToken(), 0); // to hide keyboard after button click

        if(view.getId() == R.id.signInId)
        {
            String Email =  email.getText().toString();
            String Password = password.getText().toString();

            if(Email.matches(""))  // check whether email is empty or not  (if this condition not written , the app will crash for submit empty email field)
            {
                Toast.makeText(LoginActivity.this, "Email cant be empty", Toast.LENGTH_SHORT).show();
            }
            else if(Password.matches("") || Password.length() <6) // check password validation
            {
                Toast.makeText(LoginActivity.this, "Password length should be at least 6", Toast.LENGTH_SHORT).show();
            }
            else
            {
                progressBar.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if(task.isSuccessful()){   // if user is authenticated
                            //finish();  // to end this activity
                            if(mAuth.getCurrentUser().isEmailVerified())
                            {
                                //updateUID();
                                //updateUID uid = new updateUID();
                                verifyUserExistance();
                               /* Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);*/
                                //finish();
                            }
                            else{
                                Toast.makeText(LoginActivity.this, "Please verify your email", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{  // if user is not authenticated

                            String message = task.getException().getMessage();  // get error message
                            Toast.makeText(getApplicationContext(), "Error occurred : "+message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

        else if(view.getId() == R.id.signUpId)
        {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        }
        else if(view.getId() == R.id.forgetPasswordId)
        {
            Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
            //
            startActivity(intent);
        }

    }


    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null)   // if user not signed in
        {
            //verifyUserExistance();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void updateUID(){
        currentUserId = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://chatbox-df266-default-rtdb.firebaseio.com/");
        RootRef.child("UserProfile").child(currentUserId).child("uid").setValue(currentUserId);
        RootRef.child("UserProfile").child(currentUserId).child("name").setValue("User Name");
        RootRef.child("UserProfile").child(currentUserId).child("status").setValue("Hey! I am available now.");
    }

    private void verifyUserExistance(){
        String currentUId = mAuth.getCurrentUser().getUid();

        RootRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://chatbox-df266-default-rtdb.firebaseio.com/");
        RootRef.child("UserProfile").child(currentUId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if((snapshot.child("name").exists())){
                    Toast.makeText(LoginActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                else{
                    updateUID();
                    Intent intent = new Intent(LoginActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
