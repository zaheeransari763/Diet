package com.example.diet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Signup extends AppCompatActivity {

    private TextView logintext;
    private EditText registername,registeremail,registerpass,registerphone;
    private Button userReg;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_signup);

        ProgressDialog loadingbar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        registername=(EditText)findViewById(R.id.regname);
        registeremail=(EditText)findViewById(R.id.regemail);
        registerpass=(EditText)findViewById(R.id.regpassword);
        registerphone=(EditText)findViewById(R.id.regcontact);

        userReg=(Button)findViewById(R.id.regbtn);

        userReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userreg();
            }
        });

        logintext=(TextView)findViewById(R.id.logintext);
        logintext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Signup.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void userreg() {
        final String bmi="BMI";
        final String name = registername.getText().toString();
        final String email = registeremail.getText().toString();
        final String password = registerpass.getText().toString();
        final String phone=registerphone.getText().toString();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Name is Mandatory...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "E-mail is empty...", Toast.LENGTH_SHORT).show();
        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            Toast.makeText(Signup.this, "Incorrect E-mail", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Contact is empty...", Toast.LENGTH_SHORT).show();
        } else if (phone.length() != 10) {
            Toast.makeText(this, "Invalid Contact...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Phone Number be empty...", Toast.LENGTH_SHORT).show();
        }
        else
        {


            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        String ClientID = firebaseUser.getUid();

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(ClientID);

                        HashMap<String, String> ClientMap = new HashMap();
                        ClientMap.put("Fullname", name);
                        ClientMap.put("Email",email);
                        ClientMap.put("Contact",phone);
                        ClientMap.put("Password",password);
                        ClientMap.put("BMI",bmi);
                        ClientMap.put("image","default");


                        reference.setValue(ClientMap).addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    Intent DashMainIntent = new Intent(Signup.this,Dashboard.class);
                                    DashMainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(DashMainIntent);
                                    finish();
                                }
                            }
                        });

                        Toast.makeText(Signup.this, "Authenticated Successfully...", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        String message = task.getException().getMessage();
                        Toast.makeText(Signup.this, "Error Occurred ;" + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }
}
