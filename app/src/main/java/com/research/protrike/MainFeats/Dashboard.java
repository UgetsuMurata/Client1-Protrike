package com.research.protrike.MainFeats;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.maps.model.LatLng;
import com.research.protrike.Application.Protrike;
import com.research.protrike.DataManager.FBDataCaller;
import com.research.protrike.DataTypes.OperatorInfo;
import com.research.protrike.HelperFunctions.LatLngProcessing;
import com.research.protrike.HelperFunctions.PaymentProcessing;
import com.research.protrike.R;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Dashboard extends AppCompatActivity {
    ImageView qrScanner;

    TextView tricycleNumber, operatorName, address, contactNumber;
    TextView currentDistance, currentFare, startedAt, endedAt;
    CardView fareCounterButton;
    TextView fareCounterButtonText, fareCounterButtonDescription;

    Protrike protrike;
    Protrike.FareCounterBGP fareCounterBGP;

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
                                        public void returnObject(@NonNull Object object) {
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
        startedAt = findViewById(R.id.started_at);
        endedAt = findViewById(R.id.ended_at);
        fareCounterButton = findViewById(R.id.fare_counter_button);
        fareCounterButtonText = findViewById(R.id.fare_counter_button_text);
        fareCounterButtonDescription = findViewById(R.id.button_description);

        protrike = Protrike.getInstance();
        fareCounterBGP = protrike.getFareCounterBGP();

        fareCounterButtonText.setText("Start");
        fareCounterButtonDescription.setText("Click “Start” immediately once the tricycle leaves.");
        fareButtonMode = FareButtonModes.start;

        LatLngProcessing.checkForPermissions(this);

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
                        updateLiveCounter();
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
        Date currentTime = Calendar.getInstance().getTime();
        switch (fareButtonMode) {
            case start:
                startedAt.setText(currentTime.toString());
                return true;
            case end:
                endedAt.setText(currentTime.toString());
                return true;
            case pay:
                fareCounterBGP.reset();
                startedAt.setText("--");
                endedAt.setText("--");
                break;
        }

        return false;
    }

    private void updateLiveCounter() {
        LatLngProcessing.getCurrentLocation(this, new LatLngProcessing.LocationCallback() {
            @Override
            public void onLocationChanged(@NonNull LatLng latLng) {
                float distance = 0f;
                float costing = 0f;
                if (fareCounterBGP.getPrevLatLng() != null) {
                    distance = LatLngProcessing.getDistance(latLng, fareCounterBGP.getPrevLatLng());
                    costing = PaymentProcessing.distanceToCosting(distance, PaymentProcessing.Discount.STUDENT);
                }
                fareCounterBGP.addLatLngStack(latLng);
                fareCounterBGP.addDistance(distance);
                fareCounterBGP.setCurrentFare(costing);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (fareCounterBGP.getCurrentDistance() > 1000) {
                            currentDistance.setText(String.format(Locale.getDefault(), "%.1fkm", fareCounterBGP.getCurrentDistance() / 1000));
                        } else if (fareCounterBGP.getCurrentDistance() > 100){
                            currentDistance.setText(String.format(Locale.getDefault(), "%dm", (int) (Math.ceil(fareCounterBGP.getCurrentDistance()/100.0)*100)));
                        } else {
                            currentDistance.setText(String.format(Locale.getDefault(), "%dm", Math.round(fareCounterBGP.getCurrentDistance())));
                        }
                        currentFare.setText(String.format(Locale.getDefault(), "₱ %.2f", fareCounterBGP.getCurrentFare()));
                    }
                });
            }
        });
    }

    private void changeDisplayedData(OperatorInfo operatorInfo) {
        if (operatorInfo.getTricycleNumber() != null) {
            tricycleNumber.setText(operatorInfo.getTricycleNumber());
            operatorName.setText(operatorInfo.getOperatorName());
            address.setText(operatorInfo.getAddress());
            contactNumber.setText(operatorInfo.getContactNumber());
        }
    }
}