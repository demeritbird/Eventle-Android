package com.example.sampleproject.Models;

import android.app.Activity;
import android.graphics.Color;
import android.media.Image;
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

        Date deadlineDate = new Date(model.getDeadline());
        int day = deadlineDate.getDate();
        int month = deadlineDate.getMonth();
        int year = deadlineDate.getYear();
        holder.deadline.setText(CalendarPickerDialog.makeDateString(day, month + 1, year + 1900));


        holder.uid = model.getUid();
        holder.isPrivate = model.getIsPrivate();
        holder.isComplete = model.getIsComplete();

        if (model.getIsComplete()) {
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
        } else {
            holder.eventcardView.setBackgroundResource( R.drawable.eventcard_viewbg);

            //card backgrd
            holder.eventcardView.setBackgroundResource( R.drawable.eventcard_viewbg);

            // text backgrd
            holder.titleView.setBackgroundResource( R.drawable.eventcard_textbg);
            holder.descView.setBackgroundResource( R.drawable.eventcard_textbg);
            holder.deadlineView.setBackgroundResource( R.drawable.eventcard_textbg);
            holder.daysleftView.setBackgroundResource( R.drawable.eventcard_textbg);

            // checkbox backgrd
            holder.isCompleteView.setBackgroundResource( R.drawable.eventcard_cb_complete);
            holder.editButton.setBackgroundResource( R.drawable.eventcard_textbg);
            holder.delButton.setBackgroundResource( R.drawable.eventcard_textbg);

            holder.isCompleteView.setBackgroundResource(R.drawable.eventcard_cb);
        }
        holder.daysleft.setText(String.valueOf(model.getDaysLeft()));

        if (model.getDaysLeft() >= 0) {
            holder.daysleft.setText(String.valueOf(model.getDaysLeft()));
        } else {
            holder.daysleft.setText(String.valueOf(model.getDaysLeft() * -1) );
            holder.daysleftText.setText(R.string.DAYS_AGO);
        }
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
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            deadline = itemView.findViewById(R.id.deadline);
            daysleft = itemView.findViewById(R.id.days_left);
            daysleftText = itemView.findViewById(R.id.tv_daysleft);
            final String[] newTitle = {title.getText().toString()};
            final String[] newDescription = {description.getText().toString()};
            Bundle resultIntent = ((Activity) itemView.getContext()).getIntent().getExtras();
            String id = resultIntent.getString("id", "1");
            String otherId = resultIntent.getString("otherId", "2");

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickEdit(v, editButton, newTitle, newDescription, id, otherId);
                }
            });
            delButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("one");
                    onClickDelete(v, delButton, id, otherId);
                }
            });
            completeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseReference firebase = FirebaseDatabase.getInstance(view.getContext().getResources().getString(R.string.firebase_link)).getReference().child("members").child("member"+id).child("events").child(uid);
                    if (isComplete) {
                        firebase.child("iscomplete").setValue(false);

                        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                snapshot.child("priority").getRef().setValue(Integer.parseInt(snapshot.child("daysleft").getValue().toString()) );
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {
                        firebase.child("iscomplete").setValue(true);

                        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                snapshot.child("priority").getRef().setValue(Integer.parseInt(snapshot.child("daysleft").getValue().toString()) +99999 );
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            });


        }

        private void onClickDelete(View view, ImageButton delButton, String id, String otherId) {

            DatabaseReference firebase = FirebaseDatabase.getInstance(view.getContext().getResources().getString(R.string.firebase_link)).getReference();
            removeFromFireBase(uid, firebase, id);
            removeFromFireBase(uid, firebase, otherId);
            System.out.println("two");

        }

        private void onClickEdit(View view, ImageButton editButton, String[] newTitle, String[] newDescription, String id, String otherId) {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(view.getContext(), R.style.BottomSheetDialogTheme);
            View bottomSheetView = LayoutInflater.from(bottomSheetDialog.getContext()).inflate(R.layout.fragment_bottom_dialog, null);
            newTitle[0] = title.getText().toString();
            newDescription[0] = description.getText().toString();


            // Text Components


            // Edit Event //
            TextView eventTitle = bottomSheetView.findViewById(R.id.event_title);
            eventTitle.setText(R.string.edit_event);

            // Title //
            EditText titleDialog = bottomSheetView.findViewById(R.id.title_dialog);
            titleDialog.setText(title.getText());


            titleDialog.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    newTitle[0] = editable.toString();
                    System.out.println(newTitle[0]);
                }
            });

            // Description
            EditText descriptionDialog = bottomSheetView.findViewById(R.id.description_dialog);
            descriptionDialog.setText(description.getText());


            descriptionDialog.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    newDescription[0] = editable.toString();
                }
            });


            // Deadline Date Dialog //
            Button btnDeadline = bottomSheetView.findViewById(R.id.btn_deadline);
            btnDeadline.setText(deadline.getText().toString());
            CalendarPickerDialog.initDatePicker(btnDeadline.getContext(), btnDeadline);


            // privacy //
            Boolean newPrivacy;
            Button privateSel = bottomSheetView.findViewById(R.id.btn_private_sel);
            Button publicSel = bottomSheetView.findViewById(R.id.btn_public_sel);
            if (isPrivate) {
                privateSel.setBackgroundColor(Color.YELLOW);
                publicSel.setBackgroundColor(Color.GRAY);
            } else {
                publicSel.setBackgroundColor(Color.RED);
                privateSel.setBackgroundColor(Color.GRAY);
            }
            btnPrivacySelect(privateSel, publicSel);


            DatabaseReference firebase = FirebaseDatabase.getInstance(view.getContext().getResources().getString(R.string.firebase_link)).getReference();


            // Button //
            bottomSheetView.findViewById(R.id.buttonDialog).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Date newDate = new Date(btnDeadline.getText().toString());
                    Date today = new Date();
                    Calendar todayCal = Calendar.getInstance();
                    todayCal.setTime(today);
                    todayCal.set(Calendar.HOUR, 0);
                    todayCal.set(Calendar.MINUTE, 0);
                    todayCal.set(Calendar.SECOND, -1);
                    Date newToday = todayCal.getTime();

                    long daysBetween = TimeUnit.DAYS.convert(newDate.getTime() - newToday.getTime(), TimeUnit.MILLISECONDS);

                    String newDaysLeft = String.valueOf(daysBetween);

                    ////  Check Validation ////
                    if (newTitle[0].equals("")) {
                        Toast.makeText(bottomSheetDialog.getContext(), R.string.error_emptyTitle, Toast.LENGTH_SHORT).show();
                    } else if (newDescription[0].equals("")) {
                        Toast.makeText(bottomSheetDialog.getContext(), R.string.error_emptyDescription, Toast.LENGTH_SHORT).show();
                    } else if (newDaysLeft.charAt(0) == '-') {
                        Toast.makeText(bottomSheetDialog.getContext(), R.string.error_wrongDate, Toast.LENGTH_SHORT).show();
                    } else {
                        Event event = new Event(newTitle[0], newDescription[0], newDate.toString(), Integer.valueOf(newDaysLeft), uid, isPrivate, isComplete);

                        if (isPrivate) {
                            editToFirebase(event, firebase, id);
                            removeFromFireBase(uid, firebase, otherId);
                        } else {
                            editToFirebase(event, firebase, id);
                            editToFirebase(event, firebase, otherId);
                        }
                        System.out.println("------------------------");
                        //editToFirebase(event, firebase, id);
                        bottomSheetDialog.dismiss();
                    }
                }
            });

            //postToFireBase(bottomSheetView, event, firebase);
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();
        }

        private void editToFirebase(Event event, DatabaseReference firebase, String selId) {
            DatabaseReference selFirebase = firebase.child("members").child("member" + selId).child("events").child(event.getUid());

            selFirebase.child("title").setValue(event.getTitle());
            selFirebase.child("description").setValue(event.getDescription());
            selFirebase.child("deadline").setValue(event.getDeadline());
            selFirebase.child("daysleft").setValue(event.getDaysLeft());
            selFirebase.child("uid").setValue(event.getUid());
            selFirebase.child("isprivate").setValue(event.getIsPrivate());
            selFirebase.child("priority").setValue(event.getDaysLeft());
            selFirebase.child("iscomplete").setValue(event.getIsComplete());
        }


        private void removeFromFireBase(String uid, DatabaseReference firebase, String otherId) {
            firebase.child("members").child("member" + otherId).child("events").child(uid).setValue(null);
        }

        private void btnPrivacySelect(Button privateSel, Button publicSel) {
            privateSel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isPrivate = true;
                    privateSel.setBackgroundColor(Color.YELLOW);
                    publicSel.setBackgroundColor(Color.GRAY);
                }
            });

            publicSel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isPrivate = false;
                    publicSel.setBackgroundColor(Color.RED);
                    privateSel.setBackgroundColor(Color.GRAY);
                }
            });
        }

    }
}
