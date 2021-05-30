package com.example.diet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class Diets extends AppCompatActivity {
    private CardView plan1,plan2,plan3,plan4,plan5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_diets);

        plan1=(CardView)findViewById(R.id.planone);
        plan1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Diets.this,Planone.class);
                startActivity(intent);
            }
        });

        plan2=(CardView)findViewById(R.id.plantwo);
        plan2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Diets.this,Plantwo.class);
                startActivity(intent);
            }
        });

        plan3=(CardView)findViewById(R.id.planthree);
        plan3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Diets.this,Planthree.class);
                startActivity(intent);
            }
        });

        plan4=(CardView)findViewById(R.id.planfour);
        plan4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Diets.this,Planfour.class);
                startActivity(intent);
            }
        });

        plan5=(CardView)findViewById(R.id.planfive);
        plan5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Diets.this,Planfive.class);
                startActivity(intent);
            }
        });
    }
}
