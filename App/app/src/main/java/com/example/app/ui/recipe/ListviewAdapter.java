package com.example.app.ui.recipe;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;

import com.example.app.R;

import java.util.List;
import java.util.Map;

public class ListviewAdapter extends BaseAdapter {

    private final Context context;
    private final List<Pair<String, String>> list;
    LayoutInflater mInflater;


    public ListviewAdapter(Context context, List<Pair<String, String>> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        final ViewHolder holder = new ViewHolder();
        final ViewHolder holder2 = new ViewHolder();

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = mInflater.inflate(R.layout.listview_adapter, null);
        final EditText name = (EditText) convertView.findViewById(R.id.name);
        final EditText amount = (EditText) convertView.findViewById(R.id.number);
        name.setText(list.get(position).first);
        amount.setText(list.get(position).second);

        holder.caption = amount;
        holder.caption.setTag(position);
        convertView.setTag(holder);
        int tag_position=(Integer) holder.caption.getTag();
        holder.caption.setId(tag_position);

        holder2.caption = name;
        holder2.caption.setTag(position);
        convertView.setTag(holder2);
        int tag_position2=(Integer) holder2.caption.getTag();
        holder2.caption.setId(tag_position2);


        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final int position = holder.caption.getId();
                Pair<String, String> pair = new Pair<>(holder2.caption.getText().toString(), holder.caption.getText().toString());
                list.set(position,pair);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}

        };

        name.addTextChangedListener(afterTextChangedListener);
        amount.addTextChangedListener(afterTextChangedListener);
        return convertView;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    public boolean getEmpty(){
        for(Pair<String,String> entry: list){
            if(!entry.first.equals("") && !entry.second.equals("")) return true;
        }
        return false;
    }

    public List<Pair<String, String>> getList() {
        return list;
    }
}

class ViewHolder {
    EditText caption;
}
