package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class PetProvider extends ContentProvider {


    // URI matcher code for the content URI for the pets table.
    private static final int PETS = 100;
    // URI matcher code for the content URI for a single pet in the pets table.
    private static final int PET_ID = 101;
    // UriMatcher object to match a content URI to a corresponding code.
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    // Static initializer. This is run the first time anything is called from this class.
    static {
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PET_ID);
    }

    // Tag for the log messages.
    public static final String LOG_TAG = PetProvider.class.getSimpleName();

    private PetDbHelper dbHelper;

    @Override
    public boolean onCreate() {
        // Initialize a PetDbHelper object to gain access to the pets database.
        dbHelper = new PetDbHelper(getContext());
        return true;
    }

    // Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch (match) {
            case PETS:
                cursor = database.query(PetContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case PET_ID:
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(PetContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot querry unknown URI " + uri);
        }
        return cursor;
    }

    // Returns the MIME type of data for the content URI.
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    // Insert new data into the provider with the given ContentValues.
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    // Delete the data at the given selection and selection arguments.
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    // Updates the data at the given selection and selection arguments, with the new ContentValues.
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}