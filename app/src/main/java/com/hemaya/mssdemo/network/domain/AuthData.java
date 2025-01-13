package com.hemaya.mssdemo.network.domain;

import android.util.Base64;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class AuthData {
    public static String userName = "95AE356F045197E1229B5B9CC2A0A73857AC7F8D949D5A00C8095FE3806845FF";
    public static String secretKey = "LRMaP6HlWeCs3PrAPo0a9ZP23FP7RZPyuBgjnEbUpyg";

    public static String generateHmac(String data) {
        try {
            String algorithm = "HmacSHA256";

            // Convert input and key to bytes
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);

            // Create SecretKeySpec and Mac instance
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, algorithm);
            Mac mac = Mac.getInstance(algorithm);
            mac.init(keySpec);

            // Compute HMAC
            byte[] hmacBytes = mac.doFinal(dataBytes);

            // Convert HMAC bytes to hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hmacBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            // Convert hexadecimal string to bytes for Base64 encoding
            byte[] hexToBytes = hexString.toString().getBytes(StandardCharsets.UTF_8);

            return Base64.encodeToString(hexToBytes, Base64.NO_WRAP);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate HMAC", e);
        }
    }

}
