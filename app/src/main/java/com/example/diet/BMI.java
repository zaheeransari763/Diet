package com.example.diet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class BMI extends AppCompatActivity {
    private EditText usrheight,usrweight;
    private Button calculatebmibtn;
    private TextView textbmi;
    String calculation, BMIresult;
    FirebaseAuth mAuth;
    DatabaseReference dietRef;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_bmi);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        dietRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        dietRef.keepSynced(true);

        usrheight=(EditText)findViewById(R.id.height);
        usrweight=(EditText)findViewById(R.id.weight);
        textbmi=(TextView)findViewById(R.id.bmiresult);
        calculatebmibtn=(Button)findViewById(R.id.bmibtn);

        calculatebmibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s1=usrweight.getText().toString();
                String s2=usrheight.getText().toString();

                float weightvalue=Float.parseFloat(s1);
                float heightvalue=Float.parseFloat(s2)/100;

                float bmivalue=weightvalue/(heightvalue*heightvalue);

                if(bmivalue < 16){
                    BMIresult = "Severely Under Weight";
                    plan1();
                }else if(bmivalue < 18.5){
                    BMIresult = "Under Weight";
                    plan2();
                }else if(bmivalue >= 18.5 && bmivalue <= 24.9){

                    BMIresult = "Normal Weight";
                    plan3();

                }else if (bmivalue >= 25 && bmivalue <= 29.9){
                    BMIresult = "Overweight";
                    plan4();
                }else{
                    BMIresult = "Obese";
                    plan5();
                }
                calculation = "   "+"Result:" +" " + bmivalue + " " +" "+ BMIresult;
                textbmi.setText(calculation);

                final String bmidatabase=String.valueOf(bmivalue);

                HashMap<String, Object> dietMap = new HashMap();
                dietMap.put("BMI",bmidatabase);
                dietRef.updateChildren(dietMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            
                        }
                        else
                        {
                            String msg = task.getException().toString();
                            Toast.makeText(BMI.this, "Error:" + msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void plan5() {
        Intent intent=new Intent(BMI.this,Diets.class);
        startActivity(intent);
        Toast.makeText(this,"You are obessed Please Follow Plan5 And BMI Updated",Toast.LENGTH_LONG).show();
    }

    private void plan2() {
        Intent intent=new Intent(BMI.this,Diets.class);
        startActivity(intent);
        Toast.makeText(this,"You need more weight Please Follow Plan2  And BMI Updated",Toast.LENGTH_LONG).show();
    }

    private void plan1() {
        Intent intent=new Intent(BMI.this,Diets.class);
        startActivity(intent);
        Toast.makeText(this,"Your weight too low Please Follow Plan1  And BMI Updated",Toast.LENGTH_LONG).show();
    }

    private void plan3() {

        Intent intent=new Intent(BMI.this,Diets.class);
        startActivity(intent);
        Toast.makeText(this,"You are fit to go Please Follow Plan3  And BMI Updated",Toast.LENGTH_LONG).show();
    }

    private void plan4() {
        Intent intent=new Intent(BMI.this,Diets.class);
        startActivity(intent);
        Toast.makeText(this,"You need to lose some weight Please Follow Plan4  And BMI Updated",Toast.LENGTH_LONG).show();
    }
}
