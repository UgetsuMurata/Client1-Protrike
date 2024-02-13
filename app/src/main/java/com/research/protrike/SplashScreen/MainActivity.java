package com.research.protrike.SplashScreen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.research.protrike.Application.Protrike;
import com.research.protrike.CustomObjects.TricycleFareObject;
import com.research.protrike.CustomViews.ProtrikeLoadingBar;
import com.research.protrike.DataManager.FBDataCaller;
import com.research.protrike.DataManager.FBDataCaller.TFSnapshots;
import com.research.protrike.DataManager.FBDataCaller.TFSnapshots.TFSnapshotKey;
import com.research.protrike.DataManager.PaperDBHelper;
import com.research.protrike.DataManager.SharedPref;
import com.research.protrike.MainFeats.Dashboard;
import com.research.protrike.R;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {

    ProtrikeLoadingBar display;
    Integer maxProcesses = 10;
    Protrike protrike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.loading_display);
        protrike = Protrike.getInstance();

        display.setMax(maxProcesses);
        new Thread(() -> {
            AtomicInteger process_number = new AtomicInteger(0);
            for (int i = 0; i < maxProcesses; i++) {
                process_number.getAndIncrement();
                runOnUiThread(() -> {
                    display.setProgress(process_number.get());
                });
                switch (i) {
                    case 1:
                        Process1();
                        break;
                    case 2:
                        Process2();
                        break;
                    case 3:
                        Process3();
                        break;
                    default:
                        break;
                }
            }
            startActivity(new Intent(MainActivity.this, Dashboard.class));
            finish();
        }).start();
    }

    private void Process1() {
        protrike.setHasInternet(FBDataCaller.isInternetAvailable(this));
        if (!protrike.hasInternet()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Entering offline mode...", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void Process2() {
        if (protrike.hasInternet()) {
            final CountDownLatch latch = new CountDownLatch(2);
            FBDataCaller.getLastUpdateContacts(this, new FBDataCaller.ReturnHandler() {
                @Override
                public void returnObject(@NonNull Object object) {
                    String lastUpdated = SharedPref.readString(MainActivity.this, SharedPref.CONTACTS_LAST_UPDATE, "01/01/1994");
                    String onlineUpdate = object.toString();
                    if (!onlineUpdate.equals(lastUpdated)) {
                        // Get newly updated data
                        FBDataCaller.getContacts(MainActivity.this, new FBDataCaller.ReturnHandler() {
                            @Override
                            public void returnObject(@NonNull Object object) {
                                DataSnapshot dataSnapshot = (DataSnapshot) object;
                                Object LTOObject = dataSnapshot.child("LTO").getValue();
                                Object MDRRMOObject = dataSnapshot.child("MDRRMO").getValue();
                                Object PNPObject = dataSnapshot.child("PNP").getValue();
                                Object reportObject = dataSnapshot.child("report").getValue();
                                if (LTOObject != null) {
                                    PaperDBHelper.saveContactsLTO(LTOObject.toString());
                                }
                                if (MDRRMOObject != null) {
                                    PaperDBHelper.saveContactsMDRRMO(MDRRMOObject.toString());
                                }
                                if (PNPObject != null) {
                                    PaperDBHelper.saveContactsPNP(PNPObject.toString());
                                }
                                if (reportObject != null) {
                                    PaperDBHelper.saveContactsReport(reportObject.toString());
                                }
                                SharedPref.write(MainActivity.this, SharedPref.CONTACTS_LAST_UPDATE, onlineUpdate);
                                latch.countDown();
                            }
                        });
                    } else {
                        latch.countDown();
                    }
                }
            });
            FBDataCaller.getLastUpdateTF(this, new FBDataCaller.ReturnHandler() {
                @Override
                public void returnObject(@NonNull Object object) {
                    String lastUpdated = SharedPref.readString(MainActivity.this, SharedPref.TRICYCLE_FARE_LAST_UPDATE, "01/01/1994");
                    String onlineUpdate = object.toString();
                    if (!onlineUpdate.equals(lastUpdated)) {
                        // Get newly updated data
                        FBDataCaller.getTF(MainActivity.this, new FBDataCaller.ReturnHandler() {
                            @Override
                            public void returnObject(@NonNull Object object) {
                                TFSnapshots dataSnapshot = (TFSnapshots) object;
                                Object minFareDistObj = dataSnapshot.get(TFSnapshotKey.MIN_FARE_DIST).getValue();
                                Object succeedingDistObj = dataSnapshot.get(TFSnapshotKey.SUCCEEDING_DIST).getValue();
                                Object discStartingFareObj = dataSnapshot.get(TFSnapshotKey.DISCOUNTED_STARTING_FARE).getValue();
                                Object discSucceedingFareObj = dataSnapshot.get(TFSnapshotKey.DISCOUNTED_SUCCEEDING_FARE).getValue();
                                Object normStartingFareObj = dataSnapshot.get(TFSnapshotKey.NORMAL_STARTING_FARE).getValue();
                                Object normSucceedingFareObj = dataSnapshot.get(TFSnapshotKey.NORMAL_SUCCEEDING_FARE).getValue();
                                Object tToFStartingFareObj = dataSnapshot.get(TFSnapshotKey.THREE_TO_FIVE_STARTING_FARE).getValue();
                                Object tToFSucceedingFareObj = dataSnapshot.get(TFSnapshotKey.THREE_TO_FIVE_SUCCEEDING_FARE).getValue();
                                if (minFareDistObj != null) {
                                    PaperDBHelper.saveMinDist(Float.parseFloat(minFareDistObj.toString()));
                                }
                                if (succeedingDistObj != null) {
                                    PaperDBHelper.saveSucDist(Float.parseFloat(succeedingDistObj.toString()));
                                }
                                if (discStartingFareObj != null) {
                                    PaperDBHelper.saveDisStart(Float.parseFloat(discStartingFareObj.toString()));
                                }
                                if (discSucceedingFareObj != null) {
                                    PaperDBHelper.saveDisSuc(Float.parseFloat(discSucceedingFareObj.toString()));
                                }
                                if (normStartingFareObj != null) {
                                    PaperDBHelper.saveNorStart(Float.parseFloat(normStartingFareObj.toString()));
                                }
                                if (normSucceedingFareObj != null) {
                                    PaperDBHelper.saveNorSuc(Float.parseFloat(normSucceedingFareObj.toString()));
                                }
                                if (tToFStartingFareObj != null) {
                                    PaperDBHelper.saveTTFStart(Float.parseFloat(tToFStartingFareObj.toString()));
                                }
                                if (tToFSucceedingFareObj != null) {
                                    PaperDBHelper.saveTTFSuc(Float.parseFloat(tToFSucceedingFareObj.toString()));
                                }
                                SharedPref.write(MainActivity.this, SharedPref.TRICYCLE_FARE_LAST_UPDATE, onlineUpdate);
                                latch.countDown();
                            }
                        });
                    } else {
                        latch.countDown();
                    }
                }
            });
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void Process3() {
        // get data from PaperDB
        TricycleFareObject tricycleFareObject = new TricycleFareObject();

        boolean hasData = !"".equals(SharedPref.readString(this, SharedPref.TRICYCLE_FARE_LAST_UPDATE, ""));
        if (hasData) {
            tricycleFareObject.put(TricycleFareObject.TF.MIN_FARE_DIST, PaperDBHelper.getMinDist(2f));
            tricycleFareObject.put(TricycleFareObject.TF.SUCCEEDING_DIST, PaperDBHelper.getSucDist(1f));
            tricycleFareObject.put(TricycleFareObject.TF.DISCOUNTED_STARTING_FARE, PaperDBHelper.getDisStart(12f));
            tricycleFareObject.put(TricycleFareObject.TF.DISCOUNTED_SUCCEEDING_FARE, PaperDBHelper.getDisSuc(1.6f));
            tricycleFareObject.put(TricycleFareObject.TF.NORMAL_STARTING_FARE, PaperDBHelper.getNorStart(15f));
            tricycleFareObject.put(TricycleFareObject.TF.NORMAL_SUCCEEDING_FARE, PaperDBHelper.getNorSuc(2f));
            tricycleFareObject.put(TricycleFareObject.TF.THREE_TO_FIVE_STARTING_FARE, PaperDBHelper.getTTFStart(7f));
            tricycleFareObject.put(TricycleFareObject.TF.THREE_TO_FIVE_SUCCEEDING_FARE, PaperDBHelper.getSucDist(2f));
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Some features may not work until you connect to the internet.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        protrike.setTricycleFare(tricycleFareObject);
    }
}