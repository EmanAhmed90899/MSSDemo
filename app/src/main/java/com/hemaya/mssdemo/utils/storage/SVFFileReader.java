package com.hemaya.mssdemo.utils.storage;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SVFFileReader {

    // Method to read the .svf file from the assets folder and return the raw binary data as a string
    public String readSVFFileAsString(Context context, String fileName) {
        try {
            // Open the file as an InputStream from the assets directory
            InputStream inputStream = context.getAssets().open(fileName);

            // Get the size of the file
            int fileSize = inputStream.available();

            // Create a byte array with the size of the file
            byte[] buffer = new byte[fileSize];

            // Read the file's content into the byte array
            inputStream.read(buffer);

            // Close the InputStream after reading
            inputStream.close();

            // Convert the byte array to a Base64 encoded string (or any other encoding)
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

