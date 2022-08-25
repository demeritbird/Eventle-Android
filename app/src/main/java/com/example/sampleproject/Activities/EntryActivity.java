package com.example.sampleproject.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.Toast;

import com.example.sampleproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EntryActivity extends AppCompatActivity {

    //// FIREBASE ////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrypage);

        //// Components ////
        // TODO: change to recyclerview?
        Button entryMemberOne = findViewById(R.id.member1_entry);
        entryMemberOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = new TranslateAnimation(0, 0, 0, 10);
                animation.setDuration(200);
                entryMemberOne.startAnimation(animation);
                Intent intentSub = new Intent(EntryActivity.this, ApplicationActivity.class);
                startActivity(intentSub);
            }
        });
        Button entryMemberTwo = findViewById(R.id.member2_entry);
        entryMemberTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(EntryActivity.this, "did something", Toast.LENGTH_LONG).show();
                System.out.println("not just me");
                FirebaseDatabase database = FirebaseDatabase.getInstance(getResources().getString(R.string.firebase_link));
                DatabaseReference myRef = database.getReference("message");
                myRef.setValue("THIS helplheplhpe WORK LA too, World!");
            }
        });

    }
}