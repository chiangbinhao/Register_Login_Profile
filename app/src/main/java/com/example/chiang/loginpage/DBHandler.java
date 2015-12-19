package com.example.chiang.loginpage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

/**
 * Created by Chiang on 13/11/2015.
 */
public class DBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "InformationDB.db";
    private static final String TABLE_INFORMATION = "information";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USERNAME = "_username";
    public static final String COLUMN_PASSWORD = "_password";
    public Information info = new Information();

    public DBHandler(Context context, String name,
                       SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_INFORMATION_TABLE = "CREATE TABLE " +
                TABLE_INFORMATION + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_USERNAME
                + " TEXT," + COLUMN_PASSWORD + " TEXT" + ")";
        db.execSQL(CREATE_INFORMATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INFORMATION);
        onCreate(db);
    }

    public Information findUsername(String username) {
        String query = "Select * FROM " + TABLE_INFORMATION + " WHERE " + COLUMN_USERNAME + " =  \"" + username + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            info.setId(Integer.parseInt(cursor.getString(0)));
            info.setUsername(cursor.getString(1));
            info.setPassword(cursor.getString(2));
            cursor.close();
        } else {
            info = null;
        }
        db.close();
        return info;
    }

    public void addUser(Information info) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, info.getUsername());
        values.put(COLUMN_PASSWORD, info.getPassword());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_INFORMATION, null, values);
        db.close();
    }

    public boolean deleteUser(String username) {

        boolean result = false;

        String query = "Select * FROM " + TABLE_INFORMATION + " WHERE " + COLUMN_USERNAME + " =  \"" + username + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Information info = new Information();

        if (cursor.moveToFirst()) {
            info.setId(Integer.parseInt(cursor.getString(0)));
            db.delete(TABLE_INFORMATION, COLUMN_ID + " = ?",
                    new String[] { String.valueOf(info.getId()) });
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }

    public void update(Information info) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, info.getPassword());

        SQLiteDatabase db = this.getWritableDatabase();

        // It's a good practice to use parameter ?, instead of concatenate string
        db.update(TABLE_INFORMATION, values, COLUMN_USERNAME + " = ?", new String[] { String.valueOf(info.getUsername()) });
        db.close(); // Closing database connection
    }

}
