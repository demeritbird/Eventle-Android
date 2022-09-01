package com.example.sampleproject.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;

import com.example.sampleproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EntryActivity extends AppCompatActivity implements View.OnClickListener {
    private Button entryMemberOne;
    private Button entryMemberTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrypage);


        //// Components ////
        entryMemberOne = findViewById(R.id.btn_member1_entry);
        entryMemberOne.setOnClickListener(this);
        entryMemberTwo = findViewById(R.id.btn_member2_entry);
        entryMemberTwo.setOnClickListener(this);

        //// Update Member Name ////
        DatabaseReference firebaseMembers = FirebaseDatabase.getInstance(getResources().getString(R.string.firebase_link)).getReference().child("members");
        DatabaseReference memberOneFirebase = firebaseMembers.child("member1").child("name");
        updateMemberName(memberOneFirebase, entryMemberOne);
        DatabaseReference memberTwoFirebase = firebaseMembers.child("member2").child("name");
        updateMemberName(memberTwoFirebase, entryMemberTwo);


    }

    private void updateMemberName(DatabaseReference firebase, Button memberButton) {

        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String newMemberName = dataSnapshot.getValue().toString();
                memberButton.setText(newMemberName);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(String.valueOf(R.string.TAG_MESSAGE), "Failed to read value.", error.toException());
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_member2_entry) {
            openApplication(entryMemberTwo, "2", "1");
        } else if (view.getId() == R.id.btn_member1_entry) {
            openApplication(entryMemberOne, "1", "2");
        }
    }

    private void openApplication(Button memberButton, String id, String otherId) {
        Animation animation = new TranslateAnimation(0, 0, 0, 10);
        animation.setDuration(200);
        memberButton.startAnimation(animation);
        Intent intentSub = new Intent(EntryActivity.this, ApplicationActivity.class);
        intentSub.putExtra("username", memberButton.getText());
        intentSub.putExtra("id",id);
        intentSub.putExtra("otherId",otherId);
        startActivity(intentSub);
    }
}