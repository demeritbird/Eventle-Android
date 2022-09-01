package com.example.sampleproject.Models;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sampleproject.Components.CalendarPickerDialog;
import com.example.sampleproject.Helper.FirebaseHelper;
import com.example.sampleproject.Helper.MiscHelper;
import com.example.sampleproject.Helper.TimeHelper;
import com.example.sampleproject.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

public class EventAdapter extends FirebaseRecyclerAdapter<Event, EventAdapter.eventsViewHolder> {


    public EventAdapter(@NonNull FirebaseRecyclerOptions<Event> options) {
        super(options);
    }

    @Override
    protected void
    onBindViewHolder(@NonNull eventsViewHolder holder, int position, @NonNull Event model) {
        holder.title.setText(model.getTitle());
        holder.description.setText(model.getDescription());

        Date deadlineDate = new Date(model.getDeadline());
        int day = deadlineDate.getDate();
        int month = deadlineDate.getMonth();
        int year = deadlineDate.getYear();
        holder.deadline.setText(CalendarPickerDialog.makeDateString(day, month + 1, year + 1900));

        holder.uid = model.getUid();
        holder.isPrivate = model.getIsPrivate();
        holder.isComplete = model.getIsComplete();

        if (model.getIsComplete()) {
            setComponentsComplete(holder);
        } else {
            setComponentsUncomplete(holder);
        }
        holder.daysleft.setText(String.valueOf(model.getDaysLeft()));

        if (model.getDaysLeft() >= 0) {
            holder.daysleft.setText(String.valueOf(model.getDaysLeft()));
        } else {
            holder.daysleft.setText(String.valueOf(model.getDaysLeft() * -1));
            holder.daysleftText.setText(R.string.DAYS_AGO);
        }
    }

    private void setComponentsUncomplete(@NonNull eventsViewHolder holder) {
        holder.eventcardView.setBackgroundResource( R.drawable.eventcard_viewbg);

        //card backgrd
        holder.eventcardView.setBackgroundResource( R.drawable.eventcard_viewbg);

        // text backgrd
        holder.titleView.setBackgroundResource( R.drawable.eventcard_textbg);
        holder.descView.setBackgroundResource( R.drawable.eventcard_textbg);
        holder.deadlineView.setBackgroundResource( R.drawable.eventcard_textbg);
        holder.daysleftView.setBackgroundResource( R.drawable.eventcard_textbg);

        // checkbox backgrd
        holder.isCompleteView.setBackgroundResource(R.drawable.eventcard_cb);
        holder.editButton.setBackgroundResource( R.drawable.eventcard_textbg);
        holder.delButton.setBackgroundResource( R.drawable.eventcard_textbg);
    }

    private void setComponentsComplete(@NonNull eventsViewHolder holder) {
        //card backgrd
        holder.eventcardView.setBackgroundResource( R.drawable.eventcard_viewbg_complete);

        // text backgrd
        holder.titleView.setBackgroundResource( R.drawable.eventcard_textbg_complete);
        holder.descView.setBackgroundResource( R.drawable.eventcard_textbg_complete);
        holder.deadlineView.setBackgroundResource( R.drawable.eventcard_textbg_complete);
        holder.daysleftView.setBackgroundResource( R.drawable.eventcard_textbg_complete);

        // checkbox backgrd
        holder.isCompleteView.setBackgroundResource( R.drawable.eventcard_cb_complete);
        holder.editButton.setBackgroundResource( R.drawable.eventcard_textbg_complete);
        holder.delButton.setBackgroundResource( R.drawable.eventcard_textbg_complete);
    }

    @NonNull
    @Override
    public eventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_event_card, parent, false);
        return new eventsViewHolder(view);
    }


    class eventsViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, deadline, daysleft, daysleftText;
        String uid;
        Boolean isPrivate, isComplete;
        // views//
        View eventcardView = itemView.findViewById(R.id.event_background);
        View titleView = itemView.findViewById(R.id.title);
        View descView = itemView.findViewById(R.id.description);
        View deadlineView = itemView.findViewById(R.id.deadline);
        View daysleftView = itemView.findViewById(R.id.days_left);

        View isCompleteView = itemView.findViewById(R.id.btn_isComplete);
        AppCompatButton completeButton = itemView.findViewById(R.id.btn_isComplete);
        ImageButton editButton = itemView.findViewById(R.id.btn_editEvent);
        ImageButton delButton = itemView.findViewById(R.id.btn_delEvent);

        public eventsViewHolder(@NonNull View itemView) {
            super(itemView);
            DatabaseReference firebaseMembers = FirebaseDatabase.getInstance(itemView.getContext().getResources().getString(R.string.firebase_link)).getReference().child("members");

            //// Init Components ////
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            deadline = itemView.findViewById(R.id.deadline);
            daysleft = itemView.findViewById(R.id.days_left);
            daysleftText = itemView.findViewById(R.id.tv_daysleft);

            //// Init Variables ////
            final String[] newTitle = {title.getText().toString()};
            final String[] newDescription = {description.getText().toString()};


            //// Init Intents ////
            Bundle resultIntent = ((Activity) itemView.getContext()).getIntent().getExtras();
            String id = resultIntent.getString("id", "1");
            String otherId = resultIntent.getString("otherId", "2");

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { onClickEdit(v, firebaseMembers, newTitle, newDescription, id, otherId); }
            });
            delButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickDelete(v, firebaseMembers, id, otherId);
                }
            });
            completeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) { onClickComplete(firebaseMembers, id); }
            });

        }

        private void onClickComplete(DatabaseReference firebaseMembers, String id) {
            DatabaseReference firebaseUid = firebaseMembers.child("member"+ id).child("events").child(uid);
            if (isComplete) {
                changePriority(firebaseUid, false);
            } else {
                changePriority(firebaseUid, true);
            }
        }

        private void changePriority(DatabaseReference firebaseUid, Boolean isComplete) {
            firebaseUid.child("iscomplete").setValue(isComplete);
            firebaseUid.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (isComplete){
                        MiscHelper.remainPriority(snapshot);
                    } else {
                        MiscHelper.increasePriority(snapshot);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }


        private void onClickDelete(View view, DatabaseReference firebaseMembers, String id, String otherId) {
            FirebaseHelper.delFromFirebase(uid, firebaseMembers, id);
            FirebaseHelper.delFromFirebase(uid, firebaseMembers, otherId);
        }

        private void onClickEdit(View view, DatabaseReference firebaseMembers, String[] newTitle, String[] newDescription, String id, String otherId) {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(view.getContext(), R.style.BottomSheetDialogTheme);
            View bottomSheetView = LayoutInflater.from(bottomSheetDialog.getContext()).inflate(R.layout.fragment_bottom_dialog, null);
            newTitle[0] = title.getText().toString();
            newDescription[0] = description.getText().toString();


            // Edit Event //
            TextView eventTitle = bottomSheetView.findViewById(R.id.event_title);
            eventTitle.setText(R.string.edit_event);

            // Title //
            EditText titleDialog = bottomSheetView.findViewById(R.id.title_dialog);
            titleDialog.setText(title.getText());
            titleDialog.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) { }

                @Override
                public void afterTextChanged(Editable editable) {
                    newTitle[0] = editable.toString();
                }
            });

            // Description
            EditText descriptionDialog = bottomSheetView.findViewById(R.id.description_dialog);
            descriptionDialog.setText(description.getText());
            descriptionDialog.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) { }

                @Override
                public void afterTextChanged(Editable editable) {
                    newDescription[0] = editable.toString();
                }
            });


            // Deadline Date Dialog //
            Button btnDeadline = bottomSheetView.findViewById(R.id.btn_deadline);
            btnDeadline.setText(deadline.getText().toString());
            CalendarPickerDialog.initDatePicker(btnDeadline.getContext(), btnDeadline);

            // Privacy Setting //
            Button btnPrivate = bottomSheetView.findViewById(R.id.btn_private_sel);
            Button btnPublic = bottomSheetView.findViewById(R.id.btn_public_sel);
            if (isPrivate) {
                MiscHelper.selectPrivate(btnPrivate,btnPublic);
            } else {
                MiscHelper.selectPublic(btnPublic,btnPrivate);
            }
            btnPrivacySelect(btnPrivate, btnPublic);

            // Submit Button //
            bottomSheetView.findViewById(R.id.buttonDialog).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Date newDate = new Date(btnDeadline.getText().toString());
                    Date today = new Date();

                    Calendar todayCal = TimeHelper.setDateTimeOneDown(today);
                    Date newToday = todayCal.getTime();

                    long newDaysLeft = TimeHelper.calcDaysBetween(newDate,newToday);

                    ////  Check Validation ////
                    if (newTitle[0].equals("")) {
                        Toast.makeText(bottomSheetDialog.getContext(), R.string.error_emptyTitle, Toast.LENGTH_SHORT).show();
                    } else if (newDescription[0].equals("")) {
                        Toast.makeText(bottomSheetDialog.getContext(), R.string.error_emptyDescription, Toast.LENGTH_SHORT).show();
                    } else {
                        Event event = new Event(newTitle[0], newDescription[0], newDate.toString(), (int) newDaysLeft, uid, isPrivate, isComplete);

                        if (isPrivate) {
                            FirebaseHelper.postToFirebase(event, firebaseMembers, id);
                            FirebaseHelper.delFromFirebase(uid, firebaseMembers, otherId);
                        } else {
                            FirebaseHelper.postToFirebase(event, firebaseMembers, id);
                            FirebaseHelper.postToFirebase(event, firebaseMembers, otherId);

                        }

                        bottomSheetDialog.dismiss();
                    }
                }
            });

            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();
        }


        private void btnPrivacySelect(Button btnPrivate, Button btnPublic) {
            btnPrivate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isPrivate = true;
                    MiscHelper.selectPrivate(btnPrivate,btnPublic);
                }
            });

            btnPublic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isPrivate = false;
                    MiscHelper.selectPublic(btnPublic,btnPrivate);
                }
            });
        }


    }
}
