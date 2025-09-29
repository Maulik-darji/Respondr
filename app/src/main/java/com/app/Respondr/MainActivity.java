package com.app.Respondr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private BottomNavigationView bottomNavigation;
    private FloatingActionButton fabSOS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        
        // Initialize UI components
        bottomNavigation = findViewById(R.id.bottomNavigation);
        fabSOS = findViewById(R.id.fabSOS);
        
        // Set up bottom navigation
        setupBottomNavigation();
        
        // Set click listener for SOS button
        fabSOS.setOnClickListener(v -> handleSOS());
        
        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            bottomNavigation.setSelectedItemId(R.id.nav_home);
        }
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            
            if (item.getItemId() == R.id.nav_home) {
                fragment = new HomeFragment();
            } else if (item.getItemId() == R.id.nav_map) {
                fragment = new MapFragment();
            } else if (item.getItemId() == R.id.nav_contacts) {
                fragment = new ContactsFragment();
            } else if (item.getItemId() == R.id.nav_history) {
                fragment = new HistoryFragment();
            }
            
            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }
            
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private void handleSOS() {
        // TODO: Implement SOS emergency functionality
        // This could include:
        // - Sending emergency alerts
        // - Calling emergency contacts
        // - Sharing location
        // - Recording emergency details
        
        // For now, show a placeholder
        android.widget.Toast.makeText(this, "SOS Emergency Activated!", android.widget.Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // User is not signed in, redirect to LandingActivity
            Intent intent = new Intent(this, LandingActivity.class);
            startActivity(intent);
            finish();
        }
    }
}