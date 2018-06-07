/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.pets.data.PetContract.PetEntry;


/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // Tag for the log messages.
    public static final String LOG_TAG = EditorActivity.class.getSimpleName();
    private static final int PET_CURSOR_LOADER_ID = 1;
    // The variable that stores the id of the clicked pet.
    long currentPetId;
    // EditText field to enter the pet's name.
    private EditText mNameEditText;
    // EditText field to enter the pet's breed.
    private EditText mBreedEditText;
    // EditText field to enter the pet's weight.
    private EditText mWeightEditText;
    // EditText field to enter the pet's gender.
    private Spinner mGenderSpinner;
    // Gender of the pet. The possible values are:
    // 0 for unknown gender, 1 for male, 2 for female.
    private int mGender = 0;
    // The variable that stores the Uri for the clicked pet.
    private Uri currentPetUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.edit_pet_name);
        mBreedEditText = findViewById(R.id.edit_pet_breed);
        mWeightEditText = findViewById(R.id.edit_pet_weight);
        mGenderSpinner = findViewById(R.id.spinner_gender);
        currentPetUri = getIntent().getData();
        currentPetId = getIntent().getLongExtra("PET_ID", 0);

        if (currentPetUri == null) {
            setTitle("Add Pet");
        } else setTitle("Edit Pet");
        getLoaderManager().initLoader(PET_CURSOR_LOADER_ID, null, this);

        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetEntry.GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetEntry.GENDER_FEMALE; // Female
                    } else {
                        mGender = PetEntry.GENDER_UNKNOWN; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = 0; // Unknown
            }
        });
    }

    private void savePet() {
        // Determine if this is a new or existing pet by checking if mCurrentPetUri is null or not.
        if (currentPetUri == null) {
            // Read from input fields. Use trim to eliminate leading or trailing white space.
            String nameString = mNameEditText.getText().toString().trim();
            String breedString = mBreedEditText.getText().toString().trim();
            String weightString = mWeightEditText.getText().toString().trim();
            if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(breedString) || mGender == PetEntry.GENDER_UNKNOWN) {
                Toast.makeText(this, "Please enter all the pet details", Toast.LENGTH_LONG).show();
                return;
            }
            int weightInt = 0;
            // Check if the weightString is empty.
            if (!TextUtils.isEmpty(weightString)) {
                weightInt = Integer.parseInt(weightString);
            }

            // Create a ContentValues object where column names are the keys and pet attributes from the editors are the values.
            ContentValues values = new ContentValues();
            values.put(PetEntry.COLUMN_PET_NAME, nameString);
            values.put(PetEntry.COLUMN_PET_BREED, breedString);
            values.put(PetEntry.COLUMN_PET_GENDER, mGender);
            values.put(PetEntry.COLUMN_PET_WEIGHT, weightInt);
            // Call the ContentResolver to insert a new row for pet in the database.
            Uri newUri = getContentResolver().insert(PetEntry.CONTENT_URI, values);


            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_pet_failed), Toast.LENGTH_LONG).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_pet_successful),
                        Toast.LENGTH_LONG).show();
            }
        } else {
            ContentValues values = new ContentValues();
            values.put(PetEntry.COLUMN_PET_NAME, mNameEditText.getText().toString().trim());
            values.put(PetEntry.COLUMN_PET_BREED, mBreedEditText.getText().toString().trim());
            values.put(PetEntry.COLUMN_PET_GENDER, mGender);
            values.put(PetEntry.COLUMN_PET_WEIGHT, mWeightEditText.getText().toString().trim());
            String selection = PetEntry._ID + "=?";
            String[] selectionArgs = new String[]{String.valueOf(currentPetId)};
            int rowsAffected = getContentResolver().update(currentPetUri, values, selection, selectionArgs);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_pet_failed), Toast.LENGTH_LONG).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_pet_successful), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to the database.
                savePet();
                // Exit the activity.
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {

        if (currentPetUri == null) {
            return null;
        } else {
            String[] projection = {
                    PetEntry._ID,
                    PetEntry.COLUMN_PET_NAME,
                    PetEntry.COLUMN_PET_BREED,
                    PetEntry.COLUMN_PET_GENDER,
                    PetEntry.COLUMN_PET_WEIGHT
            };

            return new CursorLoader(this, currentPetUri, projection, null, null, null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Figure out the index of each column.
        int nameColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
        int breedColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);
        int genderColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
        int weightColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);

        cursor.moveToFirst();
        // Use that index to extract the String or Int value of the word at the current row the cursor is on.
        String currentName = cursor.getString(nameColumnIndex);
        String currentBreed = cursor.getString(breedColumnIndex);
        int currentGender = cursor.getInt(genderColumnIndex);
        int currentWeight = cursor.getInt(weightColumnIndex);

        // Set the values inside the Views.
        mNameEditText.setText(currentName);
        mBreedEditText.setText(currentBreed);
        mGenderSpinner.setSelection(currentGender);
        mWeightEditText.setText(String.valueOf(currentWeight));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Clear the values from the Views.
        mNameEditText.getText().clear();
        mBreedEditText.getText().clear();
        mGenderSpinner.setSelection(PetEntry.GENDER_UNKNOWN);
        mWeightEditText.getText().clear();
    }
}