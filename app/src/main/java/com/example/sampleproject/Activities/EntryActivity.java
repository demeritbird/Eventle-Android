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

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class EntryActivity extends AppCompatActivity implements View.OnClickListener {
    private Button entryMemberOne;

    //// FIREBASE ////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrypage);


        //// Components ////
        entryMemberOne = findViewById(R.id.btn_member1_entry);
        entryMemberOne.setOnClickListener(this);
        Button entryMemberTwo = findViewById(R.id.btn_member2_entry);
        entryMemberTwo.setOnClickListener(this);

        DatabaseReference firebase = FirebaseDatabase.getInstance(getResources().getString(R.string.firebase_link)).getReference().child("members");
        DatabaseReference memberOneFirebase = firebase.child("member1").child("name");
        DatabaseReference memberTwoFirebase = firebase.child("member2").child("name");
        memberone(memberOneFirebase, entryMemberOne);
        memberone(memberTwoFirebase, entryMemberTwo);


    }

    private void memberone(DatabaseReference firebase, Button memberButton) {

        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String yes = dataSnapshot.getValue().toString();
                memberButton.setText(yes);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("sad", "Failed to read value.", error.toException());
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_member2_entry) {
            checkFirebase();
        } else if (view.getId() == R.id.btn_member1_entry) {
            openApplication();
        }
    }

    private void openApplication() {
        Animation animation = new TranslateAnimation(0, 0, 0, 10);
        animation.setDuration(200);
        entryMemberOne.startAnimation(animation);
        Intent intentSub = new Intent(EntryActivity.this, ApplicationActivity.class);
        intentSub.putExtra("username", entryMemberOne.getText());
        intentSub.putExtra("id","1");
        startActivity(intentSub);
    }

    private void checkFirebase() {
        Toast.makeText(EntryActivity.this, "did something", Toast.LENGTH_LONG).show();
        System.out.println("not just me");
        FirebaseDatabase database = FirebaseDatabase.getInstance(getResources().getString(R.string.firebase_link));
        DatabaseReference myRef = database.getReference("message");
        myRef.setValue("no u, World!");
    }
}