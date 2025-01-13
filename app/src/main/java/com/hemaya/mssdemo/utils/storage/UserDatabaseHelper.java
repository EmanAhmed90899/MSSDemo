package com.hemaya.mssdemo.utils.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.hemaya.mssdemo.model.UserModel.User;

import java.util.ArrayList;
import java.util.List;


public class UserDatabaseHelper extends SQLiteOpenHelper {

    // Database Information
    private static final String DATABASE_NAME = "userDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table name
    private static final String TABLE_USER = "User";

    // Column names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_SERIAL_NUMBER = "serialNumber";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_STORAGE_NAME = "storageName";
    private static final String COLUMN_IS_USED = "isUsed";
    private static final String COLUMN_Biometric_USER_ID = "bioUserId";
    private static final String COLUMN_IS_FIRST_LOGIN_TO_ENABLE_FINGERPRINT = "isFirstLoginToEnableFingerprint";
    // Table name
    private static final String TABLE_USER_BIOMETRIC = "UserBiometric";

    // Column names
    private static final String COLUMN_ID_BIOMETRIC = "id";
    private static final String COLUMN_USER_ID = "userId";
    private static final String COLUMN_PLATFORM_FINGERPRINT_BIOMETRIC = "platformFingerPrint";
    private static final String COLUMN_SERIAL_NUMBER_BIOMETRIC = "serialNumber";
    private static final String COLUMN_STORAGE_NAME_BIOMETRIC = "storageName";
    private static final String COLUMN_IS_DETECTION_FINGERPRINT_BIOMETRIC = "isDetectionFingerprint";

    // Create table SQL query
    private static final String CREATE_TABLE_USER =
            "CREATE TABLE " + TABLE_USER + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_SERIAL_NUMBER + " TEXT, "
                    + COLUMN_NAME + " TEXT, "
                    + COLUMN_STORAGE_NAME + " TEXT, "
                    + COLUMN_IS_USED + " INTEGER, " +
                    COLUMN_Biometric_USER_ID + " INTEGER, " +
                    COLUMN_IS_FIRST_LOGIN_TO_ENABLE_FINGERPRINT + " INTEGER ," +
                    "FOREIGN KEY (" + COLUMN_Biometric_USER_ID + ") REFERENCES " + TABLE_USER_BIOMETRIC + "(" + COLUMN_ID_BIOMETRIC + "))";


    // Create table SQL query
    private static final String CREATE_TABLE_USER_BIOMETRIC =
            "CREATE TABLE " + TABLE_USER_BIOMETRIC + "("
                    + COLUMN_ID_BIOMETRIC + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_USER_ID + " INTEGER, "
                    + COLUMN_PLATFORM_FINGERPRINT_BIOMETRIC + " TEXT, "
                    + COLUMN_SERIAL_NUMBER_BIOMETRIC + " TEXT, "
                    + COLUMN_STORAGE_NAME_BIOMETRIC + " TEXT, "
                    +
                    COLUMN_IS_DETECTION_FINGERPRINT_BIOMETRIC + " INTEGER)";

Context context;
SaveInLocalStorage saveInLocalStorage;
    public UserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the user table
        db.execSQL(CREATE_TABLE_USER);
        // Create the user biometric table
        db.execSQL(CREATE_TABLE_USER_BIOMETRIC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the old table if it exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_BIOMETRIC);
        // Create tables again
        onCreate(db);
    }

    // Add a new user
    @RequiresApi(api = Build.VERSION_CODES.O)
    public long addUser(String platformFingerPrint, String serialNumber, String name, boolean isUsed, String storageName) {
        User foundedUser = IsUserFound(serialNumber.substring(0, serialNumber.indexOf('-')));
        if(foundedUser != null) {
            saveInLocalStorage = new SaveInLocalStorage(context, foundedUser.getStorageName());
            saveInLocalStorage.deleteStorage(foundedUser.getStorageName());
            deleteUser(foundedUser.getId());
        }
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_SERIAL_NUMBER, serialNumber);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_STORAGE_NAME, storageName);
        values.put(COLUMN_IS_USED, isUsed ? 1 : 0);
        values.put(COLUMN_IS_FIRST_LOGIN_TO_ENABLE_FINGERPRINT, 0);
        // Store boolean as integer

        // Insert row
        long id = db.insert(TABLE_USER, null, values);

        // Close the database connection
        db.close();

        return id;
    }

    public User IsUserFound(String serialNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_SERIAL_NUMBER + " LIKE ?",
                new String[]{serialNumber + "%"});


        if (cursor != null) {
            if (cursor.getCount() == 0) {
                cursor.close();
                db.close();
                return null;
            } else {
                cursor.moveToFirst();
                db.close();
                User user=new User(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SERIAL_NUMBER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STORAGE_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_USED)) == 1,
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_Biometric_USER_ID)),
                        false,
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_FIRST_LOGIN_TO_ENABLE_FINGERPRINT)) == 1
                );
                cursor.close();
                return user;
            }
        } else {
            cursor.close();
            db.close();
            return null;
        }
    }

    public long addUserBiometric(int userId, String platformFingerPrint, String serialNumber, String storageName, boolean isDetectionFingerprint) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PLATFORM_FINGERPRINT_BIOMETRIC, platformFingerPrint);
        values.put(COLUMN_SERIAL_NUMBER_BIOMETRIC, serialNumber);
        values.put(COLUMN_STORAGE_NAME_BIOMETRIC, storageName);
        values.put(COLUMN_IS_DETECTION_FINGERPRINT_BIOMETRIC, isDetectionFingerprint ? 1 : 0);

        // Insert row
        long id = db.insert(TABLE_USER_BIOMETRIC, null, values);
        updateForeignKey(userId, Integer.parseInt(id + ""));

        // Close the database connection
        db.close();

        return id;
    }

    public boolean userDetectionBiometric(int userId) {
        long bioID = getBiometricIdFromUserID(userId + "");
        SQLiteDatabase db = this.getReadableDatabase();

        // Query the database
        Cursor cursor = db.query(TABLE_USER_BIOMETRIC,
                new String[]{COLUMN_IS_DETECTION_FINGERPRINT_BIOMETRIC},
                COLUMN_USER_ID + "=?",
                new String[]{String.valueOf(bioID)}, null, null, null, null);
        boolean isDetectionFingerprint = false;
        if (cursor != null) {
            if (cursor.getCount() == 0) {
                return false;
            }
            cursor.moveToFirst();
            // Create a User object
            isDetectionFingerprint = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_DETECTION_FINGERPRINT_BIOMETRIC)) > 0;

            cursor.close();
        }


        return isDetectionFingerprint;
    }

    public void updateForeignKey(int userId, int bioUserId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_Biometric_USER_ID, bioUserId);
        db.update(TABLE_USER, values, COLUMN_ID + " = ?", new String[]{String.valueOf(userId)});
    }


    // Get a single user by ID
    public User getUser(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Query the database
        Cursor cursor = db.query(TABLE_USER,
                new String[]{COLUMN_ID, COLUMN_SERIAL_NUMBER, COLUMN_NAME, COLUMN_STORAGE_NAME, COLUMN_IS_USED, COLUMN_Biometric_USER_ID, COLUMN_IS_FIRST_LOGIN_TO_ENABLE_FINGERPRINT},
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        // Create a User object
        User user = new User(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SERIAL_NUMBER)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STORAGE_NAME)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_USED)) == 1,
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_Biometric_USER_ID)),
                false,
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_FIRST_LOGIN_TO_ENABLE_FINGERPRINT)) == 1
        );

        cursor.close();
        return user;
    }

    // Get all users
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Loop through all rows and add to the list
        if (cursor.moveToFirst()) {
            do {
                User user = new User(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SERIAL_NUMBER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STORAGE_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_USED)) == 1
                        , cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_Biometric_USER_ID)),
                        false,
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_FIRST_LOGIN_TO_ENABLE_FINGERPRINT)) == 1
                );
                userList.add(user);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return userList;
    }

    // Delete a user by ID
    public void deleteUser(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteUserBiometric(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER_BIOMETRIC, COLUMN_ID_BIOMETRIC + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public long getBiometricIdFromUserID(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Query the database
        Cursor cursor = db.query(TABLE_USER,
                new String[]{COLUMN_Biometric_USER_ID},
                COLUMN_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, null, null);

        if (cursor != null) {
            if (cursor.getCount() == 0) {
                return -1;
            }
            cursor.moveToFirst();
        }

        // Create a User object
        long bioUserId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_Biometric_USER_ID));

        cursor.close();
        return bioUserId;
    }

    // Method to get one user where isUsed = true
    public User getOneUsedUser() {
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to select one user where isUsed = 1
        String selectQuery = "SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_IS_USED + " = 1 LIMIT 1";

        Cursor cursor = db.rawQuery(selectQuery, null);

        User user = null;

        if (cursor != null && cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SERIAL_NUMBER)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STORAGE_NAME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_USED)) == 1,
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_Biometric_USER_ID)),
                    false,
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_FIRST_LOGIN_TO_ENABLE_FINGERPRINT)) == 1
            );
            cursor.close();
        }

        return user;
    }

    public void updateAllIsDetectionFingerprint(boolean isDetectionFingerprint) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_DETECTION_FINGERPRINT_BIOMETRIC, isDetectionFingerprint ? 1 : 0);
        db.update(TABLE_USER_BIOMETRIC, values, null, null);
    }

    // Method to check if at least one user is used
    public boolean isAtLeastOneUserUsed() {
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to count users where isUsed = 1
        String selectQuery = "SELECT COUNT(*) FROM " + TABLE_USER + " WHERE " + COLUMN_IS_USED + " = 1";

        Cursor cursor = db.rawQuery(selectQuery, null);
        boolean isUsed = false;

        if (cursor != null) {
            cursor.moveToFirst();
            int count = cursor.getInt(0); // Get the count from the first column
            isUsed = count > 0; // If count is greater than 0, there is at least one user with isUsed = true
            cursor.close();
        }

        return isUsed;
    }

    public int updateUserName(String name, String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);

        // Update the row
        return db.update(TABLE_USER, values, COLUMN_ID + " = ?",
                new String[]{id});
    }

    public User updateIsUsed(String id, boolean isUsed) {
        SQLiteDatabase db = this.getWritableDatabase();
        deactivateAllUsers(db);
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_USED, isUsed ? 1 : 0);

        // Update the row
        db.update(TABLE_USER, values, COLUMN_ID + " = ?",
                new String[]{id});

        return getUser(Long.parseLong(id));
    }

    // Method to set isActive to false for all users
    public void deactivateAllUsers(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_USED, 0);  // Set the column value to false

        // Update all rows where isActive exists
        db.update(TABLE_USER, values, null, null);  // No WHERE clause, updates all rows
    }

    public String getStorageName(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Query the database
        Cursor cursor = db.query(TABLE_USER,
                new String[]{COLUMN_STORAGE_NAME},
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        // Create a User object
        String storageName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STORAGE_NAME));

        cursor.close();
        return storageName;

    }


    public boolean getIsFirstLoginToEnableFingerprint(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Query the database
        Cursor cursor = db.query(TABLE_USER,
                new String[]{COLUMN_IS_FIRST_LOGIN_TO_ENABLE_FINGERPRINT},
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        // Create a User object
        boolean isFirstLoginToEnableFingerprint = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_FIRST_LOGIN_TO_ENABLE_FINGERPRINT)) > 0;

        cursor.close();
        return isFirstLoginToEnableFingerprint;
    }

    public void updateIsFirstLoginToEnableFingerprint(int id, boolean isFirstLoginToEnableFingerprint) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_FIRST_LOGIN_TO_ENABLE_FINGERPRINT, isFirstLoginToEnableFingerprint);

        // Update the row
        db.update(TABLE_USER, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public boolean isUserHasBiometric(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Query the database
        Cursor cursor = db.query(TABLE_USER,
                new String[]{COLUMN_Biometric_USER_ID},
                COLUMN_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, null, null);
        int bioUserId = -1;
        if (cursor != null) {
            if (cursor.getCount() == 0) {
                return false;
            }
            cursor.moveToFirst();
            // Create a User object
            bioUserId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_Biometric_USER_ID));

            cursor.close();
        }


        return bioUserId > 0;
    }

    public int usersCount() {
        String countQuery = "SELECT  * FROM " + TABLE_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    public int selectAnotherUser() {
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to select one user where isUsed = 1
        String selectQuery = "SELECT * FROM " + TABLE_USER + " LIMIT 1";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            updateIsUsed(id + "", true);
            cursor.close();
            return id;
        } else {
            return -1;
        }
    }

    public void deleteAllUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_USER);
        db.execSQL("delete from "+ TABLE_USER_BIOMETRIC);
    }
}

