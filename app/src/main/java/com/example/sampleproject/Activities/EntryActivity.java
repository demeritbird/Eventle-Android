package com.example.sampleproject.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.Toast;

import com.example.sampleproject.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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