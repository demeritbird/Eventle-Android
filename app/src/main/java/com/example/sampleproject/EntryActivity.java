package com.example.sampleproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EntryActivity extends AppCompatActivity {

    //// FIREBASE ////
    private final String TAG = "test tag message here";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrypage);

        // Write a message to the database
        // TODO: find out how to traverse different collection and keyvalue pairs.
        FirebaseDatabase database = FirebaseDatabase.getInstance(getResources().getString(R.string.firebase_link));
        DatabaseReference myRef = database.getReference("message");
        myRef.setValue("THISsdfsdfsdfsdfsdfsdfsdf LA too, World!");

        DatabaseReference database1 = FirebaseDatabase.getInstance(getResources().getString(R.string.firebase_link)).getReference();
        DatabaseReference myRef1 = database1.child("events");
        myRef1.child("person1").child("firstname").setValue("james");


        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


        //// Components ////
        //  button & textview
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


/*        //// Fragment ////
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        HomeFragment homeFragment = new HomeFragment();
        NotificationFragment notificationFragment = new NotificationFragment();
        SettingsFragment settingsFragment = new SettingsFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                        return true;
                    case R.id.notification:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, notificationFragment).commit();
                        return true;
                    case R.id.settings:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, settingsFragment).commit();
                        return true;
                }
                return false;
            }
        });*/
    }
}