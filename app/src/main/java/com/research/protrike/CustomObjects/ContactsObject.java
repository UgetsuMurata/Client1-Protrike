package com.research.protrike.CustomObjects;

public class ContactsObject {
    String message;
    String name;
    String number;

    public ContactsObject(String message, String name, String number) {
        this.message = message;
        this.name = name;
        this.number = number;
    }

    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }
}
