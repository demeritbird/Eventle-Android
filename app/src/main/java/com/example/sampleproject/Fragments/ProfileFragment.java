package com.example.sampleproject.Fragments;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sampleproject.Models.Member;
import com.example.sampleproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executor;

import javax.annotation.Nullable;


public class ProfileFragment extends Fragment {
    private static final int galleryPick = 1;
    private Uri imageUri;
    ImageView userImage;
    String id;
    private FirebaseAuth mAuth;
    private int eventsInWeek = 0;
    private int eventsInMonth = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_profile, container, false);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
        } else {
            signInAsAnonymous();
        }
        Bundle resultIntent = getActivity().getIntent().getExtras();
        id = resultIntent.getString("id", "2");
        String newUserName = resultIntent.getString("username", "no");

        DatabaseReference firebase = FirebaseDatabase.getInstance(getResources().getString(R.string.firebase_link)).getReference().child("members").child("member" + id);
        DatabaseReference firebaseImage = firebase.child("image").child("imageUri");
        firebaseImage.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String yes = dataSnapshot.getValue().toString();
                if (!yes.equals("")) {
                    imageUri = Uri.parse(yes);
                    Picasso.get().load(imageUri).placeholder(R.drawable.dembirdimage).into(userImage);
                } else {
                    System.out.println("no image");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        EditText usernameEditText = root.findViewById(R.id.editTextTextPersonName);
        usernameEditText.setText(newUserName);

        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                System.out.println(editable.toString());
                DatabaseReference firebase = FirebaseDatabase.getInstance(getResources().getString(R.string.firebase_link)).getReference().child("members").child("member" + id).child("name");
                firebase.setValue(editable.toString());
            }
        });

        userImage = root.findViewById(R.id.userImage);
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, galleryPick);
            }
        });

        DatabaseReference firebaseEvents = firebase.child("events");
        firebaseEvents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventsInWeek = 0;
                eventsInMonth = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //TODO: for that week and for that month.


                    Date today = new Date();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(today);

                    Date childDate = new Date(String.valueOf(snapshot.child("deadline").getValue()));
                    Calendar cal2 = Calendar.getInstance();
                    cal2.setTime(childDate);



                    boolean isSameWeek = cal.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR) &&
                                cal.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
                    if ( isSameWeek ) { eventsInWeek += 1; }

                    // month //
                    boolean isSameMonth = cal.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)&&
                            cal.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
                    if (isSameMonth) { eventsInMonth += 1; }
                }

                // Set Week //
                String stringEventsInWeek = String.valueOf(eventsInWeek);
                TextView eventsInWeek = root.findViewById(R.id.tv_eventsPerWeek);
                eventsInWeek.setText(stringEventsInWeek);

                // Set Month //
                String stringEventsInMoth = String.valueOf(eventsInMonth);
                TextView eventsInMoth = root.findViewById(R.id.tv_eventsPerMonth);
                eventsInMoth.setText(stringEventsInMoth);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return root;
    }

    private void signInAsAnonymous() {
        mAuth.signInAnonymously().addOnSuccessListener((Activity) getContext(), new OnSuccessListener<AuthResult>() {
        @Override
        public void onSuccess(AuthResult authResult) {
                // do your stuff
            }
        })
        .addOnFailureListener((Executor) this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("as", "signInAnonymously:FAILURE", exception);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == galleryPick && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Log.d("sad", "new image uri is " + imageUri);
            userImage.setImageURI(imageUri);

            if (imageUri != null) {
                uploadToFirebase(imageUri);
            } else {
                System.out.println("yes");
            }
        }
    }

    private void uploadToFirebase(Uri uri) {
        DatabaseReference koot = FirebaseDatabase.getInstance(getResources().getString(R.string.firebase_link)).getReference().child("members");
        StorageReference referemce = FirebaseStorage.getInstance("gs://scheduleapp-3ebb7.appspot.com").getReference();

        StorageReference fileRef = referemce.child(System.currentTimeMillis() + "." + ".jpg");

        fileRef.putFile(uri).addOnSuccessListener((new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Member member = new Member(uri.toString());
                        koot.child("member" + id).child("image").setValue(member);
                    }
                });
            }
        })).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("FAIL");
            }
        });
    }


}