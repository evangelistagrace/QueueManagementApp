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
    public static final String COL_REQUESTS = "REQUESTS";

    //SERVICES
    public static final String TABLE_SERVICES = "services";
    public static final String COL_SERVICE_NAME = "SERVICE_NAME";
    public static final String COL_SERVICE_RUNNING = "IS_RUNNING";
    public static final String COL_SERVICE_REQUESTS = "SERVICE_REQUESTS";

    //COUNTERS
    public static final String TABLE_COUNTERS = "counters";
    public static final String COL_COUNTER_NAME = "COUNTER_NAME";
    public static final String COL_SERVICE_ID = "SERVICE_ID";
    public static final String COL_CURRENT_SERVING_TICKET_NUMBER = "CURRENT_SERVING_TICKET_NUMBER";
    public static final String COL_CURRENT_SERVING_TICKET_TIME = "CURRENT_SERVING_TICKET_TIME";
    public static final String COL_REMAINING_IN_QUEUE = "REMAINING_IN_QUEUE";
    public static final String COL_COUNTER_OPENED = "IS_OPENED";

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
                COL_ADMIN + " INTEGER, " +
                COL_REQUESTS + " INTEGER" +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_SERVICES +
                " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_SERVICE_NAME + " TEXT, " +
                COL_SERVICE_RUNNING + " INTEGER, " +
                COL_SERVICE_REQUESTS + " INTEGER" +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_COUNTERS +
                " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_COUNTER_NAME + " TEXT, " +
                COL_SERVICE_ID + " INTEGER, " +
                COL_CURRENT_SERVING_TICKET_NUMBER + " INTEGER, " +
                COL_CURRENT_SERVING_TICKET_TIME + " INTEGER, " +
                COL_REMAINING_IN_QUEUE + " INTEGER, " +
                COL_COUNTER_OPENED + " INTEGER" +
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
        cv.put("requests", 0);
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
        cv.put("requests", 0);
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

    public Cursor getUsers () {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { COL_ID, COL_USERNAME, COL_REQUESTS };
        String selection = COL_ADMIN + "=" + 0;

        Cursor cursor = db.query(REGISTERED_USERS, columns, selection, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getTopCustomers () {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { COL_ID, COL_USERNAME, COL_REQUESTS };
        String selection = COL_ADMIN + "=" + 0 + " and " + COL_REQUESTS + " > " + 3;

        Cursor cursor = db.query(REGISTERED_USERS, columns, selection, null, null, null, COL_REQUESTS);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public long incrementCustomerRequest(int userId) {
        //get previous request value first
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { COL_REQUESTS };
        String selection = COL_ID + "=" + userId;
        int numOfRequests = 0;

        Cursor cursor = db.query(REGISTERED_USERS, columns, selection, null, null, null, null);
        if (cursor.moveToFirst()) {
            numOfRequests = cursor.getInt(0);
        }

        //increment for each call
        ++numOfRequests;

        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("requests", numOfRequests);

        long res = db.update(REGISTERED_USERS, cv, selection, null);
        db.close();
        return res;
    }

    // SERVICES
    public long addService (String serviceName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("service_name", serviceName);
        cv.put("is_running", 1);
        cv.put("service_requests", 0);
        long res = db.insert(TABLE_SERVICES, null, cv);
        db.close();
        return res;
    }

    public Cursor getServices () {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { COL_ID, COL_SERVICE_NAME, COL_SERVICE_RUNNING, COL_SERVICE_REQUESTS};

        Cursor cursor = db.query(TABLE_SERVICES, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getService (int serviceId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { COL_ID, COL_SERVICE_NAME, COL_SERVICE_RUNNING};
        String selection = COL_ID + "=" + serviceId;

        Cursor cursor = db.query(TABLE_SERVICES, columns, selection, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public long setServiceName(int serviceId, String serviceName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String selection = COL_ID + "=?";
        String[] selectionArgs = { String.valueOf(serviceId) };

        cv.put("service_name", serviceName);

        long res = db.update(TABLE_SERVICES, cv, selection, selectionArgs);
        db.close();
        return res;
    }

    public long setServiceRunning(int serviceId, int serviceRunning) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String selection = COL_ID + "=?";
        String[] selectionArgs = { String.valueOf(serviceId) };

        cv.put("is_running", serviceRunning);

        long res = db.update(TABLE_SERVICES, cv, selection, selectionArgs);

        if (res > 0) {
            // also search counters table and set service counters to closed
            Cursor cursor = this.getCounters(serviceId);

            if (cursor.moveToFirst()) {
                do {
                    if (serviceRunning == 0) {
                        this.setCounterOpen(cursor.getInt(0), 0);
                    } else {
                        this.setCounterOpen(cursor.getInt(0), 1);
                    }
                } while (cursor.moveToNext());
            }
        }

        db.close();
        return res;
    }

    public long deleteService(int serviceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = COL_ID + "=?";
        String[] selectionArgs = { String.valueOf(serviceId) };

        long res = db.delete(TABLE_SERVICES, selection, selectionArgs);
        db.close();
        return res;
    }

    public long incrementServiceRequest(int serviceId) {
        //get previous request value first
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { COL_SERVICE_REQUESTS };
        String selection = COL_ID + "=" + serviceId;
        int numOfServiceRequests = 0;

        Cursor cursor = db.query(TABLE_SERVICES, columns, selection, null, null, null, null);
        if (cursor.moveToFirst()) {
            numOfServiceRequests = cursor.getInt(0);
        }

        //increment for each call
        ++numOfServiceRequests;

        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("service_requests", numOfServiceRequests);

        long res = db.update(TABLE_SERVICES, cv, selection, null);
        db.close();
        return res;
    }


    // COUNTERS
    public long addCounter (String counterName, Integer serviceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("counter_name", counterName);
        cv.put("service_id", serviceId);
        cv.put("current_serving_ticket_number", 0);
        cv.put("current_serving_ticket_time", 0);
        cv.put("remaining_in_queue", 0);
        cv.put("is_opened", 1);
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

    public Cursor getAllCounters () {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { COL_ID, COL_COUNTER_NAME, COL_CURRENT_SERVING_TICKET_NUMBER, COL_REMAINING_IN_QUEUE};
        String selection = COL_COUNTER_OPENED + "=" + 1;

        Cursor cursor = db.query(TABLE_COUNTERS, columns, selection, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getServingTime () {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { COL_CURRENT_SERVING_TICKET_TIME };
        String selection = COL_COUNTER_OPENED + "=" + 1;

        Cursor cursor = db.query(TABLE_COUNTERS, columns, selection, null, null, null, null, "1");
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public long setCounterOpen(int counterId, int isOpened) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String selection = COL_ID + "=?";
        String[] selectionArgs = { String.valueOf(counterId) };

        cv.put("is_opened", isOpened);

        long res = db.update(TABLE_COUNTERS, cv, selection, selectionArgs);

        db.close();
        return res;
    }

    public long setCurrentServingTicket(int counterId, int ticketNumber, int ticketTime, int remainingInQueue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String selection = COL_ID + "=?";
        String[] selectionArgs = { String.valueOf(counterId) };

        cv.put("current_serving_ticket_number", ticketNumber);
        cv.put("current_serving_ticket_time", ticketTime);
        cv.put("remaining_in_queue", remainingInQueue);

        long res = db.update(TABLE_COUNTERS, cv, selection, selectionArgs);
        db.close();
        return res;
    }

}
