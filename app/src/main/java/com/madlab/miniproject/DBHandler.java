package com.madlab.miniproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "medicinedb";

    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME = "myreminders";

    private static final String ID_COL = "id";

    private static final String NAME_COL = "medicinename";

    private static final String DATE_COL = "date";

    private static final String TIME_COL = "time";

    private static final String COMPLETED_COL = "completed";

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, "
                + NAME_COL + " TEXT,"
                + DATE_COL + " TEXT,"
                + TIME_COL + " TEXT,"
                + COMPLETED_COL + " TEXT)";

        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertReminder(int id, String name, String date, String time, String completed) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ID_COL, id);
        values.put(NAME_COL, name);
        values.put(DATE_COL, date);
        values.put(TIME_COL, time);
        values.put(COMPLETED_COL, completed);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public boolean checkReminder(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT " + ID_COL +" FROM " + TABLE_NAME, null);

        ArrayList<String> courseModalArrayList = new ArrayList<>();


        if (cursor.moveToFirst()) {
            do {
                courseModalArrayList.add(Integer.toString(cursor.getInt(0)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return courseModalArrayList.contains(Integer.toString(id));
    }

    public ArrayList<String> readReminders() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COMPLETED_COL + " ASC" + ","+ DATE_COL + " ASC" + "," + TIME_COL + " ASC", null);

        ArrayList<String> courseModalArrayList = new ArrayList<>();


        if (cursor.moveToFirst()) {
            do {
                courseModalArrayList.add("ID: " + Integer.toString(cursor.getInt(0)) + "\n" +
                        "Medicine Name: " + cursor.getString(1) + "\n" +
                        "Date: " + cursor.getString(2) + "\n" +
                        "Time: " + cursor.getString(3) + "\n" +
                        "Completed: " + cursor.getString(4));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return courseModalArrayList;
    }

    public void markAsCompleted(int id, String name, String date, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ID_COL, id);
        values.put(NAME_COL, name);
        values.put(DATE_COL, date);
        values.put(TIME_COL, time);
        values.put(COMPLETED_COL, "Yes");

        db.update(TABLE_NAME, values, "id=?", new String[]{Integer.toString(id)});

        db.close();
    }

    public void deleteReminder(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, ID_COL + "=" + id, null);
        db.close();
    }
}
