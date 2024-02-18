package com.research.protrike.HelperFunctions;

public class CharacterCode {

    public static String LOCATION_CODE = "\uD83D\uDCCD";
    public static String TRICYCLE_NUMBER_CODE = "\uD83E\uDEAA";
    public static String LOCATION_DECODE = "@app:location";
    public static String TRICYCLE_NUMBER_DECODE = "@app:tricycle_number";
    public static String messageEncode(String message){
        return message.replace(LOCATION_DECODE, LOCATION_CODE)
                .replace(TRICYCLE_NUMBER_DECODE, TRICYCLE_NUMBER_CODE);
    }

    public static String messageDecode(String message){
        return message.replace(TRICYCLE_NUMBER_CODE, TRICYCLE_NUMBER_DECODE)
                .replace(LOCATION_CODE, LOCATION_DECODE);
    }
}
