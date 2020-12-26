package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private EditText phoneNumber, veriCode;
    private Button sendButton;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callBacks;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    private TabAccesstorAdapter mTabAccessorAdapter;
    String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // userIsLoggedIn();
        mAuth = FirebaseAuth.getInstance();

        toolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar); // add custom toolbar
        getSupportActionBar().setTitle("ChatBox");

        viewPager = (ViewPager) findViewById(R.id.viewPagerId);
        mTabAccessorAdapter = new TabAccesstorAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(mTabAccessorAdapter);

        tabLayout = (TabLayout) findViewById(R.id.main_tabLayout_id);
        tabLayout.setupWithViewPager(viewPager);

        /*
        */
    }

   /* protected void onStart() {

        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null)   // if user not signed in
        {
            sendUserToLoginActivity();
        }
        else
        {
            //verifyUserExistance();
        }

    }*/

    private void sendUserToLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_layout, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()== R.id.findFriendsId)
        {

        }

        if(item.getItemId()== R.id.createGroupId)
        {
            requestNewGroup();
        }

        if(item.getItemId()== R.id.settingsId)
        {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }

        if(item.getItemId()== R.id.logoutId)
        {
            mAuth.signOut();  // signing out the user
            Intent intent = new Intent(MainActivity.this, LoginActivity.class); // return to login activity
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void requestNewGroup(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder.setTitle("Enter group name :");
        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("Group name");
        builder.setView(groupNameField);

        builder.setPositiveButton("Creat", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = groupNameField.getText().toString();
                if(TextUtils.isEmpty(groupName)){
                    Toast.makeText(MainActivity.this, "Please write group name", Toast.LENGTH_SHORT).show();
                }
                else{
                    createNewGroup(groupName);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void createNewGroup(String groupName){
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Group created successully", Toast.LENGTH_SHORT).show();
                        }
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