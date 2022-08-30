package com.example.sampleproject.Fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sampleproject.Components.CalendarPickerDialog;
import com.example.sampleproject.Components.CustomCalendarView;
import com.example.sampleproject.Models.Event;
import com.example.sampleproject.Models.EventAdapter;
import com.example.sampleproject.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CalendarFragment extends Fragment {
    public RecyclerView recyclerView;
    public static EventAdapter recyclerAdapter; // Create Object of the Adapter class
    DatabaseReference mbase; // Create object of the Firebase Realtime Database
    Date selectedDate = CustomCalendarView.dateSelected;
    Boolean isPrivate = false;
    Boolean isComplete = false;
//    private CalendarFragment calendarFragment = new CalendarFragment();

    Button btnPrivate;
    Button btnPublic;

    TextView todayText;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);
        recyclerView = root.findViewById(R.id.recycler2);
        todayText = root.findViewById(R.id.tv_selDate);
        TextView errorMsg = root.findViewById(R.id.tv_error_msg_calendarr);
        Date today = new Date();
        String todayString = CalendarPickerDialog.makeDateString(today.getDate(), today.getMonth()+1, today.getYear()+1900);

        todayText.setText(todayString);

        Bundle resultIntent = getActivity().getIntent().getExtras();
        String id = resultIntent.getString("id", "1");
        String otherId = resultIntent.getString("otherId", "2");


        initRecycler(root, id, errorMsg);


        //// Init Calendar View ////
        CustomCalendarView calendarView = ((CustomCalendarView) root.findViewById(R.id.calendar_view));
        calendarView.setEventHandler(new CustomCalendarView.EventHandler() {
            @Override
            public void onDayLongPress(Date date) {
                // show returned day
                DateFormat df = SimpleDateFormat.getDateInstance();
//                Toast.makeText(getContext(), df.format(date), Toast.LENGTH_SHORT).show();
                selectedDate = date;


                Calendar now = Calendar.getInstance();
                now.setTime(selectedDate);
                now.set(Calendar.HOUR, 0);
                now.set(Calendar.MINUTE, 0);
                now.set(Calendar.SECOND, 0);
                now.set(Calendar.HOUR_OF_DAY, 0);

                selectedDate = now.getTime();
                Toast.makeText(getContext(), selectedDate.toString(), Toast.LENGTH_SHORT).show();
                updateRecycler(root, id, errorMsg);
            }
        });


        //// Init GET firebase ////
        calendarView.invokeFirebaseEvent(calendarView);


        final String[] newTitle = {""};
        final String[] newDescription = {""};;
        final Boolean[] isPrivate = {false};
        Date newDate= new Date();


        //// Init Components ////
        FloatingActionButton dialogButton = root.findViewById(R.id.fab);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
                View bottomSheetView = inflater.inflate(R.layout.fragment_bottom_dialog, root.findViewById(R.id.bottomDialogContainer));
                EditText titleDialog = bottomSheetView.findViewById(R.id.title_dialog);
                EditText descriptionDialog = bottomSheetView.findViewById(R.id.description_dialog);


                titleDialog.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}
                    @Override
                    public void afterTextChanged(Editable editable) {
                        newTitle[0] = editable.toString();
                    }
                });
                descriptionDialog.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}
                    @Override
                    public void afterTextChanged(Editable editable) {
                        newDescription[0] = editable.toString();
                    }
                });

                Button btnDeadline = bottomSheetView.findViewById(R.id.btn_deadline);
                CalendarPickerDialog.initDatePicker(btnDeadline.getContext(), btnDeadline);
                btnDeadline.setText(CalendarPickerDialog.getTodayDate());

                btnPrivate = bottomSheetView.findViewById(R.id.btn_private_sel);
                btnPublic = bottomSheetView.findViewById(R.id.btn_public_sel);

                btnPublic.setBackgroundColor(Color.RED);
                btnPrivate.setBackgroundColor(Color.GRAY);
                btnPrivate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isPrivate[0] = true;
                        btnPrivate.setBackgroundColor(Color.YELLOW);
                        btnPublic.setBackgroundColor(Color.GRAY);
                    }
                });

                btnPublic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isPrivate[0] = false;
                        btnPublic.setBackgroundColor(Color.RED);
                        btnPrivate.setBackgroundColor(Color.GRAY);
                    }
                });


                bottomSheetView.findViewById(R.id.buttonDialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Date newDate = new Date(btnDeadline.getText().toString());
                        Date today = new Date();
                        Calendar todayCal = Calendar.getInstance();
                        todayCal.setTime(today);
                        todayCal.set(Calendar.HOUR,0);
                        todayCal.set(Calendar.MINUTE,0);
                        todayCal.set(Calendar.SECOND,-1);
                        Date newToday = todayCal.getTime();

                        long daysBetween = TimeUnit.DAYS.convert(newDate.getTime()-newToday.getTime(), TimeUnit.MILLISECONDS);
                        String newDaysLeft = String.valueOf(daysBetween);

                        ////  Check Validation ////
                        if (newTitle[0].equals("")) {
                            Toast.makeText(getContext(),R.string.error_emptyTitle, Toast.LENGTH_SHORT).show();
                        } else if (newDescription[0].equals("")) {
                            Toast.makeText(getContext(),R.string.error_emptyDescription, Toast.LENGTH_SHORT).show();
                        } else if (newDaysLeft.charAt(0) == '-') {
                            Toast.makeText(getContext(),R.string.error_wrongDate, Toast.LENGTH_SHORT).show();
                        } else {


                            //DatabaseReference firebase = FirebaseDatabase.getInstance(getResources().getString(R.string.firebase_link)).getReference().child("events");
                            DatabaseReference firebase = FirebaseDatabase.getInstance(getResources().getString(R.string.firebase_link)).getReference()
                                                            .child("members");
                            String uuid = UUID.randomUUID().toString();
                            Event event = new Event(newTitle[0], newDescription[0], newDate.toString(),  Integer.valueOf(newDaysLeft), uuid, isPrivate[0], false);

                            if (event.getIsPrivate()) {
                                postToFirebase(firebase, uuid, event,id);
                            } else {
                                postToFirebase(firebase, uuid, event,id);
                                postToFirebase(firebase, uuid, event,otherId);
                            }


                            postToFirebase(firebase, uuid, event,id);

                            Toast.makeText(getContext(), "added!", Toast.LENGTH_SHORT).show();
                            bottomSheetDialog.dismiss();

                            updateRecycler(root,id,errorMsg);

                            // Reload Page w new fragment.
                            Fragment f;
                            f = new CalendarFragment();
                            FragmentTransaction ft2 =   getFragmentManager().beginTransaction();
                            ft2.replace(R.id.framelayout_line,f);
                            ft2.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                            ft2.commit();
                        }


                    }
                });

                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });

        return root;
    }

    private void postToFirebase(DatabaseReference firebase, String uuid, Event event, String selId) {
        DatabaseReference selFirebase = firebase.child("member"+selId).child("events").child(uuid);

        selFirebase.child("title").setValue(event.getTitle());
        selFirebase.child("description").setValue(event.getDescription());
        selFirebase.child("deadline").setValue(event.getDeadline());
        selFirebase.child("daysleft").setValue(event.getDaysLeft());
        selFirebase.child("uid").setValue(event.getUid());
        selFirebase.child("isprivate").setValue(event.getIsPrivate());
        selFirebase.child("iscomplete").setValue(event.getIsComplete());
        selFirebase.child("priority").setValue(event.getDaysLeft());
    }



    private void initRecycler(View root, String selId, TextView errorMsg) {
        // Create a instance of the database and get its reference
        mbase = (DatabaseReference) FirebaseDatabase.getInstance(getResources().getString(R.string.firebase_link)).getReference()
                                                    .child("members").child("member"+selId).child("events");
        Query query = mbase.orderByChild("deadline").equalTo(selectedDate.toString());

        FirebaseRecyclerOptions<Event> options
                = new FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(query, Event.class)
                .build();

        // FIXME: must just be month, day year la, use the date formatter than we will roll from there

        recyclerView = root.findViewById(R.id.recycler2);
        recyclerView.setNestedScrollingEnabled(false);

        // To display the Recycler view linearly
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // It is a class provide by the FirebaseUI to make a query in the database to fetch appropriate data

        // Connecting object of required Adapter class to the Adapter class itself
        recyclerAdapter = new EventAdapter(options);
        checkEmptyList(query, errorMsg);
        // Connecting Adapter class with the Recycler view*/
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void checkEmptyList(Query query, TextView errorMsg) {
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    errorMsg.setVisibility(View.VISIBLE);
                } else {
                    errorMsg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void updateRecycler(View root, String selId, TextView errorMsg) {
        // Create a instance of the database and get its reference
        mbase = (DatabaseReference) FirebaseDatabase.getInstance(getResources().getString(R.string.firebase_link)).getReference()
                .child("members").child("member"+selId).child("events");
        Query query = mbase.orderByChild("deadline").equalTo(selectedDate.toString());

        // FIXME: must just be month, day year la, use the date formatter than we will roll from there
        FirebaseRecyclerOptions<Event> options
                = new FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(query, Event.class)
                .build();

        recyclerAdapter.updateOptions(options);
        checkEmptyList(query, errorMsg);
        recyclerView.setAdapter(recyclerAdapter);

        String selDateString = CalendarPickerDialog.makeDateString(selectedDate.getDate(), selectedDate.getMonth() + 1, selectedDate.getYear() + 1900);
        todayText.setText(selDateString);
    }

    // Function to tell the app to start getting from database on starting of the activity
    @Override
    public void onStart() {
        super.onStart();
        recyclerAdapter.startListening();
    }

    // Function to tell the app to stop getting data from database on stopping of the activity
    @Override
    public void onStop() {
        super.onStop();
        recyclerAdapter.stopListening();
    }


}