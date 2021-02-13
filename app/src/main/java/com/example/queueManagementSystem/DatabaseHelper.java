package com.example.queueManagementSystem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB = "qms.db";
    public static final String REGISTERED_USERS = "users";
    public static final String COL_ID = "ID";
    public static final String COL_EMAIL = "EMAIL";
    public static final String COL_USERNAME = "USERNAME";
    public static final String COL_PASSWORD = "PASSWORD";
    public static final String COL_ADMIN = "IS_ADMIN";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DB, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + REGISTERED_USERS +
                " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EMAIL + " TEXT, " +
                COL_USERNAME + " TEXT, " +
                COL_PASSWORD + " TEXT, " +
                COL_ADMIN + " INTEGER" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + REGISTERED_USERS);
        onCreate(db);
    }

    // TODO: create user model
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

    public boolean checkUser(String username, String password) {
        String[] columns = { COL_ID };
        SQLiteDatabase db = getReadableDatabase();
        String selection = COL_USERNAME + "=?" + " and " + COL_PASSWORD + "=?";
        String[] selectionArgs = { username, password};
        //SELECT {columns} FROM {table} WHERE {selection - insert ? for placholders} = {selectionargs - replaces placeholders in selection}
        Cursor cursor = db.query(REGISTERED_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkAdmin(String username, String password) {
        String[] columns = { COL_ADMIN };
        SQLiteDatabase db = getReadableDatabase();
        String selection = COL_USERNAME + "=?" + " and " + COL_PASSWORD + "=?" + " and " + COL_ADMIN + "=" + 1;
        String[] selectionArgs = { username, password};
        //SELECT {columns} FROM {table} WHERE {selection - insert ? for placholders} = {selectionargs - replaces placeholders in selection}
        Cursor cursor = db.query(REGISTERED_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }


}
