package com.example.android.pets.data;

import android.net.Uri;
import android.provider.BaseColumns;

public final class PetContract {

    // The content authority is a name for the entire content provider.
    public static final String CONTENT_AUTHORITY = "com.example.android.pets";
    // The base content uri that contains the scheme and the content authority.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    // The path for the pets table.
    public static final String PATH_PETS = "pets";

    private PetContract() {
    }

    public static final class PetEntry implements BaseColumns {

        // The content URI to access the pet data in the provider.
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);
        // Table name.
        public static final String TABLE_NAME = "pets";
        // The _Id column.
        public static final String _ID = BaseColumns._ID;
        // The Name column.
        public static final String COLUMN_PET_NAME = "Name";
        // The Breed column.
        public static final String COLUMN_PET_BREED = "Breed";
        // The Gender column.
        public static final String COLUMN_PET_GENDER = "Gender";
        // The Weight column.
        public static final String COLUMN_PET_WEIGHT = "Weight";

        // Possible values for the gender of the pet.
        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;
    }
}