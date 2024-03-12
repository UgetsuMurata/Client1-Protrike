package com.research.protrike.MainFeats.Contacts;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.os.Bundle;

import android.Manifest;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.research.protrike.Adapters.ContactsAdapter;
import com.research.protrike.Application.Protrike;
import com.research.protrike.Application.Protrike.ContactHolder;
import com.research.protrike.CustomObjects.ContactsObject;
import com.research.protrike.DataManager.PaperDBHelper;
import com.research.protrike.HelperFunctions.LatLngProcessing;
import com.research.protrike.MainFeats.TOIScanner;
import com.research.protrike.R;

public class Contacts extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private RecyclerView contactsView;
    private Protrike protrike;
    private ContactHolder contactHolder;
    private ContactsAdapter contactsAdapter;
    private CardView addContact;
    private final ActivityResultLauncher<Intent> qrLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent == null) return;
                    String message = intent.getStringExtra("MESSAGE");
                    String number = intent.getStringExtra("NUMBER");
                    String name = intent.getStringExtra("NAME");
                    sendSMS(number, message, name);
                }
            }
    );

    private final ActivityResultLauncher<Intent> newContactLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    contactsAdapter.notifyDataSetChanged();
                }
            }
    );

    private final ActivityResultLauncher<Intent> editContactLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    contactsAdapter.notifyDataSetChanged();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        contactsView = findViewById(R.id.contacts);
        addContact = findViewById(R.id.add_contact);

        protrike = Protrike.getInstance();
        contactHolder = protrike.getContactHolder();

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        contactsAdapter = new ContactsAdapter(this, contactHolder, protrike.getDefaultContactsList());
        contactsView.setLayoutManager(llm);
        contactsView.setAdapter(contactsAdapter);
        checkForPermissions();

        contactsAdapter.onContactCallback(new ContactsAdapter.ContactsCallback() {
            @Override
            public void SendMessage(String mName, String mNumber, String mMessage) {
                final String[] message = {mMessage};
                final String number = mNumber;
                final String name = mName;
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (message[0].contains("@app:location")) {
                                LatLngProcessing.getCurrentLocation(Contacts.this, new LatLngProcessing.LocationReturn() {
                                    @Override
                                    public void LatLngReturn(LatLng latLng) {
                                        if (latLng != null) {
                                            String newMessage = String.format("www.google.com/maps/search/?api=1&query=%s,%s", latLng.latitude, latLng.longitude);
                                            message[0] = message[0].replace("@app:location", newMessage);
                                        }
                                        if (message[0].contains("@app:tricycle_number")) {
                                            Intent intent = new Intent(getApplicationContext(), TOIScanner.class);
                                            intent.putExtra("FROM_CONTACTS", true);
                                            intent.putExtra("MESSAGE", message[0]);
                                            intent.putExtra("NUMBER", number);
                                            intent.putExtra("NAME", name);
                                            qrLauncher.launch(intent);
                                        } else { // if the message doesn't contain tricycle number, it should send.
                                            sendSMS(number, message[0], name);
                                        }
                                    }
                                });
                            } else {
                                if (message[0].contains("@app:tricycle_number")) {
                                    Intent intent = new Intent(getApplicationContext(), TOIScanner.class);
                                    intent.putExtra("FROM_CONTACTS", true);
                                    intent.putExtra("MESSAGE", message[0]);
                                    intent.putExtra("NUMBER", number);
                                    intent.putExtra("NAME", name);
                                    qrLauncher.launch(intent);
                                } else { // if the message doesn't contain tricycle number, it should send.
                                    sendSMS(number, message[0], name);
                                }
                            }
                        }
                    });
                    thread.run();
                } else {
                    Toast.makeText(getApplicationContext(), "You need to grant permissions to access this feature.", Toast.LENGTH_LONG).show();
                    checkForPermissions();
                }
            }

            @Override
            public void deleted(ContactsObject contactsObject) {
                contactHolder.remove(contactsObject);
                PaperDBHelper.removeContact(contactsObject);
                contactsAdapter.notifyDataSetChanged();
            }

            @Override
            public void edit(Intent intent) {
                editContactLauncher.launch(intent);
            }
        });
        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newContactLauncher.launch(new Intent(getApplicationContext(), NewContact.class));
            }
        });
    }

    protected void checkForPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
        }
    }

    protected void sendSMS(String number, String message, String name) {

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, message, null, null);
            Toast.makeText(getApplicationContext(),
                    String.format("Message sent to %s", name),
                    Toast.LENGTH_SHORT).show();
        } catch (android.content.ActivityNotFoundException ex) {
            Log.e("MESSAGING", ex.getMessage());
            Toast.makeText(getApplicationContext(),
                    "Message not sent. Please try again later.",
                    Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Log.e("MESSAGING", ex.getMessage());
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