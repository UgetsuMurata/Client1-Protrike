package com.research.protrike.Application;

import android.app.Application;

import com.google.android.gms.maps.model.LatLng;
import com.research.protrike.CustomObjects.TricycleFareObject;

import java.util.ArrayList;

import io.paperdb.Paper;

public class Protrike extends Application {

    private FareCounterBGP fareCounterBGP;
    private static Protrike instance;
    private Boolean hasInternet;
    private TricycleFareObject tricycleFareObject;

    @Override
    public void onCreate() {
        super.onCreate();
        Paper.init(this);
        fareCounterBGP = new FareCounterBGP();
        tricycleFareObject = new TricycleFareObject();
        instance = this;
    }

    public FareCounterBGP getFareCounterBGP() {
        return fareCounterBGP;
    }

    public void setFareCounterBGP(FareCounterBGP fareCounterBGP) {
        this.fareCounterBGP = fareCounterBGP;
    }

    public static Protrike getInstance() {
        return instance;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public Boolean hasInternet() {
        return hasInternet;
    }

    public void setHasInternet(Boolean hasInternet) {
        this.hasInternet = hasInternet;
    }

    public TricycleFareObject getTricycleFare(){
        return tricycleFareObject;
    }
    public void setTricycleFare(TricycleFareObject tricycleFareObject){
        this.tricycleFareObject = tricycleFareObject;
    }

    public static class FareCounterBGP {
        Float currentDistance = 0f;
        Float currentFare = 0f;
        ArrayList<LatLng> latLngStack;
        LatLng prevLatLng;

        public FareCounterBGP() {
            latLngStack = new ArrayList<>();
        }

        public void addDistance(Float distance) {
            currentDistance = currentDistance + distance;
        }

        public void addLatLngStack(LatLng latLng) {
            latLngStack.add(latLng);
            prevLatLng = latLng;
        }

        public LatLng getPrevLatLng() {
            return prevLatLng;
        }

        public Float getCurrentDistance() {
            return currentDistance;
        }

        public Float getCurrentFare() {
            return currentFare;
        }

        public void setCurrentFare(Float currentFare) {
            this.currentFare = currentFare;
        }

        public void reset() {
            currentDistance = 0f;
            latLngStack.clear();
            prevLatLng = null;
        }
    }
}
