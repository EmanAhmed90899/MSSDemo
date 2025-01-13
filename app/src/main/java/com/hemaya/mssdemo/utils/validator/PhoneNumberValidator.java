package com.hemaya.mssdemo.utils.validator;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class PhoneNumberValidator {

    public String getValidPhone(String phone, String regionCode) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {

            // Parse the phone number
            Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(phone, regionCode);

            // Check if the phone number is valid
            return phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);

        } catch (NumberParseException e) {
            e.printStackTrace();
            return ""; // Invalid number
        }
    }
}

