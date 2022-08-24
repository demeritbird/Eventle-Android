package com.example.sampleproject;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sampleproject.Models.EventAdapter;
import com.example.sampleproject.Fragments.HomeFragment;
import com.example.sampleproject.Fragments.NotificationFragment;
import com.example.sampleproject.Fragments.SettingsFragment;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DatabaseReference;
import com.google.android.material.bottomnavigation.BottomNavigationView;
public class ApplicationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    EventAdapter adapter; // Create Object of the Adapter class
    DatabaseReference mbase; // Create object of the Firebase Realtime Database


    HomeFragment homeFragment = new HomeFragment();
    NotificationFragment notificationFragment = new NotificationFragment();
    SettingsFragment settingsFragment = new SettingsFragment();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);

        //// Bottom Navigation Fragment ////
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.no, homeFragment).commit();
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected( MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.no, homeFragment).commit();
                        return true;
                    case R.id.notification:
                        getSupportFragmentManager().beginTransaction().replace(R.id.no, notificationFragment).commit();
                        return true;
                    case R.id.settings:
                        getSupportFragmentManager().beginTransaction().replace(R.id.no, settingsFragment).commit();
                        return true;
                }
                return false;
            }
        });

    }


}
