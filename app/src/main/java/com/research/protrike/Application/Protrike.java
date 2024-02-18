package com.research.protrike.Application;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.research.protrike.CustomObjects.ContactsObject;
import com.research.protrike.CustomObjects.TricycleFareObject;
import com.research.protrike.DataManager.PaperDBHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class Protrike extends Application {

    private FareCounterBGP fareCounterBGP;
    private static Protrike instance;
    private Boolean hasInternet;
    private TricycleFareObject tricycleFareObject;

    private ContactHolder contactHolder;
    private List<String> defaultContactsList;

    @Override
    public void onCreate() {
        super.onCreate();
        Paper.init(this);
        fareCounterBGP = new FareCounterBGP();
        tricycleFareObject = new TricycleFareObject();
        contactHolder = new ContactHolder();
        defaultContactsList = new ArrayList<>();
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

    public TricycleFareObject getTricycleFare() {
        return tricycleFareObject;
    }

    public void setTricycleFare(TricycleFareObject tricycleFareObject) {
        this.tricycleFareObject = tricycleFareObject;
    }

    public ContactHolder getContactHolder() {
        return contactHolder;
    }

    public void setContactHolder(ContactHolder contactHolder) {
        this.contactHolder = contactHolder;
    }

    public List<String> getDefaultContactsList() {
        return defaultContactsList;
    }

    public void setDefaultContactsList(List<String> defaultContactsList) {
        this.defaultContactsList = defaultContactsList;
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

    public static class ContactHolder {
        private List<ContactsObject> contactsObjects;

        public ContactHolder(List<ContactsObject> contactsObjects) {
            this.contactsObjects = contactsObjects;
        }

        public ContactHolder(ContactHolder contactHolder) {
            this.contactsObjects = new ArrayList<>(contactHolder.getRaw());
        }

        public ContactHolder() {
            contactsObjects = new ArrayList<>();
        }

        public void add(ContactsObject contactsObject) {
            contactsObjects.add(contactsObject);
        }

        public void remove(ContactsObject contactsObject) {
            contactsObjects.remove(contactsObject);
        }

        public void remove(int index) {
            contactsObjects.remove(index);
        }

        public int size() {
            return contactsObjects.size();
        }

        public void clear() {
            contactsObjects.clear();
        }

        public void addAll(ContactHolder contactHolder1) {
            contactsObjects.addAll(contactHolder1.getRaw());
        }

        public List<ContactsObject> getRaw() {
            return contactsObjects;
        }

        public ContactsObject get(int index) {
            return contactsObjects.get(index);
        }

        public int getIndexOfNumber(String number) {
            int index = 0;
            for (ContactsObject contactsObject : contactsObjects) {
                if (contactsObject.getNumber().equals(number)) return index;
                index++;
            }
            return -1;
        }

        public void replace(int index, ContactsObject contactsObject) {
            contactsObjects.remove(index);
            contactsObjects.add(index, contactsObject);
        }

        public List<String> getNumbers() {
            List<String> numbers = new ArrayList<>();
            for (ContactsObject contactsObject : contactsObjects) {
                numbers.add(contactsObject.getNumber());
            }
            return numbers;
        }

        @NonNull
        @Override
        public String toString() {
            String generatedString = "";
            for (int i = 0; i < size(); i++) {
                generatedString = String.format(Locale.getDefault(), "%s   %d:{name:%s, number:%s, message:%s}", generatedString, i, get(i).getName(), get(i).getNumber(), get(i).getMessage());
            }
            return generatedString.trim();
        }
    }

}
