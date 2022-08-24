package com.example.sampleproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.material.bottomnavigation.BottomNavigationView;
public class ApplicationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    personAdaptar adapter; // Create Object of the Adapter class
    DatabaseReference mbase; // Create object of the Firebase Realtime Database


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);

        Bundle resultIntent = getIntent().getExtras();


        ///// Recycler View ////

        // Create a instance of the database and get its reference
        mbase = FirebaseDatabase.getInstance(getResources().getString(R.string.firebase_link)).getReference().child("events");

        recyclerView = findViewById(R.id.recycler1);

        // To display the Recycler view linearly
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // It is a class provide by the FirebaseUI to make a query in the database to fetch appropriate data
        FirebaseRecyclerOptions<person> options
                = new FirebaseRecyclerOptions.Builder<person>()
                .setQuery(mbase, person.class)
                .build();
        // Connecting object of required Adapter class to the Adapter class itself
        adapter = new personAdaptar(options);
        // Connecting Adapter class with the Recycler view*/
        recyclerView.setAdapter(adapter);

        /////

        Button addMember = findViewById(R.id.add_member);
        addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference database1 = FirebaseDatabase.getInstance(getResources().getString(R.string.firebase_link)).getReference().child("events");
                person newguy = new person("firstjhin", "lastjhin", "24");
                database1.child("person4").child("firstname").setValue(newguy.getFirstname());
                database1.child("person4").child("lastname").setValue(newguy.getFirstname());
                database1.child("person4").child("age").setValue(newguy.getAge());
            }
        });



    }
    // Function to tell the app to start getting from database on starting of the activity
    @Override protected void onStart()
    {
        super.onStart();
        ///// Recycler View ////
        adapter.startListening();
    }

    // Function to tell the app to stop getting data from database on stopping of the activity
    @Override protected void onStop()
    {
        super.onStop();
        ///// Recycler View ////
        adapter.stopListening();
    }





}
