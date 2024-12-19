package com.mouseboy.finalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mouseboy.finalproject.server.Walk;

import java.util.List;

public class ArrayListAdaptor extends ArrayAdapter<Walk> {

    private final Context context;
    private final List<Walk> items;

    public ArrayListAdaptor(Context context, List<Walk> items) {
        super(context, android.R.layout.simple_list_item_1, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate the custom layout
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        // Get the current item
        Walk currentItem = items.get(position);

        // Populate the layout with data
        TextView itemText = convertView.findViewById(android.R.id.text1);

        itemText.setText(currentItem.toString());

        return convertView;
    }
}
