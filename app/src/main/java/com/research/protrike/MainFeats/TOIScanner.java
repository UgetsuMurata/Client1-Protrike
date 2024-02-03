package com.research.protrike.MainFeats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.zxing.Result;
import com.research.protrike.DataManager.FBDataCaller;
import com.research.protrike.DataManager.FirebaseData;
import com.research.protrike.DataTypes.OperatorInfo;
import com.research.protrike.R;

import java.util.Objects;

public class TOIScanner extends AppCompatActivity {
    CodeScannerView scannerView;
    CodeScanner codeScanner;
    TextView scannerLabel;

    Integer CAMERA_PERMISSION_REQUEST_CODE = 1;
    Boolean scannerState = false;
    String tricycleNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toiscanner);

        scannerView = findViewById(R.id.qr_scanner);
        scannerLabel = findViewById(R.id.qr_results);

        scannerLabel.setText("Status: Scanning...");
        scannerLabel.setTextColor(ContextCompat.getColor(TOIScanner.this, android.R.color.black));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else setupScanner();
    }

    private void setupScanner() {
        scannerState = true;
        codeScanner = new CodeScanner(this, scannerView);
        scannerView.setOnClickListener(view -> codeScanner.startPreview());
        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (FBDataCaller.isInternetAvailable()){
                            FBDataCaller.qrToTricycleNumber(TOIScanner.this, result.toString(), new FBDataCaller.ReturnHandlerWithStatus() {
                                @Override
                                public void returnObject(Object object, Boolean status) {
                                    tricycleNumber = object.toString();
                                    Handler handler = new Handler();
                                    if (status) {
                                        scannerLabel.setText("Status: Success!");
                                        scannerLabel.setTextColor(ContextCompat.getColor(TOIScanner.this, R.color.blue));
                                        handler.postDelayed(() -> {
                                            Intent returnIntent = new Intent(TOIScanner.this, Dashboard.class);
                                            returnIntent.putExtra("TRICYCLE_NUMBER", tricycleNumber);
                                            setResult(Activity.RESULT_OK, returnIntent);
                                            finish();
                                        }, 500);
                                    } else {
                                        scannerLabel.setText("Status: Failed");
                                        scannerLabel.setTextColor(ContextCompat.getColor(TOIScanner.this, R.color.red_error));
                                        handler.postDelayed(() -> {
                                            codeScanner.startPreview();
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    scannerLabel.setText("Status: Scanning...");
                                                    scannerLabel.setTextColor(ContextCompat.getColor(TOIScanner.this, android.R.color.black));
                                                }
                                            });
                                        }, 1000);
                                    }
                                }
                            });
                        } else {
                            scannerLabel.setText("Cannot contact Firebase at the moment.");
                            scannerLabel.setTextColor(ContextCompat.getColor(TOIScanner.this, R.color.red_error));
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE & resultCode == RESULT_OK) {
            if (scannerState) codeScanner.startPreview();
            setupScanner();
        } else {
            Toast.makeText(this, "Camera permission is required to access this feature.", Toast.LENGTH_SHORT).show();
            getOnBackPressedDispatcher().onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (scannerState) codeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        if (scannerState) codeScanner.releaseResources();
        super.onPause();
    }
}