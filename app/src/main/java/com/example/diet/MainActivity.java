package com.example.diet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    ProgressBar pd;
    private TextView regtext,forgotpwdtext;
    private Button LoginButton;
    private EditText UserEmail,UserPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        UserEmail=(EditText)findViewById(R.id.usremail);
        UserPassword=(EditText)findViewById(R.id.usrpassword);

        forgotpwdtext=findViewById(R.id.textforgotpwd);
        forgotpwdtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showpasswordrecoverydialog();
            }
        });

        LoginButton=(Button)findViewById(R.id.loginbtn);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserLogin();
            }
        });


        regtext=(TextView)findViewById(R.id.regtext);

        regtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,Signup.class);
                startActivity(intent);
                finish();
                Toast.makeText(MainActivity.this,"You are about to register",Toast.LENGTH_LONG).show();
            }
        });




    }

    private void showpasswordrecoverydialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");

        LinearLayout linearLayout=new LinearLayout(this);
        final EditText emailforgot=new EditText(this);
        emailforgot.setHint("Enter the registered Email");
        emailforgot.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailforgot.setMinEms(30);

        linearLayout.addView(emailforgot);
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);

        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String Email= emailforgot.getText().toString().trim();
                beginrecovery(Email);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void beginrecovery(String email) {
       Toast.makeText(MainActivity.this,"Sending Email",Toast.LENGTH_LONG).show();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(MainActivity.this,"Email sent",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(MainActivity.this,"Failed",Toast.LENGTH_LONG).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            SendUserToMainActivity();
      }
    }

    private void SendUserToMainActivity() {

        Intent mainIntent = new Intent(MainActivity.this,Dashboard.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void UserLogin() {


        final String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "E-mail is Mandatory...", Toast.LENGTH_SHORT).show();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            Toast.makeText(MainActivity.this, "Incorrect E-mail", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Pass is Mandatory...", Toast.LENGTH_SHORT).show();
        }
        else

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (task.isSuccessful())
                    {
                        SendUserToMainActivity();
                        Toast.makeText(MainActivity.this, "Logged In Successfully...", Toast.LENGTH_SHORT).show();

                    }
                    else
                    {
                        String message = task.getException().getMessage();
                        Toast.makeText(MainActivity.this, "Error Occurred:" + message, Toast.LENGTH_SHORT).show();

                    }
                }
            });



    }
}
