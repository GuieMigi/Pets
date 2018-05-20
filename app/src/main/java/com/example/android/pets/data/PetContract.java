package com.example.android.pets.data;

import android.provider.BaseColumns;

public final class PetContract {

    public static final class PetEntry implements BaseColumns {
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