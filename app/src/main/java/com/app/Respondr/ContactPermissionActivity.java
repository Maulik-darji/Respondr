package com.app.Respondr;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ContactPermissionActivity extends AppCompatActivity {

    private static final int CONTACT_PERMISSION_REQUEST_CODE = 1001;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1002;
    private MaterialButton btnContinue, btnSkip;
    private boolean contactsSynced = false;
    private boolean locationPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_permission);

        initializeViews();
        setClickListeners();
    }

    private void initializeViews() {
        btnContinue = findViewById(R.id.btnContinue);
        btnSkip = findViewById(R.id.btnSkip);
    }

    private void setClickListeners() {
        btnContinue.setOnClickListener(v -> requestContactPermission());
        btnSkip.setOnClickListener(v -> navigateToMainApp());
    }

    private void requestContactPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) 
                != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(this, 
                new String[]{Manifest.permission.READ_CONTACTS}, 
                CONTACT_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, sync contacts
            syncContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == CONTACT_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Contact permission granted, sync contacts
                syncContacts();
            } else {
                // Contact permission denied, show message and request location
                Toast.makeText(this, "Contact access denied. You can sync contacts later in settings.", 
                    Toast.LENGTH_LONG).show();
                requestLocationPermission();
            }
        } else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted
                locationPermissionGranted = true;
                Toast.makeText(this, "Location access granted for map features.", 
                    Toast.LENGTH_SHORT).show();
            } else {
                // Location permission denied
                Toast.makeText(this, "Location access denied. Map features may be limited.", 
                    Toast.LENGTH_LONG).show();
            }
            // Navigate to main app regardless of location permission result
            navigateToMainApp();
        }
    }

    private void syncContacts() {
        try {
            List<ContactInfo> contacts = getContacts();
            
            // Here you would typically save contacts to your database or send to server
            // For now, we'll just show a success message
            Toast.makeText(this, "Successfully synced " + contacts.size() + " contacts!", 
                Toast.LENGTH_SHORT).show();
            
            contactsSynced = true;
            
            // After syncing contacts, request location permission
            requestLocationPermission();
            
        } catch (Exception e) {
            Toast.makeText(this, "Failed to sync contacts: " + e.getMessage(), 
                Toast.LENGTH_SHORT).show();
            // Even if contact sync fails, request location permission
            requestLocationPermission();
        }
    }

    private List<ContactInfo> getContacts() {
        List<ContactInfo> contacts = new ArrayList<>();
        
        Cursor cursor = getContentResolver().query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null, null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(
                    ContactsContract.CommonDataKinds.Phone.NUMBER));
                
                if (name != null && phoneNumber != null) {
                    contacts.add(new ContactInfo(name, phoneNumber));
                }
            }
            cursor.close();
        }
        
        return contacts;
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(this, 
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 
                LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted
            locationPermissionGranted = true;
            Toast.makeText(this, "Location access already granted for map features.", 
                Toast.LENGTH_SHORT).show();
            navigateToMainApp();
        }
    }

    private void navigateToMainApp() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // Simple data class for contact information
    public static class ContactInfo {
        public String name;
        public String phoneNumber;

        public ContactInfo(String name, String phoneNumber) {
            this.name = name;
            this.phoneNumber = phoneNumber;
        }
    }
}
