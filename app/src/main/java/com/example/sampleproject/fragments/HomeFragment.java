package com.example.sampleproject.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.sampleproject.ApplicationActivity;
import com.example.sampleproject.R;
import com.example.sampleproject.person;
import com.example.sampleproject.personAdaptar;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    personAdaptar adapter; // Create Object of the Adapter class
    DatabaseReference mbase; // Create object of the Firebase Realtime Database
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);



        ///// Recycler View ////

        // Create a instance of the database and get its reference
        mbase = FirebaseDatabase.getInstance(getResources().getString(R.string.firebase_link)).getReference().child("people");

        recyclerView = root.findViewById(R.id.recycler1);
        recyclerView.setNestedScrollingEnabled(false);

        // To display the Recycler view linearly
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

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


        Button addMember = root.findViewById(R.id.add_member);
        addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference database1 = FirebaseDatabase.getInstance(getResources().getString(R.string.firebase_link)).getReference().child("people");
                person newguy = new person("firstjhin", "lastjhin", "24");
                database1.child("person4").child("firstname").setValue(newguy.getFirstname());
                database1.child("person4").child("lastname").setValue(newguy.getFirstname());
                database1.child("person4").child("age").setValue(newguy.getAge());
            }
        });

    return root;

    }

    // Function to tell the app to start getting from database on starting of the activity
    @Override
    public void onStart()
    {
        super.onStart();
        System.out.println("this is starting");
        ///// Recycler View ////
        adapter.startListening();
    }

    // Function to tell the app to stop getting data from database on stopping of the activity
    @Override
    public void onStop()
    {
        super.onStop();
        ///// Recycler View ////
        adapter.stopListening();
    }
}