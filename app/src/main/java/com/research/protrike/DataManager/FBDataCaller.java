package com.research.protrike.DataManager;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.research.protrike.DataTypes.OperatorInfo;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;

public class FBDataCaller {
    public interface ReturnHandler {
        void returnObject(Object object);
    }

    public interface ReturnHandlerWithStatus {
        void returnObject(Object object, Boolean status);
    }

    public static boolean isInternetAvailable() {
        try {
            URL url = new URL("https://protrike-1ae0d-default-rtdb.asia-southeast1.firebasedatabase.app"); // check for the internet connection.
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Test");
            connection.setConnectTimeout(10000);
            connection.connect();
            connection.disconnect();
            try {
                return FirebaseDatabase.getInstance() != null; // check if firebase is reachable.
            } catch (IllegalStateException e) {
                System.out.println("Firebase no instance.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Firebase cannot be reached.");
            return false;
        }
    }

    public static void qrToTricycleNumber(Context context, String qrValue, ReturnHandlerWithStatus returnHandlerWithStatus) {
        FirebaseData fd = new FirebaseData();
        fd.retrieveData(context, "operator_information/", new FirebaseData.FirebaseDataCallback() {
            @Override
            public void onDataReceived(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String childKey = childSnapshot.getKey();
                    if (Objects.equals(childKey, qrValue)) {
                        returnHandlerWithStatus.returnObject(childKey, true);
                        return;
                    }
                }
                returnHandlerWithStatus.returnObject(null, false);
            }
        });
    }

    public static void tricycleNumberToOperatorInformation(Context context, String trikeNum, ReturnHandler returnHandler){
        FirebaseData fd = new FirebaseData();
        fd.retrieveData(context, String.format(Locale.getDefault(), "operator_information/%s", trikeNum), new FirebaseData.FirebaseDataCallback() {
            @Override
            public void onDataReceived(DataSnapshot dataSnapshot) {
                Object operatorNameObject = dataSnapshot.child("operator_name").getValue();
                Object addressObject = dataSnapshot.child("address").getValue();
                Object contactNumberObject = dataSnapshot.child("contact_number").getValue();
                if (operatorNameObject != null && addressObject != null && contactNumberObject != null) {
                    returnHandler.returnObject(new OperatorInfo(trikeNum,
                            operatorNameObject.toString(),
                            addressObject.toString(),
                            contactNumberObject.toString()));
                } else {
                    returnHandler.returnObject(null);
                }
            }
        });
    }
}
