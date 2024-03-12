package com.research.protrike.MainFeats.Contacts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.research.protrike.Application.Protrike;
import com.research.protrike.CustomObjects.ContactsObject;
import com.research.protrike.DataManager.PaperDBHelper;
import com.research.protrike.HelperFunctions.CharacterCode;
import com.research.protrike.HelperFunctions.EditTextUtils;
import com.research.protrike.R;

import java.util.ArrayList;
import java.util.List;

public class NewContact extends AppCompatActivity {

    TextView header;
    TextInputEditText contactName, contactNumber, contactMessage;
    CardView insertLocation, insertTricycleNumber, save;

    String mode = "NEW";
    String originalNumber;
    List<String> numbers;
    Protrike protrike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);

        header = findViewById(R.id.header);
        contactName = findViewById(R.id.contact_name_input);
        contactNumber = findViewById(R.id.contact_number_input);
        contactMessage = findViewById(R.id.contact_message_input);
        insertLocation = findViewById(R.id.insert_location);
        insertTricycleNumber = findViewById(R.id.insert_tricycle_number);
        save = findViewById(R.id.save);

        protrike = Protrike.getInstance();
        numbers = protrike.getContactHolder().getNumbers();

        Intent intent = getIntent();
        if (intent.hasExtra("MODE")) {
            mode = "EDIT";
            header.setText("Edit Contact");
            contactName.setText(intent.getStringExtra("CONTACT_NAME"));
            contactNumber.setText(intent.getStringExtra("CONTACT_NUMBER"));
            originalNumber = intent.getStringExtra("CONTACT_NUMBER");
            contactMessage.setText(CharacterCode.messageEncode(intent.getStringExtra("CONTACT_MESSAGE")));
        }
        save.setOnClickListener(v -> saveContact());

        insertLocation.setOnClickListener(v -> {
            String message = CharacterCode.messageDecode(contactMessage.getText().toString());
            if (!message.endsWith(" ")){
                message += " ";
            }
            message += CharacterCode.LOCATION_DECODE;
            contactMessage.setText(CharacterCode.messageEncode(message));
            contactMessage.requestFocus();
            contactMessage.setSelection(contactMessage.getText().length());
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(contactMessage, InputMethodManager.SHOW_IMPLICIT);
        });
        insertTricycleNumber.setOnClickListener(v -> {
            String message = CharacterCode.messageDecode(contactMessage.getText().toString());
            if (!message.endsWith(" ")){
                message += " ";
            }
            message += CharacterCode.TRICYCLE_NUMBER_DECODE;
            contactMessage.setText(CharacterCode.messageEncode(message));
            contactMessage.requestFocus();
            contactMessage.setSelection(contactMessage.getText().length());
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(contactMessage, InputMethodManager.SHOW_IMPLICIT);
        });
    }

    private void saveContact() {
        String name = contactName.getText().toString().trim();
        String number = contactNumber.getText().toString().trim();
        String message = CharacterCode.messageDecode(contactMessage .getText().toString()).trim();

        if (name.equals("")){
            Toast.makeText(this, "Insert contact name.", Toast.LENGTH_SHORT).show();
            return;
        } else if (number.equals("")){
            Toast.makeText(this, "Insert contact number.", Toast.LENGTH_SHORT).show();
            return;
        } else if (message.equals("")){
            Toast.makeText(this, "Insert message.", Toast.LENGTH_SHORT).show();
            return;
        } else if (numbers.contains(number) && "NEW".equals(mode)){
            Toast.makeText(this, "Number already added! Please insert a new one when adding.", Toast.LENGTH_SHORT).show();
            return;
        }

        ContactsObject contactsObject = new ContactsObject(message, name, number);
        switch (mode) {
            case "NEW":
                PaperDBHelper.saveContact(contactsObject);
                protrike.getContactHolder().add(contactsObject);
                break;
            case "EDIT":
                Protrike.ContactHolder contactHolder = protrike.getContactHolder();
                int index = contactHolder.getIndexOfNumber(originalNumber);
                if (index == -1) {
                    PaperDBHelper.saveContact(contactsObject);
                    protrike.getContactHolder().add(contactsObject);
                    break;
                } else {
                    contactHolder.replace(index, contactsObject);
                    protrike.setContactHolder(contactHolder);
                }
                index = PaperDBHelper.getIndexFromNumber(originalNumber);
                if (index != -1) {
                    PaperDBHelper.saveContact(index, contactsObject);
                }
                break;
        }
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }


}