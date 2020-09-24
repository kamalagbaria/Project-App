package com.example.projectapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomAdapter implements ListAdapter {

    ArrayList<ItemAnswer> arrayList;
    Context context;
    LayoutInflater layoutInflater;
    public CustomAdapter(Context context, ArrayList<ItemAnswer> arrayList) {
        super();
        this.arrayList=arrayList;
        this.context=context;
        this.layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ItemAnswer subjectData = arrayList.get(position);

        convertView = layoutInflater.inflate(R.layout.simple_list_item_1, parent);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        if(subjectData != null) {
            TextView tittle = convertView.findViewById(R.id.title);
            ImageView imag = convertView.findViewById(R.id.answerImage);
            tittle.setText(subjectData.getContent());
            if (subjectData.getPhotoAnswer() != null) {
                Picasso.get()
                        .load(subjectData.getPhotoAnswer())
                        .into(imag);
                imag.setVisibility(View.VISIBLE);
            }
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
