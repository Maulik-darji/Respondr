package com.app.Respondr;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView tvWelcome;
    private Button btnSignOut;

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
        tvWelcome = findViewById(R.id.tvWelcome);
        btnSignOut = findViewById(R.id.btnSignOut);
        
        // Set click listener for sign out
        btnSignOut.setOnClickListener(v -> signOut());
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
        } else {
            // Update welcome message with user email
            tvWelcome.setText("Welcome " + currentUser.getEmail() + "!");
        }
    }

    private void signOut() {
        mAuth.signOut();
        Intent intent = new Intent(this, LandingActivity.class);
        startActivity(intent);
        finish();
    }
}