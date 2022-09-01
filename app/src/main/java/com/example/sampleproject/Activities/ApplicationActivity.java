package com.example.sampleproject.Activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sampleproject.Fragments.HomeFragment;
import com.example.sampleproject.Fragments.CalendarFragment;
import com.example.sampleproject.Fragments.ProfileFragment;
import com.example.sampleproject.R;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ApplicationActivity extends AppCompatActivity {

    private HomeFragment homeFragment = new HomeFragment();
    private CalendarFragment calendarFragment = new CalendarFragment();
    private ProfileFragment profileFragment = new ProfileFragment();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);

        //// Bottom Navigation Fragments ////
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_area, homeFragment).commit();
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_area, homeFragment).commit();
                        return true;
                    case R.id.calendar:
                        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_area, calendarFragment).commit();
                        return true;
                    case R.id.profile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_area, profileFragment).commit();
                        return true;
                }
                return false;
            }
        });

    }


}
