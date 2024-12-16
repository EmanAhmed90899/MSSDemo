package com.hemaya.mssdemo.model.identification;

public class Country {
    private final String isoCode;
    private final String name;
    private final String dialCode;

    public Country(String isoCode, String name, String dialCode) {
        this.isoCode = isoCode;
        this.name = name;
        this.dialCode = dialCode;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public String getName() {
        return name;
    }

    public String getDialCode() {
        return dialCode;
    }

    @Override
    public String toString() {
        return name + " (" + dialCode + ")";
    }
}


