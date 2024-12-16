package com.hemaya.mssdemo.model;

import com.hemaya.mssdemo.model.activationData.Response.ActivationDataResponse;
import com.hemaya.mssdemo.model.ephemeral.response.DSAPPSRPEphemeralResponse;
import com.vasco.digipass.sdk.DigipassSDKConstants;
import com.vasco.digipass.sdk.responses.MultiDeviceLicenseActivationResponse;
import com.vasco.dsapp.client.responses.SRPClientEphemeralKeyResponse;
import com.vasco.dsapp.client.responses.SRPSessionKeyResponse;
import com.vasco.message.client.CredentialsData;


public class ActivationModel {

    public static ActivationModel instance;

    String name;
    String scannedImageData;
    String codeFormated;
    CredentialsData credentialsData;

    SRPClientEphemeralKeyResponse srpClientEphemeralKeyResponse;
    DSAPPSRPEphemeralResponse dsappsrpEphemeralResponse;
    SRPSessionKeyResponse srpSessionKeyResponse;

    ActivationDataResponse activationDataResponse;
    byte[] encryptionKey;

    MultiDeviceLicenseActivationResponse multiDeviceLicenseActivationResponse;
    int clientServerTimeShift = 0;
    byte jailbreakStatus = DigipassSDKConstants.JAILBREAK_STATUS_NA;
    int cryptoApplicationIndex = DigipassSDKConstants.CRYPTO_APPLICATION_INDEX_APP_4;
    String platformFingerprint;
    String storageName;

    String instanceActivationMessage;
    StringBuilder pinBuilder;

    String password;
    String signature;

    byte[] biometricKey;

    public static ActivationModel getInstance() {
        if (instance == null) {
            instance = new ActivationModel();
        }
        return instance;
    }



    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public StringBuilder getPinBuilder() {
        return pinBuilder;
    }

    public void setPinBuilder(StringBuilder pinBuilder) {
        this.pinBuilder = pinBuilder;
    }

    public String getInstanceActivationMessage() {
        return instanceActivationMessage;
    }

    public void setInstanceActivationMessage(String instanceActivationMessage) {
        this.instanceActivationMessage = instanceActivationMessage;
    }

    public String getStorageName() {
        return storageName;
    }

    public void setStorageName(String storageName) {
        this.storageName = storageName;
    }

    public void setPlatformFingerprint(String platformFingerprint) {
        this.platformFingerprint = platformFingerprint;
    }

    public String getPlatformFingerprint() {
        return platformFingerprint;
    }

    public void setJailbreakStatus(byte jailbreakStatus) {
        this.jailbreakStatus = jailbreakStatus;
    }

    public byte getJailbreakStatus() {
        return jailbreakStatus;
    }

    public void setClientServerTimeShift(int clientServerTimeShift) {
        this.clientServerTimeShift = clientServerTimeShift;
    }


    public int getClientServerTimeShift() {
        return clientServerTimeShift;
    }

    public void setMultiDeviceLicenseActivationResponse(MultiDeviceLicenseActivationResponse multiDeviceLicenseActivationResponse) {
        this.multiDeviceLicenseActivationResponse = multiDeviceLicenseActivationResponse;
    }

    public MultiDeviceLicenseActivationResponse getMultiDeviceLicenseActivationResponse() {
        return multiDeviceLicenseActivationResponse;
    }

    public void setEncryptionKey(byte[] encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public byte[] getEncryptionKey() {
        return encryptionKey;
    }

    public void setActivationDataResponse(ActivationDataResponse activationDataResponse) {
        this.activationDataResponse = activationDataResponse;
    }

    public ActivationDataResponse getActivationDataResponse() {
        return activationDataResponse;
    }

    public void setSrpSessionKeyResponse(SRPSessionKeyResponse srpSessionKeyResponse) {
        this.srpSessionKeyResponse = srpSessionKeyResponse;
    }

    public SRPSessionKeyResponse getSrpSessionKeyResponse() {
        return srpSessionKeyResponse;
    }

    public void setDsappsrpEphemeralResponse(DSAPPSRPEphemeralResponse dsappsrpEphemeralResponse) {
        this.dsappsrpEphemeralResponse = dsappsrpEphemeralResponse;
    }

    public DSAPPSRPEphemeralResponse getDsappsrpEphemeralResponse() {
        return dsappsrpEphemeralResponse;
    }

    public void setSrpClientEphemeralKeyResponse(SRPClientEphemeralKeyResponse srpClientEphemeralKeyResponse) {
        this.srpClientEphemeralKeyResponse = srpClientEphemeralKeyResponse;
    }

    public SRPClientEphemeralKeyResponse getSrpClientEphemeralKeyResponse() {
        return srpClientEphemeralKeyResponse;
    }

    public void setCredentialsData(CredentialsData credentialsData) {
        this.credentialsData = credentialsData;
    }

    public CredentialsData getCredentialsData() {
        return credentialsData;
    }

    public void setScannedImageData(String scannedImageData) {
        this.scannedImageData = scannedImageData;
    }

    public void setCodeFormated(String scannedImageType) {
        this.codeFormated = scannedImageType;
    }

    public String getScannedImageData() {
        return scannedImageData;
    }

    public String getCodeFormated() {
        return codeFormated;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSignature() {
        return signature;
    }

    public void setBiometricKey(byte[] biometricKey) {
        this.biometricKey = biometricKey;
    }

    public byte[] getBiometricKey() {
        return biometricKey;
    }

    public int getCryptoApplicationIndex() {
        return cryptoApplicationIndex;
    }

    public void setCryptoApplicationIndex(int cryptoApplicationIndex) {
        this.cryptoApplicationIndex = cryptoApplicationIndex;
    }
}
