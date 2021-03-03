package com.example.queueManagementSystem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB = "qms.db";

    //USERS
    public static final String REGISTERED_USERS = "users";
    public static final String COL_ID = "ID";
    public static final String COL_EMAIL = "EMAIL";
    public static final String COL_USERNAME = "USERNAME";
    public static final String COL_PASSWORD = "PASSWORD";
    public static final String COL_ADMIN = "IS_ADMIN";

    //SERVICES
    public static final String TABLE_SERVICES = "services";
    public static final String COL_SERVICE_NAME = "SERVICE_NAME";

    //COUNTERS
    public static final String TABLE_COUNTERS = "counters";
    public static final String COL_COUNTER_NAME = "COUNTER_NAME";
    public static final String COL_SERVICE_ID = "SERVICE_ID";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DB, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create tables here
        db.execSQL("CREATE TABLE " + REGISTERED_USERS +
                " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EMAIL + " TEXT, " +
                COL_USERNAME + " TEXT, " +
                COL_PASSWORD + " TEXT, " +
                COL_ADMIN + " INTEGER" +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_SERVICES +
                " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_SERVICE_NAME + " TEXT" +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_COUNTERS +
                " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_COUNTER_NAME + " TEXT, " +
                COL_SERVICE_ID + " INTEGER" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + REGISTERED_USERS);
        onCreate(db);
    }

    // ADMIN
    public long addAdmin (String email, String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("email", email);
        cv.put("username", username);
        cv.put("password", password);
        cv.put("is_admin", 1);
        long res = db.insert(REGISTERED_USERS, null, cv);
        db.close();
        return res;
    }

    public int checkAdmin(String username, String password) {
        int adminId = -1;
        SQLiteDatabase db = getReadableDatabase();
        String selection = COL_USERNAME + "=?" + " and " + COL_PASSWORD + "=?" + " and " + COL_ADMIN + "=" + 1;
        String[] selectionArgs = { username, password};
        //SELECT {columns} FROM {table} WHERE {selection - insert ? for placholders} = {selectionargs - replaces placeholders in selection}
        Cursor cursor = db.rawQuery("SELECT " + COL_ID + " FROM " + REGISTERED_USERS + " WHERE " + selection, selectionArgs);

        while (cursor.moveToNext()) {
            adminId = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return adminId;
    }


    // CUSTOMER
    public long addUser (String email, String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("email", email);
        cv.put("username", username);
        cv.put("password", password);
        cv.put("is_admin", 0);
        long res = db.insert(REGISTERED_USERS, null, cv);
        db.close();
        return res;
    }

    public int checkUser(String username, String password) {
        int userId = -1;
        SQLiteDatabase db = getReadableDatabase();
        String selection = COL_USERNAME + "=?" + " and " + COL_PASSWORD + "=?";
        String[] selectionArgs = { username, password};
        //SELECT {columns} FROM {table} WHERE {selection - insert ? for placholders} = {selectionargs - replaces placeholders in selection}
        Cursor cursor = db.rawQuery("SELECT " + COL_ID + " FROM " + REGISTERED_USERS + " WHERE " + selection, selectionArgs);

        while (cursor.moveToNext()) {
            userId = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return userId;
    }

    public long setUserPassword(int userId, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String selection = COL_ID + "=?";
        String[] selectionArgs = { String.valueOf(userId) };

        cv.put("password", newPassword);

        long res = db.update(REGISTERED_USERS, cv, selection, selectionArgs);
        db.close();
        return res;
    }

    // SERVICES
    public long addService (String serviceName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("service_name", serviceName);
        long res = db.insert(TABLE_SERVICES, null, cv);
        db.close();
        return res;
    }

    public Cursor getServices () {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { COL_ID, COL_SERVICE_NAME };
//        String selection = COL_USERNAME + "=?" + " and " + COL_PASSWORD + "=?" + " and " + COL_ADMIN + "=" + 1;
//        String[] selectionArgs = { username, password};

        Cursor cursor = db.query(TABLE_SERVICES, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }


    // COUNTERS
    public long addCounter (String counterName, Integer serviceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("counter_name", counterName);
        cv.put("service_id", serviceId);
        long res = db.insert(TABLE_COUNTERS, null, cv);
        db.close();
        return res;
    }

    public Cursor getCounters (int serviceId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { COL_ID, COL_COUNTER_NAME };
        String selection = COL_SERVICE_ID + "=?";
        String[] selectionArgs = { String.valueOf(serviceId) };

        Cursor cursor = db.query(TABLE_COUNTERS, columns, selection, selectionArgs, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }



}
