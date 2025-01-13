package com.hemaya.mssdemo.model.UserModel;

public class User {

    private int id;
    private String serialNumber;
    private String name;
    private String storageName;
    private boolean isUsed;
    private boolean isEdit = false;
    private int biometricId ;
    private boolean isDetectionFingerPrint;
    private boolean isFirstLoginToEnableFingerPrint;

    public User(int id,  String serialNumber, String name,String storageName, boolean isUsed, int biometricId,boolean isDetectionFingerPrint,boolean isFirstLoginToEnableFingerPrint) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.name = name;
        this.storageName = storageName;
        this.isUsed = isUsed;
        this.biometricId = biometricId;
        this.isDetectionFingerPrint = isDetectionFingerPrint;
        this.isFirstLoginToEnableFingerPrint = isFirstLoginToEnableFingerPrint;
    }

    public boolean isFirstLoginToEnableFingerPrint() {
        return isFirstLoginToEnableFingerPrint;
    }

    public void setFirstLoginToEnableFingerPrint(boolean firstLoginToEnableFingerPrint) {
        isFirstLoginToEnableFingerPrint = firstLoginToEnableFingerPrint;
    }

    public boolean isDetectionFingerPrint() {
        return isDetectionFingerPrint;
    }
    public void setDetectionFingerPrint(boolean detectionFingerPrint) {
        isDetectionFingerPrint = detectionFingerPrint;
    }

    public String getStorageName() {
        return storageName;
    }
    public int getId() {
        return id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getName() {
        return name;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public boolean isEdit() {
        return isEdit;
    }


    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public int isUsingPin() {
        return biometricId;
    }

    public void setUsingPin(int biometricId) {
        biometricId = biometricId;
    }

    public void setStorageName(String storageName) {
        this.storageName = storageName;
    }

    public void setBiometricId(int biometricId) {
        this.biometricId = biometricId;
    }

    public int getBiometricId() {
        return biometricId;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", serialNumber='" + serialNumber + '\'' +
                ", name='" + name + '\'' +
                ", storageName='" + storageName + '\'' +
                ", isUsed=" + isUsed +
                ", isEdit=" + isEdit +
                ", biometricId=" + biometricId +
                ", isDetectionFingerPrint=" + isDetectionFingerPrint +
                ", isFirstLoginToEnableFingerPrint=" + isFirstLoginToEnableFingerPrint +
                '}';
    }
}
