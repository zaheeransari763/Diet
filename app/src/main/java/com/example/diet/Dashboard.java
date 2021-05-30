package com.example.diet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Dashboard extends AppCompatActivity {

    private FirebaseUser firebaseUser;
    DatabaseReference reference;
    private TextView Usrname;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar mToolbar;
    private TextView navProfileName;
    CircleImageView imageV,imageVHead;

    private FirebaseAuth mAuth;
    private DatabaseReference UserRef;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_dashboard);

        mAuth = FirebaseAuth.getInstance();

        Usrname=(TextView)findViewById(R.id.start_name);
        imageV = (CircleImageView) findViewById(R.id.dashProfile);

        currentUserId = mAuth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    String userfullname = dataSnapshot.child("Fullname").getValue().toString();

                    Usrname.setText(userfullname);
                    final String image = dataSnapshot.child("image").getValue().toString();
                    if(!image.equals("default"))
                    {
                        Picasso.with(Dashboard.this).load(image).placeholder(R.drawable.profileimg).into(imageV);
                        Picasso.with(Dashboard.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profileimg).into(imageV, new Callback()
                        {
                            @Override
                            public void onSuccess()
                            {

                            }

                            @Override
                            public void onError()
                            {
                                Picasso.with(Dashboard.this).load(image).placeholder(R.drawable.profileimg).into(imageV);
                            }
                        });
                    }
                  }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        TextView t = (TextView) findViewById(R.id.quotetext);
        Typeface myCustomFont= Typeface.createFromAsset(getAssets(),"fonts/KaushanScript-Regular.otf");
        t.setTypeface(myCustomFont);//font style

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");

        drawerLayout =(DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(Dashboard.this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View navView = navigationView.inflateHeaderView(R.layout.header);
        navProfileName = (TextView) navView.findViewById(R.id.header_name);
        imageVHead = (CircleImageView) navView.findViewById(R.id.navHeaderImage);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("Fullname").getValue().toString();
                navProfileName.setText(username);
                final String imageHead = dataSnapshot.child("image").getValue().toString();
                if(!imageHead.equals("default"))
                {
                    Picasso.with(Dashboard.this).load(imageHead).placeholder(R.drawable.profileimg).into(imageVHead);
                    Picasso.with(Dashboard.this).load(imageHead).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profileimg).into(imageVHead, new Callback()
                    {
                        @Override
                        public void onSuccess()
                        {

                        }

                        @Override
                        public void onError()
                        {
                            Picasso.with(Dashboard.this).load(imageHead).placeholder(R.drawable.profileimg).into(imageVHead);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                UserMenuSelector(item);
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout= (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    private void SendUserToLoginActivity()
    {
        Intent loginIntent = new Intent(this,MainActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item) {


        switch ( item.getItemId())
        {
            case R.id.profile:
                SendUserToProfile();
                break;
            case R.id.diets:
                SendUserToDiets();
                break;
            case R.id.bmi:
                SendUserToBmi();
                break;

            case R.id.exercise:
                SendUserToExercise();
                break;
            case R.id.dietecian:
                SendUserToDietecian();
                break;
            case R.id.nutrition:
                SendUserToNutrition();
                break;
            case R.id.logout:
                mAuth.signOut();
                SendUserToLoginActivity();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void SendUserToNutrition() {

        Intent homeIntent = new Intent(this,Nutrition.class);
        startActivity(homeIntent);
    }

    private void SendUserToDietecian() {
        Intent homeIntent = new Intent(this, DietecianActivity.class);
        startActivity(homeIntent);
    }

    private void SendUserToExercise() {
        Intent homeIntent = new Intent(this,Exercise.class);
        startActivity(homeIntent);
    }

    private void SendUserToBmi() {
        Intent homeIntent = new Intent(this,BMI.class);
        startActivity(homeIntent);
    }

    private void SendUserToDiets() {
        Intent homeIntent = new Intent(this,Diets.class);
        startActivity(homeIntent);
    }

    private void SendUserToProfile() {
        Intent homeIntent = new Intent(this,Profile.class);
        startActivity(homeIntent);
    }
}
