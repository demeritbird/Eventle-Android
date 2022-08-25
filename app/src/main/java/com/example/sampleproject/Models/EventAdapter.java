package com.example.sampleproject.Models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sampleproject.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class EventAdapter extends FirebaseRecyclerAdapter< Event, EventAdapter.eventsViewHolder> {

    public EventAdapter(@NonNull FirebaseRecyclerOptions<Event> options) {
        super(options);
    }

    @Override
    protected void
    onBindViewHolder(@NonNull eventsViewHolder holder, int position, @NonNull Event model) {
        holder.title.setText(model.getTitle());
        holder.description.setText(model.getDescription());
        holder.deadline.setText(model.getDeadline());
        holder.daysleft.setText(model.getDaysLeft());
    }


    @NonNull
    @Override
    public eventsViewHolder
    onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_event_card, parent, false);
        return new eventsViewHolder(view);
    }


    class eventsViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, deadline, daysleft;

        public eventsViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            deadline = itemView.findViewById(R.id.deadline);
            daysleft = itemView.findViewById(R.id.days_left);

            Button check = itemView.findViewById(R.id.check_button);


            // NOTE: setText of each recyclerView and their buttons
            check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Bundle args = new Bundle();
//                    args.putString("key", "value");
//                    DialogFragment newFragment = new YourDialogFragment();
//                    newFragment.setArguments(args);
//                    newFragment.show(getSupportFragmentManager(), "TAG");


                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(view.getContext(), R.style.BottomSheetDialogTheme);

                    View bottomSheetView =LayoutInflater.from(bottomSheetDialog.getContext()).inflate(R.layout.fragment_bottom_dialog, null);


                    // textview //
                    //TODO: just directly change here lmaooo dont need bundle args

                    TextView eventTitle = bottomSheetView.findViewById(R.id.event_title);
                    eventTitle.setText("Edit Event");

                    EditText titleDialog = bottomSheetView.findViewById(R.id.title_dialog);
                    titleDialog.setText(title.getText());
                    EditText descriptionDialog = bottomSheetView.findViewById(R.id.description_dialog);
                    descriptionDialog.setText(description.getText());
                    EditText deadlineDialog = bottomSheetView.findViewById(R.id.deadline_dialog);
                    deadlineDialog.setText(deadline.getText());


                    // Button //
                    bottomSheetView.findViewById(R.id.buttonDialog).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(view.getContext(), "nice la", Toast.LENGTH_SHORT).show();
                        }
                    });
                    bottomSheetDialog.setContentView(bottomSheetView);
                    bottomSheetDialog.show();

                }
            });
        }
    }
}
