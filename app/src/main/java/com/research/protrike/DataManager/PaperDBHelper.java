package com.research.protrike.DataManager;

import com.research.protrike.Application.Protrike;
import com.research.protrike.Application.Protrike.ContactHolder;
import com.research.protrike.CustomObjects.ContactsObject;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class PaperDBHelper {
    public static class Contacts {
        public static String MDRRMO = "Contacts_MDRRMO";
        public static String PNP = "Contacts_PNP";
        public static String report = "Contacts_report";

        public static void save(String key, ContactsObject contactsObject) {
            Paper.book().write(key + "_name", contactsObject.getName());
            Paper.book().write(key + "_number", contactsObject.getNumber());
            Paper.book().write(key + "_message", contactsObject.getMessage());
        }

        public static ContactsObject get(String key) {
            return new ContactsObject(Paper.book().read(key + "_message", null),
                    Paper.book().read(key + "_name", null),
                    Paper.book().read(key + "_number", null));
        }
    }

    public static class TricycleFare {
        public static String DiscountedStart = "TF_discounted_start";
        public static String DiscountedSucceeding = "TF_discounted_succeeding";
        public static String NormalStart = "TF_normal_start";
        public static String NormalSucceeding = "TF_normal_succeeding";
        public static String MinimumDistance = "TF_min_dist";
        public static String SucceedingDistance = "TF_succeeding_distance";

        public static void save(String key, Float value) {
            Paper.book().write(key, value);
        }

        public static Float get(String key, Float defaultValue) {
            return Paper.book().read(key, defaultValue);
        }
    }

    public static class ContactsStorage {
        public static String count = "ContactsStorage_count";
        public static String startingKeyName = "ContactStorage_name";
        public static String startingKeyMessage = "ContactStorage_message";
        public static String startingKeyNumber = "ContactStorage_number";

        public static void save(ContactsObject contactsObject) {
            int newSuffix = getCount(-1) + 1;
            Paper.book().write(startingKeyName + newSuffix, contactsObject.getName());
            Paper.book().write(startingKeyMessage + newSuffix, contactsObject.getMessage());
            Paper.book().write(startingKeyNumber + newSuffix, contactsObject.getNumber());
            Paper.book().write(count, newSuffix);
        }

        public static void save(int index, ContactsObject contactsObject) {
            Paper.book().write(startingKeyName + index, contactsObject.getName());
            Paper.book().write(startingKeyMessage + index, contactsObject.getMessage());
            Paper.book().write(startingKeyNumber + index, contactsObject.getNumber());
        }

        public static int getIndexFromNumber(String number) {
            for (int i = 0; i < ContactsStorage.getCount(-1); i++) {
                if (get(i).getNumber().equals(number)) {
                    return i;
                }
            }
            return -1;
        }

        public static ContactsObject get(Integer key) {
            String name = Paper.book().read(startingKeyName + key, "");
            String message = Paper.book().read(startingKeyMessage + key, "");
            String number = Paper.book().read(startingKeyNumber + key, "");
            return new ContactsObject(message, name, number);
        }

        public static Integer getCount(Integer defaultValue) {
            return Paper.book().read(count, defaultValue);
        }
    }

    public static void saveContactsMDRRMO(ContactsObject contactsObject) {
        Contacts.save(Contacts.MDRRMO, contactsObject);
    }

    public static void saveContactsPNP(ContactsObject contactsObject) {
        Contacts.save(Contacts.PNP, contactsObject);
    }

    public static void saveContactsReport(ContactsObject contactsObject) {
        Contacts.save(Contacts.report, contactsObject);
    }

    public static void saveMinDist(Float value) {
        TricycleFare.save(TricycleFare.MinimumDistance, value);
    }

    public static void saveSucDist(Float value) {
        TricycleFare.save(TricycleFare.SucceedingDistance, value);
    }

    public static void saveDisStart(Float value) {
        TricycleFare.save(TricycleFare.DiscountedStart, value);
    }

    public static void saveDisSuc(Float value) {
        TricycleFare.save(TricycleFare.DiscountedSucceeding, value);
    }

    public static void saveNorStart(Float value) {
        TricycleFare.save(TricycleFare.NormalStart, value);
    }

    public static void saveNorSuc(Float value) {
        TricycleFare.save(TricycleFare.NormalSucceeding, value);
    }

    public static Float getMinDist(Float value) {
        return TricycleFare.get(TricycleFare.MinimumDistance, value);
    }

    public static Float getSucDist(Float value) {
        return TricycleFare.get(TricycleFare.SucceedingDistance, value);
    }

    public static Float getDisStart(Float value) {
        return TricycleFare.get(TricycleFare.DiscountedStart, value);
    }

    public static Float getDisSuc(Float value) {
        return TricycleFare.get(TricycleFare.DiscountedSucceeding, value);
    }

    public static Float getNorStart(Float value) {
        return TricycleFare.get(TricycleFare.NormalStart, value);
    }

    public static Float getNorSuc(Float value) {
        return TricycleFare.get(TricycleFare.NormalSucceeding, value);
    }

    public static ContactHolder getContactHolder() {
        ContactHolder contactHolder = new ContactHolder();
        contactHolder.add(Contacts.get(Contacts.PNP));
        contactHolder.add(Contacts.get(Contacts.MDRRMO));
        contactHolder.add(Contacts.get(Contacts.report));

        List<String> defaultContactsList = new ArrayList<>();
        defaultContactsList.add(Contacts.get(Contacts.PNP).getName());
        defaultContactsList.add(Contacts.get(Contacts.MDRRMO).getName());
        defaultContactsList.add(Contacts.get(Contacts.report).getName());
        Protrike protrike = Protrike.getInstance();
        protrike.setDefaultContactsList(defaultContactsList);

        for (int i = 0; i < ContactsStorage.getCount(-1) + 1; i++) {
            contactHolder.add(ContactsStorage.get(i));
        }
        return contactHolder;
    }

    public static void saveContact(ContactsObject contactsObject){
        ContactsStorage.save(contactsObject);
    }

    public static void saveContact(int index, ContactsObject contactsObject){
        ContactsStorage.save(index, contactsObject);
    }

    public static int getIndexFromNumber(String number){
        return ContactsStorage.getIndexFromNumber(number);
    }
}
