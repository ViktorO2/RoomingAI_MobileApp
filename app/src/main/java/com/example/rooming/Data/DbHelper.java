package com.example.rooming.Data;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.rooming.Entity.Users;

public class DbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "rooming.db";

    //table USERS info

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";


    public DbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE " + TABLE_USERS + "(" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_EMAIL + " TEXT ," +
                COLUMN_PASSWORD + " TEXT " + ");";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    //Registration of new user
    public Boolean Registration(Users user) {

        ContentValues v = new ContentValues();
        v.put(COLUMN_USERNAME, user.getUsername());
        v.put(COLUMN_EMAIL, user.getEmail());
        v.put(COLUMN_PASSWORD, user.getPassword());

        SQLiteDatabase database = getWritableDatabase();

        long resultRow = database.insert(TABLE_USERS, null, v);

        database.close();

        return resultRow != -1;


    }

    public Boolean checkEmailRegistration(Users user) {
        SQLiteDatabase database = getWritableDatabase();

        Cursor cursor = database.rawQuery("Select * from users where email = ?", new String[]{user.getEmail()});

        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean checkLoginParametersExist(Users user) {
        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.rawQuery("Select * from users where username = ? and password = ?",
                new String[]{user.getUsername(), user.getPassword()});

        return cursor.getCount() > 0;

    }

    public String getUserId(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();
        String result = "";
        Cursor cursor = db.rawQuery("Select id from users where username = ? and password = ?",
                new String[]{username, password});

        if (cursor.moveToFirst()) {
            result = result + cursor.getString(0);
        } else {
            result = result + "result not found";
        }

        return result;
    }

    public String[] getUsername(String user_id) {
        SQLiteDatabase database = getWritableDatabase();
        String username = "";
        String[] result = new String[1];
        Cursor cursor = database.rawQuery("Select username from users where id=" + user_id, null);

        if (cursor.moveToFirst()) {
            username += cursor.getString(0);

        } else {
            username = "not found";
        }

        result[0] = username;

        return result;
    }

    public String[] getEmail(String user_id) {
        SQLiteDatabase database = getWritableDatabase();
        String email = "";
        String[] result = new String[1];
        Cursor cursor = database.rawQuery("Select email from users where id=" + user_id, null);

        if (cursor.moveToFirst()) {
            email += cursor.getString(0);

        } else {
            email = "not found";
        }

        result[0] = email;

        return result;
    }

    public String[] getPassword(String user_id) {
        SQLiteDatabase database = getWritableDatabase();
        String password = "";
        String[] result = new String[1];
        Cursor cursor = database.rawQuery("Select password from users where id=" + user_id, null);

        if (cursor.moveToFirst()) {
            password += cursor.getString(0);

        } else {
            password = "not found";
        }

        result[0] = password;

        return result;
    }

    public void editUser(int id, String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_USERS + " SET " +
                COLUMN_USERNAME + " = '" + username + "', " +
                COLUMN_EMAIL + " = '" + email + "', " +
                COLUMN_PASSWORD + " = '" + password + "' WHERE " +
                COLUMN_USER_ID + " = " + id;

        db.execSQL(query);
    }

    public void deleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Delete from " + TABLE_USERS + " where " + COLUMN_USER_ID + " = " + userId;
        db.execSQL(query);
    }



}
