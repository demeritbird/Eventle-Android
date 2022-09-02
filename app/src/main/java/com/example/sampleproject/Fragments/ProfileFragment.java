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

import com.example.sampleproject.Helper.FirebaseHelper;
import com.example.sampleproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        //// Firebase Authentication ////
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        //if (user == null) { signInAsAnonymous(); }


        //// Receive Intents ////
        Bundle resultIntent = getActivity().getIntent().getExtras();
        id = resultIntent.getString("id", "1");
        String newUserName = resultIntent.getString("username", "user");

        /// Init Image & Username ////
        ImageView userImage = root.findViewById(R.id.iv_userImage);
        FirebaseHelper.changeImageFromFirebase(userImage,id, imageUri );
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, galleryPick);
            }
        });

        DatabaseReference firebaseMemberId = FirebaseDatabase.getInstance(getResources().getString(R.string.firebase_link)).getReference().child("members").child("member" + id);

        EditText usernameEditText = root.findViewById(R.id.editTextTextPersonName);
        usernameEditText.setText(newUserName);
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {  }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                DatabaseReference firebase = firebaseMemberId.child("name");
                firebase.setValue(editable.toString());
            }
        });


        FirebaseHelper.calcInsights(root ,firebaseMemberId, eventsInWeek, eventsInMonth );


        return root;
    }

//    private void signInAsAnonymous() {
//        mAuth.signInAnonymously().addOnSuccessListener((Activity) getContext(), new OnSuccessListener<AuthResult>() {
//        @Override
//        public void onSuccess(AuthResult authResult) {
//                // do your stuff
//            }
//        });
////        .addOnFailureListener((Executor) this, new OnFailureListener() {
////            @Override
////            public void onFailure(@NonNull Exception exception) {
////                Log.e(String.valueOf(R.string.error_readFirebase), String.valueOf(R.string.error_signinFail), exception);
////            }
////        });
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == galleryPick && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            userImage.setImageURI(imageUri);

            if (imageUri != null) {
                DatabaseReference firebaseMembers = FirebaseDatabase.getInstance(getResources().getString(R.string.firebase_link)).getReference().child("members");
                FirebaseHelper.uploadImageToFirebase(imageUri, firebaseMembers, id);
            } else {
                Log.w(String.valueOf(R.string.error_readFirebase), String.valueOf(R.string.TAG_failImage));
            }
        }
    }



}