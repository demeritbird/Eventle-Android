package com.example.sampleproject.Fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sampleproject.Components.CalendarPickerDialog;
import com.example.sampleproject.R;
import com.example.sampleproject.Models.Event;
import com.example.sampleproject.Models.EventAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    EventAdapter adapter; // Create Object of the Adapter class
    DatabaseReference mbase; // Create object of the Firebase Realtime Database

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = root.findViewById(R.id.recycler1);
        ///// Recycler View ////
        initRecycler(root);
        Bundle resultIntent = getActivity().getIntent().getExtras();
        String id = resultIntent.getString("id", "2");
        String newUserName = resultIntent.getString("username", "no");

        ImageView userImage = root.findViewById(R.id.userImage);
        DatabaseReference firebase2 = FirebaseDatabase.getInstance(getResources().getString(R.string.firebase_link)).getReference().child("members").child("member" + id).child("image").child("imageUri");
        firebase2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String yes = dataSnapshot.getValue().toString();
                if (!yes.equals("")) {
                    Uri imageUri = Uri.parse(yes);
                    Picasso.get().load(imageUri).placeholder(R.drawable.dembirdimage).into(userImage);
                } else {
                    System.out.println("no image");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        TextView username = root.findViewById(R.id.username_text);
        username.setText(newUserName);


        return root;
    }

    // TODO: move to helper function file, this file is only for view
    public void initRecycler(View root) {

        System.out.println("here");
        // Create a instance of the database and get its reference
        mbase = FirebaseDatabase.getInstance(getResources().getString(R.string.firebase_link)).getReference().child("events");
        //FIXME: change daysleft to int


        mbase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DataSnapshot deadlineString = snapshot.child("deadline");
                    System.out.println(deadlineString.getValue());
                    System.out.println("asdasdasd");
                    Date deadlineDate = new Date(deadlineString.getValue().toString());

                    int day = deadlineDate.getDate();
                    int month = deadlineDate.getMonth();
                    int year = deadlineDate.getYear();

                    Date today = new Date();
                    long daysBetween = TimeUnit.DAYS.convert(deadlineDate.getTime()-today.getTime(), TimeUnit.MILLISECONDS);
                    snapshot.child("daysleft").getRef().setValue(String.valueOf(daysBetween));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("sad", "Failed to read value.", error.toException());
            }
        });

        Query queryBy = mbase.orderByChild("daysleft").limitToFirst(2);


        recyclerView.setNestedScrollingEnabled(false);

        // To display the Recycler view linearly
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // It is a class provide by the FirebaseUI to make a query in the database to fetch appropriate data
        FirebaseRecyclerOptions<Event> options = new FirebaseRecyclerOptions.Builder<Event>()
                                                        .setQuery(queryBy, Event.class)
                                                        .build();
        // Connecting object of required Adapter class to the Adapter class itself
        adapter = new EventAdapter(options);
        // Connecting Adapter class with the Recycler view*/
        recyclerView.setAdapter(adapter);
    }

    // Function to tell the app to start getting from database on starting of the activity
    @Override
    public void onStart() {
        super.onStart();
        System.out.println("this is starting");
        ///// Recycler View ////
        adapter.startListening();
    }

    // Function to tell the app to stop getting data from database on stopping of the activity
    @Override
    public void onStop() {
        super.onStop();
        ///// Recycler View ////
        adapter.stopListening();
    }
}