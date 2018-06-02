package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.data.PetContract.PetEntry;

public class PetCursorAdapter extends CursorAdapter {

    public PetCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    // Makes a new blank list item view. No data is set (or bound) to the views yet.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    // This method binds the pet data (in the current row pointed to by cursor) to the given list item layout.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout.
        TextView petNameTextView = view.findViewById(R.id.pet_name);
        TextView petBreedTextView = view.findViewById(R.id.pet_breed);

        // Read the pet attributes from the Cursor for the current pet.
        String name = cursor.getString(cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME));
        String breed = cursor.getString(cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED));

        // Update the TextViews with the attributes for the current pet.
        petNameTextView.setText(name);
        petBreedTextView.setText(breed);
    }
}