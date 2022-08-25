package com.example.sampleproject.Activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sampleproject.Models.EventAdapter;
import com.example.sampleproject.Fragments.HomeFragment;
import com.example.sampleproject.Fragments.CalendarFragment;
import com.example.sampleproject.Fragments.ProfileFragment;
import com.example.sampleproject.R;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DatabaseReference;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ApplicationActivity extends AppCompatActivity {

    HomeFragment homeFragment = new HomeFragment();
    CalendarFragment calendarFragment = new CalendarFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);

        //// Bottom Navigation Fragments ////
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.no, homeFragment).commit();
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.no, homeFragment).commit();
                        return true;
                    case R.id.calendar:
                        getSupportFragmentManager().beginTransaction().replace(R.id.no, calendarFragment).commit();
                        return true;
                    case R.id.profile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.no, profileFragment).commit();
                        return true;
                }
                return false;
            }
        });

    }


}
