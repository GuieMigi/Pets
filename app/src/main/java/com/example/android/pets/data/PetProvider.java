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
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.android.pets.data.PetContract.PetEntry;

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
            default:
                throw new IllegalArgumentException("Cannot querry unknown URI " + uri);
        }
        // Set notification URI on the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    // Returns the MIME type of data for the content URI.
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case PETS:
                return PetEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknownwn URI " + uri + " with match " + match);
        }
    }

    // Insert new data into the provider with the given ContentValues.
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case PETS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    // Delete the data at the given selection and selection arguments.
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case PETS:
                return deletePet(uri, selection, selectionArgs);
            case PET_ID:
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return deletePet(uri, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Delete is not supported for " + uri);
        }
    }

    // Updates the data at the given selection and selection arguments, with the new ContentValues.
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case PETS:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case PET_ID:
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private Uri insertPet(Uri uri, ContentValues values) {
        // Check that the name is not null.
        String name = values.getAsString(PetEntry.COLUMN_PET_NAME);
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Pet requires a name");
        }

        // Check that the gender is valid.
        Integer gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
        if (gender == null || !PetEntry.isValidGender(gender)) {
            throw new IllegalArgumentException("Pet requires a valid gender");
        }

        // If the weight is provided, check that it's greater than or equal to 0 kg.
        Integer weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
        if (weight != null && weight < 0) {
            throw new IllegalArgumentException("The weight cannot have a negative value");
        }

        // Get the database in write mode.
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        // Insert a new row for pet in the database, returning the ID of that new row.
        long id = database.insert(PetEntry.TABLE_NAME, null, values);
        // Show a toast message depending on whether or not the insertion was successful.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        } else Toast.makeText(getContext(), "Pet saved", Toast.LENGTH_LONG).show();
        // Notify all listeners that the data has changed for the pet content URI.
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Check if the name needs to be updated.
        if (values.containsKey(PetEntry.COLUMN_PET_NAME)) {
            // Check that the name is not null.
            String name = values.getAsString(PetEntry.COLUMN_PET_NAME);
            if (TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }

        // Check if the gender needs to be updated.
        if (values.containsKey(PetEntry.COLUMN_PET_GENDER)) {
            // Check that the gender is valid.
            Integer gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
            if (gender == null || !PetEntry.isValidGender(gender)) {
                throw new IllegalArgumentException("Pet requires a valid gender");
            }
        }

        // Check if the weight needs to be updated.
        if (values.containsKey(PetEntry.COLUMN_PET_WEIGHT)) {
            // If the weight is provided, check that it's greater than or equal to 0 kg.
            Integer weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("The weight cannot have a negative value");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Get the database in write mode.
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        // Perform the update on the database and get the number of rows affected.
        int rowsUpdated = database.update(PetEntry.TABLE_NAME, values, selection, selectionArgs);
        // If 1 or more rows were updated, then notify all listeners that the data at the given URI has changed.
        if (rowsUpdated >= 1) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Returns the number of database rows affected by the update statement.
        return rowsUpdated;
    }

    private int deletePet(Uri uri, String selection, String[] selectionArgs) {
        // Get the database in write mode.
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        // Track the number of rows that were deleted.
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case PETS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PET_ID:
                // Delete a single row given by the ID in the URI.
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted >= 1) {
            // If 1 or more rows were deleted, then notify all listeners that the data has changed for the pet content URI.
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Returns the number of database rows affected by the update statement.
        return rowsDeleted;
    }
}