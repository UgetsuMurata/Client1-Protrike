package com.research.protrike.DataManager;

import io.paperdb.Paper;

public class PaperDBHelper {
    public static class Contacts {
        public static String LTO = "Contacts_LTO";
        public static String MDRRMO = "Contacts_MDRRMO";
        public static String PNP = "Contacts_PNP";
        public static String report = "Contacts_report";

        public static void save(String key, String value) {
            Paper.book().write(key, value);
        }
        public static String get(String key, String defaultValue){
            return Paper.book().read(key, defaultValue);
        }
    }

    public static class TricycleFare {
        public static String DiscountedStart = "TF_discounted_start";
        public static String DiscountedSucceeding = "TF_discounted_succeeding";
        public static String NormalStart = "TF_normal_start";
        public static String NormalSucceeding = "TF_normal_succeeding";
        public static String ThreeToFiveStart = "TF_three_to_five_start";
        public static String ThreeToFiveSucceeding = "TF_three_to_five_succeeding";
        public static String MinimumDistance = "TF_min_dist";
        public static String SucceedingDistance = "TF_succeeding_distance";

        public static void save(String key, Float value) {
            Paper.book().write(key, value);
        }
        public static Float get(String key, Float defaultValue){
            return Paper.book().read(key, defaultValue);
        }
    }

    public static void saveContactsLTO(String value) {
        Contacts.save(Contacts.LTO, value);
    }

    public static void saveContactsMDRRMO(String value) {
        Contacts.save(Contacts.MDRRMO, value);
    }

    public static void saveContactsPNP(String value) {
        Contacts.save(Contacts.PNP, value);
    }

    public static void saveContactsReport(String value) {
        Contacts.save(Contacts.report, value);
    }
    public static String getContactsLTO(String value) {
        return Contacts.get(Contacts.LTO, value);
    }

    public static String getContactsMDRRMO(String value) {
        return Contacts.get(Contacts.MDRRMO, value);
    }

    public static String getContactsPNP(String value) {
        return Contacts.get(Contacts.PNP, value);
    }

    public static String getContactsReport(String value) {
        return Contacts.get(Contacts.report, value);
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

    public static void saveTTFStart(Float value) {
        TricycleFare.save(TricycleFare.ThreeToFiveStart, value);
    }

    public static void saveTTFSuc(Float value) {
        TricycleFare.save(TricycleFare.ThreeToFiveSucceeding, value);
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

    public static Float getTTFStart(Float value) {
        return TricycleFare.get(TricycleFare.ThreeToFiveStart, value);
    }

    public static Float getTTFSuc(Float value) {
        return TricycleFare.get(TricycleFare.ThreeToFiveSucceeding, value);
    }
}
