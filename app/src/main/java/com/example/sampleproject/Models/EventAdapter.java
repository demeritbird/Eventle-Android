package com.example.sampleproject.Models;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sampleproject.Components.CalendarPickerDialog;
import com.example.sampleproject.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class EventAdapter extends FirebaseRecyclerAdapter<Event, EventAdapter.eventsViewHolder> {

    public EventAdapter(@NonNull FirebaseRecyclerOptions<Event> options) {
        super(options);
    }

    @Override
    protected void
    onBindViewHolder(@NonNull eventsViewHolder holder, int position, @NonNull Event model) {
        holder.title.setText(model.getTitle());
        holder.description.setText(model.getDescription());
//        holder.deadline.setText(model.getDeadline());
//        holder.daysleft.setText(model.getDaysLeft());

        System.out.println("there");
        String deadlineString = model.getDeadline();
        Date deadlineDate = new Date(deadlineString);
        int day = deadlineDate.getDate();
        int month = deadlineDate.getMonth();
        int year = deadlineDate.getYear();
        holder.deadline.setText(CalendarPickerDialog.makeDateString(day, month+1, year+1900));

        Date today = new Date();
        long daysBetween = TimeUnit.DAYS.convert(Math.abs(deadlineDate.getTime()-today.getTime()), TimeUnit.MILLISECONDS);
        holder.daysleft.setText(String.valueOf(daysBetween));


        holder.uid = model.getUid();
        holder.isCompleted = model.getIsCompleted();
    }

    @NonNull
    @Override
    public eventsViewHolder
    onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_event_card, parent, false);
        return new eventsViewHolder(view);
    }


    class eventsViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, deadline, daysleft;
        String uid;
        Boolean isCompleted;

        public eventsViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            deadline = itemView.findViewById(R.id.deadline);
            daysleft = itemView.findViewById(R.id.days_left);

            Button editButton = itemView.findViewById(R.id.check_button);

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(view.getContext(), R.style.BottomSheetDialogTheme);
                    View bottomSheetView = LayoutInflater.from(bottomSheetDialog.getContext()).inflate(R.layout.fragment_bottom_dialog, null);

                    TextView eventTitle = bottomSheetView.findViewById(R.id.event_title);
                    eventTitle.setText("Edit Event");

                    EditText titleDialog = bottomSheetView.findViewById(R.id.title_dialog);
                    titleDialog.setText(title.getText());
                    EditText descriptionDialog = bottomSheetView.findViewById(R.id.description_dialog);
                    descriptionDialog.setText(description.getText());
//                    EditText deadlineDialog = bottomSheetView.findViewById(R.id.deadline_dialog);
//                    deadlineDialog.setText(deadline.getText());
                    String left = "5";

                    Button btnDate = bottomSheetView.findViewById(R.id.btnDate);
                    CalendarPickerDialog.initDatePicker(btnDate.getContext(), btnDate);
                    btnDate.setText(CalendarPickerDialog.getTodayDate());

                    Button privateSel = bottomSheetView.findViewById(R.id.btn_private_sel);
                    Button publicSel = bottomSheetView.findViewById(R.id.btn_public_sel);


                    if (isCompleted) {
                        privateSel.setBackgroundColor(Color.YELLOW);
                        publicSel.setBackgroundColor(Color.GRAY);
                    } else {
                        publicSel.setBackgroundColor(Color.RED);
                        privateSel.setBackgroundColor(Color.GRAY);
                    }

                    DatabaseReference firebase = FirebaseDatabase.getInstance(view.getContext().getResources().getString(R.string.firebase_link)).getReference().child("events").child(uid);

                    // Refactor Code here
                    privateSel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            isCompleted = false;
                            publicSel.setBackgroundColor(Color.RED);
                            privateSel.setBackgroundColor(Color.GRAY);
                        }
                    });

                    publicSel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            isCompleted = true;
                            privateSel.setBackgroundColor(Color.YELLOW);
                            publicSel.setBackgroundColor(Color.GRAY);
                        }
                    });


                    // Button //
                    bottomSheetView.findViewById(R.id.buttonDialog).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(view.getContext(), "nice la", Toast.LENGTH_SHORT).show();
                            Event event = new Event(titleDialog.getText().toString(), descriptionDialog.getText().toString(), "5".toString(), left, uid, isCompleted);
                            firebase.child("title").setValue(event.getTitle());
                            firebase.child("description").setValue(event.getDescription());
                            firebase.child("deadline").setValue(event.getDeadline());
                            firebase.child("daysleft").setValue(event.getDaysLeft());
                            firebase.child("uid").setValue(event.getUid());
                            firebase.child("iscomplete").setValue(event.getIsCompleted());

                            System.out.println(btnDate.getText().toString());

                        }
                    });
                    bottomSheetDialog.setContentView(bottomSheetView);
                    bottomSheetDialog.show();
                }
            });
        }
    }
}
