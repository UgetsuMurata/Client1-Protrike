package com.research.protrike.MainFeats.Contacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.os.Bundle;

import android.Manifest;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.research.protrike.Adapters.ContactsAdapter;
import com.research.protrike.Application.Protrike;
import com.research.protrike.Application.Protrike.ContactHolder;
import com.research.protrike.HelperFunctions.LatLngProcessing;
import com.research.protrike.R;

public class Contacts extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private RecyclerView contactsView;
    private Protrike protrike;
    private ContactHolder contactHolder;
    private ContactsAdapter contactsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        contactsView = findViewById(R.id.contacts);

        protrike = Protrike.getInstance();
        contactHolder = protrike.getContactHolder();

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        contactsAdapter = new ContactsAdapter(this, contactHolder, protrike.getDefaultContactsList(), new ContactsAdapter.MessageCallback() {
            @Override
            public void SendMessage(String number, String message) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    if (message.contains("@app:location")) {
                        LatLng latLng = LatLngProcessing.getCurrentLocation(Contacts.this);
                        if (latLng != null) {
                            String newMessage = String.format("https://www.google.com/maps/search/?api=1&query=%s,%s", latLng.latitude, latLng.longitude);
                            message = message.replace("@app:location", newMessage);
                            smsManager.sendTextMessage(number, null, message, null, null);
                        }
                    } else if (message.contains("@app:tricycle_number")) {
                        // TODO: getting tricycle number from here.

                    } else {
                        smsManager.sendTextMessage(number, null, message, null, null);
                    }
                    Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "You need to grant permissions to access this feature.", Toast.LENGTH_LONG).show();
                }
            }
        });
        contactsView.setLayoutManager(llm);
        contactsView.setAdapter(contactsAdapter);
        checkForPermissions();
    }

    protected void checkForPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(getApplicationContext(), "SMS permission denied.", Toast.LENGTH_LONG).show();
            }
        }
    }
}