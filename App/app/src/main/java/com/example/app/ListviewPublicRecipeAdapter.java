package com.example.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ListviewPublicRecipeAdapter extends BaseAdapter {

    private final Context context;
    private final List<Pair<String, String>> list;
    LayoutInflater mInflater;


    public ListviewPublicRecipeAdapter(Context context, List<Pair<String, String>> list){
        this.context = context;
        this.list = list;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = mInflater.inflate(R.layout.listviewpublic_adapter, null);

        final TextView name = (TextView) convertView.findViewById(R.id.name);
        final TextView amount = (TextView) convertView.findViewById(R.id.number);

        name.setText(list.get(position).first);
        amount.setText(list.get(position).second);


        return convertView;
    }

    @Override
    public int getCount() {return list.size();}

    @Override
    public Object getItem(int arg0) {return arg0;}

    @Override
    public long getItemId(int arg0) {return arg0;}

}