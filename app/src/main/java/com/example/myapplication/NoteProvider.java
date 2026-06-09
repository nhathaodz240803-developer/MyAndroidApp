package com.example.myapplication;

import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NoteProvider extends ContentProvider {

    public static final String AUTHORITY    = "com.example.myapplication.provider";
    public static final Uri    CONTENT_URI  = Uri.parse("content://" + AUTHORITY + "/notes");

    private static final int NOTES   = 1;
    private static final int NOTE_ID = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY, "notes",   NOTES);
        uriMatcher.addURI(AUTHORITY, "notes/#", NOTE_ID);
    }

    private DatabaseHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable @Override
    public Cursor query(@NonNull Uri uri, String[] proj, String sel,
                        String[] selArgs, String sort) {
        Cursor cursor;
        if (uriMatcher.match(uri) == NOTE_ID) {
            sel     = DatabaseHelper.COLUMN_ID + "=?";
            selArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
        }
        cursor = dbHelper.getReadableDatabase()
                .query(DatabaseHelper.TABLE_NOTES, proj, sel, selArgs,
                        null, null, sort != null ? sort : DatabaseHelper.COLUMN_ID + " DESC");
        if (getContext() != null)
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        long id = dbHelper.getWritableDatabase()
                .insert(DatabaseHelper.TABLE_NOTES, null, values);
        if (getContext() != null)
            getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    @Override
    public int delete(@NonNull Uri uri, String sel, String[] selArgs) {
        if (uriMatcher.match(uri) == NOTE_ID) {
            sel     = DatabaseHelper.COLUMN_ID + "=?";
            selArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
        }
        int count = dbHelper.getWritableDatabase()
                .delete(DatabaseHelper.TABLE_NOTES, sel, selArgs);
        if (getContext() != null)
            getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override public int update(@NonNull Uri uri, ContentValues v, String s, String[] a) { return 0; }
    @Nullable @Override public String getType(@NonNull Uri uri) { return null; }
}