package com.research.protrike.DataTypes;

public class OperatorInfo {
    private final String tricycleNumber;
    private final String operatorName;
    private final String address;
    private final String contactNumber;

    public OperatorInfo(String tricycleNumber, String operatorName, String address, String contactNumber) {
        this.tricycleNumber = tricycleNumber;
        this.operatorName = operatorName;
        this.address = address;
        this.contactNumber = contactNumber;
    }

    public String getTricycleNumber() {
        return tricycleNumber;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public String getAddress() {
        return address;
    }

    public String getContactNumber() {
        return contactNumber;
    }
}
