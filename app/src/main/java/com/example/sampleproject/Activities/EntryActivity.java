package com.example.sampleproject.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import android.arch.lifecycle.LifecycleOwner;
//import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;

//import com.example.sampleproject.ViewModel.MemberViewModel;
//import com.example.sampleproject.Models.Member;
import com.example.sampleproject.ViewModels.MemberViewModel;
import com.example.sampleproject.Models.EventAdapter;
import com.example.sampleproject.Models.Member;
import com.example.sampleproject.Models.MemberAdapter;
import com.example.sampleproject.R;

import java.util.List;

//public class EntryActivity extends AppCompatActivity implements View.OnClickListener {
public class EntryActivity extends AppCompatActivity {
    private Button entryMemberOne;
    private Button entryMemberTwo;
    private RecyclerView recyclerView;
    EventAdapter recyclerAdapter; // Create Object of the Adapter class

    //    private MainViewModel mainViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrypage);

//        MainViewModel mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        //// Components ////
        entryMemberOne = findViewById(R.id.btn_member1_entry);
//        entryMemberOne.setOnClickListener(this);
        entryMemberTwo = findViewById(R.id.btn_member2_entry);
//        entryMemberTwo.setOnClickListener(this);


        recyclerView = findViewById(R.id.entry_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MemberViewModel viewModel = new ViewModelProvider(this).get(MemberViewModel.class);
        viewModel.getMembers().observe(this, new Observer<List<Member>>() {
            @Override
            public void onChanged(@Nullable List<Member> members) {
                recyclerView.setAdapter(new MemberAdapter(members));
            }
        });



        // init viewmodel & livedata
/*
        MemberViewModel viewModel = new ViewModelProvider(this).get(MemberViewModel.class);
        LiveData<Member> liveData = viewModel.getDataSnapshotLiveData();

        liveData.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable DataSnapshot snapshot) {
                if (snapshot != null) {
                    String newMemberOneName= snapshot.child("member1").child("name").getValue(String.class);
                    entryMemberOne.setText(newMemberOneName);
                    String newMemberTwoName = snapshot.child("member2").child("name").getValue(String.class);
                    entryMemberTwo.setText(newMemberTwoName);
                }
            }
        });
*/


        //// Update Member Name ////
/*        DatabaseReference firebaseMembers = FirebaseDatabase.getInstance(getResources().getString(R.string.firebase_link)).getReference().child("members");
        DatabaseReference memberOneFirebase = firebaseMembers.child("member1").child("name");
        updateMemberName(memberOneFirebase, entryMemberOne);
        DatabaseReference memberTwoFirebase = firebaseMembers.child("member2").child("name");
        updateMemberName(memberTwoFirebase, entryMemberTwo);*/


    }

//  @Override
//  public void onClick(View v) {
//
//  }

/*    private void updateMemberName(DatabaseReference firebase, Button memberButton) {

        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String newMemberName = dataSnapshot.getValue().toString();
                memberButton.setText(newMemberName);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(String.valueOf(R.string.TAG_MESSAGE), String.valueOf(R.string.error_readFirebase), error.toException());
            }
        });
    }*/

//    @Override
//    public void onClick(View view) {
//        if (view.getId() == R.id.btn_member2_entry) {
//            openApplication(entryMemberTwo, "2", "1");
//        } else if (view.getId() == R.id.btn_member1_entry) {
//            openApplication(entryMemberOne, "1", "2");
//        }
//    }
//
//    private void openApplication(Button memberButton, String id, String otherId) {
//        Animation animation = new TranslateAnimation(0, 0, 0, 10);
//        animation.setDuration(200);
//        memberButton.startAnimation(animation);
//        Intent intentSub = new Intent(EntryActivity.this, ApplicationActivity.class);
//        intentSub.putExtra("username", memberButton.getText());
//        intentSub.putExtra("id",id);
//        intentSub.putExtra("otherId",otherId);
//        startActivity(intentSub);
//    }
}