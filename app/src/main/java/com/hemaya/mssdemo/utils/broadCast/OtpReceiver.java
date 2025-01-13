package com.hemaya.mssdemo.utils.broadCast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OtpReceiver extends BroadcastReceiver {

    // Trusted sender name (Alphanumeric Sender ID, like "YourBank")
    private static final String TRUSTED_SENDER_NAME = "NBE-OTP"; // Replace with actual sender name
    private OtpListener otpListener;

    public void setOtpListener(OtpListener listener) {
        this.otpListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                // Retrieve the SMS messages received
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    for (Object pdu : pdus) {
                        // Convert the PDU to an SMS message object
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);

                        // Get the sender's alphanumeric sender ID or phone number
                        String senderId = smsMessage.getDisplayOriginatingAddress();

                        // Get the message body
                        String messageBody = smsMessage.getMessageBody();
                        // Check if the sender is the trusted service provider (by sender ID)
                        if (TRUSTED_SENDER_NAME.equals(senderId)) {
                            // Extract OTP from message
                            String otp = extractOtpFromMessage(messageBody);
                            if (otp != null && otpListener != null) {
                                otpListener.onOtpReceived(otp);
                            }

                        }
                    }
                }
            }
        }
    }

    // A simple method to extract OTP from the message body (assuming OTP is 6 digits)
    private String extractOtpFromMessage(String message) {
        // Use regex to extract the first occurrence of 6 digits (modify if OTP format differs)
        Pattern pattern = Pattern.compile("\\b\\d{6}\\b");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }

    public interface OtpListener {
        void onOtpReceived(String otp);
    }
}
