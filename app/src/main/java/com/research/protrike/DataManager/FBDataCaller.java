package com.research.protrike.DataManager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.research.protrike.DataTypes.OperatorInfo;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class FBDataCaller {
    public interface ReturnHandler {
        void returnObject(@NonNull Object object);
    }

    public interface ReturnHandlerWithStatus {
        void returnObject(Object object, Boolean status);
    }

    public interface ReturnHandlerWithKey {
        void returnObject(String key, Object object);
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
        return nc != null && nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
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

    public static void tricycleNumberToOperatorInformation(Context context, String trikeNum, ReturnHandler returnHandler) {
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
                    returnHandler.returnObject(new OperatorInfo(null, null, null, null));
                }
            }
        });
    }

    public static void getLastUpdateTF(Context context, ReturnHandler returnHandler) {
        FirebaseData fd = new FirebaseData();
        fd.retrieveData(context, "tricycle_fare/last_update", new FirebaseData.FirebaseDataCallback() {
            @Override
            public void onDataReceived(DataSnapshot dataSnapshot) {
                Object lastUpdate = dataSnapshot.getValue();
                if (lastUpdate != null) {
                    returnHandler.returnObject(lastUpdate);
                }
            }
        });
    }

    public static void getLastUpdateContacts(Context context, ReturnHandler returnHandler) {
        FirebaseData fd = new FirebaseData();
        fd.retrieveData(context, "contacts/last_update", new FirebaseData.FirebaseDataCallback() {
            @Override
            public void onDataReceived(DataSnapshot dataSnapshot) {
                Object lastUpdate = dataSnapshot.getValue();
                if (lastUpdate != null) {
                    returnHandler.returnObject(lastUpdate);
                }
            }
        });
    }

    public static void getContacts(Context context, ReturnHandler returnHandler) {
        FirebaseData fd = new FirebaseData();
        fd.retrieveData(context, "contacts", new FirebaseData.FirebaseDataCallback() {
            @Override
            public void onDataReceived(DataSnapshot dataSnapshot) {
                returnHandler.returnObject(dataSnapshot);
            }
        });
    }

    public static class TFSnapshots {
        HashMap<TFSnapshotKey, DataSnapshot> dataSnapshotHashMap;
        public enum TFSnapshotKey {
            MIN_FARE_DIST,
            SUCCEEDING_DIST,
            DISCOUNTED_STARTING_FARE,
            DISCOUNTED_SUCCEEDING_FARE,
            NORMAL_STARTING_FARE,
            NORMAL_SUCCEEDING_FARE,
            THREE_TO_FIVE_STARTING_FARE,
            THREE_TO_FIVE_SUCCEEDING_FARE
        }

        public TFSnapshots() {
            dataSnapshotHashMap = new HashMap<>();
        }

        public DataSnapshot get(TFSnapshotKey key) {
            return dataSnapshotHashMap.get(key);
        }

        public void put(TFSnapshotKey key, DataSnapshot dataSnapshot){
            dataSnapshotHashMap.put(key, dataSnapshot);
        }
    }

    public static void getTF(Context context, ReturnHandler returnHandler) {
        FirebaseData fd = new FirebaseData();
        fd.retrieveData(context, "tricycle_fare", new FirebaseData.FirebaseDataCallback() {
            @Override
            public void onDataReceived(DataSnapshot dataSnapshot) {
                TFSnapshots dataSnapshots = new TFSnapshots();
                dataSnapshots.put(TFSnapshots.TFSnapshotKey.MIN_FARE_DIST, dataSnapshot.child("minimum_fare_distance"));
                dataSnapshots.put(TFSnapshots.TFSnapshotKey.SUCCEEDING_DIST, dataSnapshot.child("succeeding_distance"));
                dataSnapshots.put(TFSnapshots.TFSnapshotKey.DISCOUNTED_STARTING_FARE, dataSnapshot.child("discounted/starting_fare"));
                dataSnapshots.put(TFSnapshots.TFSnapshotKey.DISCOUNTED_SUCCEEDING_FARE, dataSnapshot.child("discounted/succeeding_fare"));
                dataSnapshots.put(TFSnapshots.TFSnapshotKey.NORMAL_STARTING_FARE, dataSnapshot.child("normal/starting_fare"));
                dataSnapshots.put(TFSnapshots.TFSnapshotKey.NORMAL_SUCCEEDING_FARE, dataSnapshot.child("normal/succeeding_fare"));
                dataSnapshots.put(TFSnapshots.TFSnapshotKey.THREE_TO_FIVE_STARTING_FARE, dataSnapshot.child("three_to_five_discount/starting_fare"));
                dataSnapshots.put(TFSnapshots.TFSnapshotKey.THREE_TO_FIVE_SUCCEEDING_FARE, dataSnapshot.child("three_to_five_discount/succeeding_fare"));
                returnHandler.returnObject(dataSnapshots);
            }
        });
    }
}
