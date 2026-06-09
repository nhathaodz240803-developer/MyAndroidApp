package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME    = "notes.db";
    public static final int    DATABASE_VERSION = 1;
    public static final String TABLE_NOTES      = "notes";
    public static final String COLUMN_ID        = "_id";
    public static final String COLUMN_TITLE     = "title";
    public static final String COLUMN_CONTENT   = "content";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NOTES + " (" +
                    COLUMN_ID      + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE   + " TEXT NOT NULL, " +
                    COLUMN_CONTENT + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override public void onCreate(SQLiteDatabase db) { db.execSQL(CREATE_TABLE); }

    @Override public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    public long insertNote(String title, String content) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_TITLE, title);
        v.put(COLUMN_CONTENT, content);
        long id = db.insert(TABLE_NOTES, null, v);
        db.close();
        return id;
    }

    public Cursor getAllNotes() {
        return getReadableDatabase()
                .query(TABLE_NOTES, null, null, null, null, null, COLUMN_ID + " DESC");
    }

    public int deleteNote(long id) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_NOTES, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }
}