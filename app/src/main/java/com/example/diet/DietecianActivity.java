package com.example.diet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class DietecianActivity extends AppCompatActivity
{

    FirebaseAuth mAuth;
    DatabaseReference DietRef;
    String currentUserId;
    RecyclerView dieticianList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_dietecian);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        DietRef = FirebaseDatabase.getInstance().getReference().child("Dietician");

        dieticianList = (RecyclerView) findViewById(R.id.dieticianRecList);
        dieticianList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        dieticianList.setLayoutManager(linearLayoutManager);


        //RECYCLER VIEW FOR Dietecians UPDATE(CLOSE)
        startListen();

    }

    @Override
    protected void onStart() {
        super.onStart();
        startListen();
    }

    private void startListen()
    {
        Query query = FirebaseDatabase.getInstance().getReference().child("Dietician").limitToLast(50);
        FirebaseRecyclerOptions<Dietician> options = new FirebaseRecyclerOptions.Builder<Dietician>().setQuery(query, Dietician.class).build();
        FirebaseRecyclerAdapter<Dietician, DietViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Dietician, DietViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull DietViewHolder holder, final int position, @NonNull Dietician model)
            {
                //final String PostKey = getRef(position).getKey();

                holder.setAddress(model.getAddress());
                holder.setNamee(model.getNamee());
                //holder.setCityy(model.getCityy());
                holder.setEmaill(model.getEmaill());
                holder.setPhonee(model.getPhonee());
            }

            @NonNull
            @Override
            public DietViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ditecian_layout,parent,false);
                return new DietViewHolder(view);
            }
        };
        dieticianList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    /*And this is the static class*/
    public static class DietViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        public DietViewHolder(@NonNull View itemView)
        {
            super(itemView);
            mView = itemView;
        }

        public void setNamee(String name)
        {
            TextView username = (TextView) mView.findViewById(R.id.dietName);
            username.setText(name);
        }

        public void setAddress(String address)
        {
            TextView addresdoonor = (TextView) mView.findViewById(R.id.dietAdd);
            addresdoonor.setText(address);
        }

        public void setEmaill(String email)
        {
            TextView addresdoonor = (TextView) mView.findViewById(R.id.Email);
            addresdoonor.setText(email);
        }

        public void setPhonee(String phone)
        {
            TextView dietcity = (TextView) mView.findViewById(R.id.contact);
            dietcity.setText(phone);
        }
    }
}
