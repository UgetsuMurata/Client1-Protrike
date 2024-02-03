package com.research.protrike.MainFeats;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.maps.model.LatLng;
import com.research.protrike.DataManager.FBDataCaller;
import com.research.protrike.DataTypes.OperatorInfo;
import com.research.protrike.HelperFunctions.LatLangProcessing;
import com.research.protrike.R;

public class Dashboard extends AppCompatActivity {
    ImageView qrScanner;

    TextView tricycleNumber, operatorName, address, contactNumber;
    TextView currentDistance, currentFare, departedFrom, arrivedAt;
    CardView fareCounterButton;
    TextView fareCounterButtonText, fareCounterButtonDescription;

    private enum FareButtonModes {
        start, end, pay
    }

    FareButtonModes fareButtonMode;

    private final ActivityResultLauncher<Intent> scanQR = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        if (FBDataCaller.isInternetAvailable()) {
                            FBDataCaller.tricycleNumberToOperatorInformation(
                                    this,
                                    intent.getStringExtra("TRICYCLE_NUMBER"),
                                    new FBDataCaller.ReturnHandler() {
                                        @Override
                                        public void returnObject(Object object) {
                                            changeDisplayedData((OperatorInfo) object);
                                        }
                                    }
                            );
                        } else {
                            Toast.makeText(this, "Check your internet connection and try again.", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        qrScanner = findViewById(R.id.scan);
        tricycleNumber = findViewById(R.id.tricycle_number);
        operatorName = findViewById(R.id.operator_name);
        address = findViewById(R.id.address);
        contactNumber = findViewById(R.id.contact_number);
        currentDistance = findViewById(R.id.current_distance);
        currentFare = findViewById(R.id.current_fare);
        departedFrom = findViewById(R.id.departed_from);
        arrivedAt = findViewById(R.id.arrived_at);
        fareCounterButton = findViewById(R.id.fare_counter_button);
        fareCounterButtonText = findViewById(R.id.fare_counter_button_text);
        fareCounterButtonDescription = findViewById(R.id.button_description);

        fareCounterButtonText.setText("Start");
        fareCounterButtonDescription.setText("Click “Start” immediately once the tricycle leaves.");
        fareButtonMode = FareButtonModes.start;

        LatLangProcessing.checkForPermissions(this);

        qrScanner.setOnClickListener(view -> {
            Intent intent = new Intent(this, TOIScanner.class);
            scanQR.launch(intent);
        });

        fareCounterButton.setOnClickListener(view -> {
            switch (fareButtonMode) {
                case start:
                    if (updateFareCounter()) {
                        fareCounterButtonText.setText("End");
                        fareCounterButtonDescription.setText("Click “End” when you reached your destination");
                        fareButtonMode = FareButtonModes.end;
                    } else {
                        Toast.makeText(Dashboard.this, "Cannot receive GPS signal. Try again later.", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case end:
                    if (updateFareCounter()) {
                        fareCounterButtonText.setText("Pay");
                        fareCounterButtonDescription.setText("Click “Pay” when you have paid your fare.");
                        fareButtonMode = FareButtonModes.pay;
                    } else {
                        Toast.makeText(Dashboard.this, "Cannot receive GPS signal. Try again later.", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case pay:
                    if (updateFareCounter()) {
                        fareCounterButtonText.setText("Start");
                        fareCounterButtonDescription.setText("Click “Start” immediately once the tricycle leaves.");
                        fareButtonMode = FareButtonModes.start;
                    } else {
                        Toast.makeText(Dashboard.this, "Cannot receive GPS signal. Try again later.", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        });
    }

    private boolean updateFareCounter() {
        LatLng loc = LatLangProcessing.getPhoneLocation(this);
        if (loc != null) {
            switch (fareButtonMode) {
                case start:
                    departedFrom.setText(LatLangProcessing.getAddress(loc.latitude, loc.longitude, this));
                    return true;
                case end:
                    arrivedAt.setText(LatLangProcessing.getAddress(loc.latitude, loc.longitude, this));
                    return true;
                case pay:
                    departedFrom.setText("--");
                    arrivedAt.setText("--");
                    break;
            }

        }
        return false;
    }

    private void changeDisplayedData(OperatorInfo operatorInfo) {
        tricycleNumber.setText(operatorInfo.getTricycleNumber());
        operatorName.setText(operatorInfo.getOperatorName());
        address.setText(operatorInfo.getAddress());
        contactNumber.setText(operatorInfo.getContactNumber());
    }
}