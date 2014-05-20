package com.fruitmill.grapes.adapter;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.fruitmill.grapes.R;

public class MapSearchAdapter extends CursorAdapter {
	 
    private List addresses;
 
    private TextView text;
 
    public MapSearchAdapter(Context context, Cursor cursor, List addresses) {
 
        super(context, cursor, false);
 
        this.addresses = addresses;
 
    }
 
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
 
    	Address address = (Address) addresses.get(cursor.getPosition());
        
    	String addressText = String.format("%s, %s",
				address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
						address.getCountryName());
        text.setText(addressText);
 
    }
 
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
 
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
        View view = inflater.inflate(R.layout.search_suggestion, parent, false);
 
        text = (TextView) view.findViewById(R.id.suggestion_text);
 
        return view;
 
    }
 
}