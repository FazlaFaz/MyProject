package com.example.chatandhome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginChatActivity extends AppCompatActivity {
    private Button LoginButton,PhoneLoginButton;
    private EditText UserEmail,UserPassword;
    private TextView NeednewAccountlink,forgetpasswordlink;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    // private DatabaseReference UsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_chat);

        mAuth=FirebaseAuth.getInstance();
        //  UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");

        InitializeFields();

        NeednewAccountlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendUserToRegisterActivity();

            }
        });

        /*PhoneLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent phoneLoginIntent=new Intent(LoginActivity.this,PhoneLoginActivity.class);
                startActivity(phoneLoginIntent);

            }
        });*/


        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                AllowUserToLogin();

            }
        });
    }

    private void AllowUserToLogin() {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter Email...", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter Password...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Sign In");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();


            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                //String currentUserId=mAuth.getCurrentUser().getUid();
                                //  String deviceToken= FirebaseInstanceId.getInstance().getToken();
                               /* UsersRef.child(currentUserId).child("device_token")
                                       .setValue(deviceToken)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {

                                                }
                                            }
                                        });
                                         */
                                SendUserToMainActivity();
                                Toast.makeText(LoginChatActivity.this, "Logged in Successful...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                            }
                            else
                            {
                                String message=task.getException().toString();
                                Toast.makeText(LoginChatActivity.this,"Error :"+message,Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                            }
                        }
                    });
        }
    }

    private void InitializeFields()
    {
        LoginButton=(Button)findViewById(R.id.login_button);
        // PhoneLoginButton=(Button)findViewById(R.id.phone_login_button);
        UserEmail=(EditText) findViewById(R.id.login_email);
        UserPassword=(EditText) findViewById(R.id.login_password);
        NeednewAccountlink=(TextView) findViewById(R.id.need_new_account_link);
        // forgetpasswordlink=(TextView) findViewById(R.id.forget_password_link);
        loadingBar=new ProgressDialog(this);
    }


    private void SendUserToMainActivity()
    {
        Intent mainIntent=new Intent(LoginChatActivity.this,ChatMainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void SendUserToRegisterActivity()
    {
        Intent registerIntent=new Intent(LoginChatActivity.this,RegisterActivity.class);
        startActivity(registerIntent);
    }
}
