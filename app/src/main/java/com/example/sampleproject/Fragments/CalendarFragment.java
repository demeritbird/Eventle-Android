package com.example.sampleproject.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.sampleproject.Components.CustomCalendarView;
import com.example.sampleproject.Models.Event;
import com.example.sampleproject.Models.EventAdapter;
import com.example.sampleproject.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

public class CalendarFragment extends Fragment {
    RecyclerView recyclerView;
    EventAdapter adapter; // Create Object of the Adapter class
    DatabaseReference mbase; // Create object of the Firebase Realtime Database
    Date selectedDate = new Date();

    public HashSet<Date> events = new HashSet<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);

//        HomeFragment homeFragment = new HomeFragment();
//        homeFragment.actRecycler(root);

//        // Create a instance of the database and get its reference
//        mbase = FirebaseDatabase.getInstance(getResources().getString(R.string.firebase_link)).getReference().child("events");
//
//        recyclerView = root.findViewById(R.id.recycler1);
//        recyclerView.setNestedScrollingEnabled(false);
//
//        // To display the Recycler view linearly
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        // It is a class provide by the FirebaseUI to make a query in the database to fetch appropriate data
//        FirebaseRecyclerOptions<Event> options
//                = new FirebaseRecyclerOptions.Builder<Event>()
//                .setQuery(mbase, Event.class)
//                .build();
//        // Connecting object of required Adapter class to the Adapter class itself
//        adapter = new EventAdapter(options);
//        // Connecting Adapter class with the Recycler view*/
//        recyclerView.setAdapter(adapter);

        actRecycler(root);


        //// Init Calendar View ////
        CustomCalendarView calendarView = ((CustomCalendarView) root.findViewById(R.id.calendar_view));
        calendarView.setEventHandler(new CustomCalendarView.EventHandler() {
            @Override
            public void onDayLongPress(Date date) {
                // show returned day
                DateFormat df = SimpleDateFormat.getDateInstance();
                Toast.makeText(getContext(), df.format(date), Toast.LENGTH_SHORT).show();
                selectedDate = date;
                System.out.println(date);
                System.out.println("dafasasdlsndksandksads");
            }
        });

        //// Init GET firebase ////
        calendarView.invokeFirebaseEvent(calendarView);

        //// Init Components ////
        Button dialogButton = root.findViewById(R.id.buttontodialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
                View bottomSheetView = inflater.inflate(R.layout.layout_bottom_dialog, root.findViewById(R.id.bottomDialogContainer));
                bottomSheetView.findViewById(R.id.buttonDialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(), "nicela", Toast.LENGTH_SHORT).show();
                    }
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // inflater.inflate(R.menu.calendar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    public void actRecycler(View root) {

        // Create a instance of the database and get its reference
        mbase = (DatabaseReference) FirebaseDatabase.getInstance(getResources().getString(R.string.firebase_link)).getReference().child("events");
        System.out.println(selectedDate.toString());
        System.out.println("here i come");
        Query queryBy = mbase.child("deadline").equalTo(selectedDate.toString());
        // FIXME: must just be month, day year la, use the date formatter than we will roll from there

        recyclerView = root.findViewById(R.id.recycler2);
        recyclerView.setNestedScrollingEnabled(false);

        // To display the Recycler view linearly
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // It is a class provide by the FirebaseUI to make a query in the database to fetch appropriate data
        FirebaseRecyclerOptions<Event> options
                = new FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(queryBy, Event.class)
                .build();
        // Connecting object of required Adapter class to the Adapter class itself
        adapter = new EventAdapter(options);
        // Connecting Adapter class with the Recycler view*/
        recyclerView.setAdapter(adapter);
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