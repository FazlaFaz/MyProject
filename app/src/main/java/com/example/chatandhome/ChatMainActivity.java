package com.example.chatandhome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ChatMainActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessAdapter myTabsAccessorAdaptor;

    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private String currentUserID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main);

        mAuth=FirebaseAuth.getInstance();

        RootRef= FirebaseDatabase.getInstance().getReference();

        mToolbar=(Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("fazla");


        myViewPager=(ViewPager) findViewById(R.id.main_tabs_pager);
        myTabsAccessorAdaptor=new TabsAccessAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccessorAdaptor);


        myTabLayout=(TabLayout) findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser==null)
        {
            SendUserToLoginActivity();
            //  SendUserToSettingsActivity();


        }
        else
        {
            updateUserStatus("online");
            VerifyUserExistance();
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser=mAuth.getCurrentUser();

        if(currentUser !=null)
        {
            updateUserStatus("offline");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser !=null)
        {
            updateUserStatus("offline");
        }
    }

    private void VerifyUserExistance()
    {
        String currentUserID=mAuth.getCurrentUser().getUid();

        RootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if((dataSnapshot.child("name").exists()))
                {
                    // Toast.makeText(MainActivity.this,"Welcome",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    SendUserToSettingsActivity();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu,menu);

        return  true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.main_logout_option)
        {
            updateUserStatus("offline");
            mAuth.signOut();
            SendUserToLoginActivity();

        }
        if(item.getItemId()==R.id.main_settings_option)
        {
            SendUserToSettingsActivity();
        }
        if(item.getItemId()==R.id.main_find_friends_option)
        {
            SendUserToFindFriendsActivity();
        }

        return true;
    }

    private void SendUserToLoginActivity()
    {
        Intent loginIntent=new Intent(ChatMainActivity.this,LoginChatActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }



    private void SendUserToSettingsActivity()
    {
        Intent settingsIntent=new Intent(ChatMainActivity.this,SettingsActivity.class);
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingsIntent);
        finish();
    }

    private void SendUserToFindFriendsActivity()
    {
        Intent findFriendsIntent=new Intent(ChatMainActivity.this,FindFriendsActivity.class);
        startActivity(findFriendsIntent);
    }
    private void updateUserStatus(String state)
    {
        String saveCurrentTime,saveCurrentDate;
        Calendar calendar=Calendar.getInstance();

        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("hh:mm a");
        saveCurrentTime=currentTime.format(calendar.getTime());


        HashMap<String,Object> onlineStateMap=new HashMap<>();
        onlineStateMap.put("time",saveCurrentTime);
        onlineStateMap.put("date",saveCurrentDate);
        onlineStateMap.put("state",state);

        currentUserID=mAuth.getCurrentUser().getUid()
        ;

        RootRef.child("Users").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);

    }
}
