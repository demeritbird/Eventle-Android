package com.example.sampleproject.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sampleproject.Components.CustomCalendarView;
import com.example.sampleproject.Models.Event;
import com.example.sampleproject.Models.EventAdapter;
import com.example.sampleproject.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

public class CalendarFragment extends Fragment {
    RecyclerView recyclerView;
    EventAdapter adapter; // Create Object of the Adapter class
    DatabaseReference mbase; // Create object of the Firebase Realtime Database
    Date selectedDate = new Date();

    public HashSet<Date> events = new HashSet<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);

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
        FloatingActionButton dialogButton = root.findViewById(R.id.fab);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
                View bottomSheetView = inflater.inflate(R.layout.fragment_bottom_dialog, root.findViewById(R.id.bottomDialogContainer));
                EditText titleDialog = bottomSheetView.findViewById(R.id.title_dialog);
                EditText descriptionDialog = bottomSheetView.findViewById(R.id.description_dialog);
                EditText deadlineDialog = bottomSheetView.findViewById(R.id.deadline_dialog);
//                EditText daysleftDialog  = bottomSheetView.findViewById(R.id.daysleft_dialog);

                String title = titleDialog.getText().toString();
                String description = descriptionDialog.getText().toString();
                String deadline = deadlineDialog.getText().toString();
                String daysleft = "5";

                        bottomSheetView.findViewById(R.id.buttonDialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseReference firebase = FirebaseDatabase.getInstance(getResources().getString(R.string.firebase_link)).getReference().child("events");
                        String uuid = UUID.randomUUID().toString();
                        Event event = new Event(title,description,deadline,daysleft, uuid);
                        firebase.child(uuid).child("title").setValue(event.getTitle());
                        firebase.child(uuid).child("description").setValue(event.getDescription());
                        firebase.child(uuid).child("deadline").setValue(event.getDeadline());
                        firebase.child(uuid).child("daysleft").setValue(event.getDaysLeft());
                        firebase.child(uuid).child("uid").setValue(event.getUid());

                        Toast.makeText(getContext(), "added!", Toast.LENGTH_SHORT).show();
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