package com.karol.sezonnazdrowie.model;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.karol.sezonnazdrowie.R;
import com.karol.sezonnazdrowie.data.FoodItem;

import java.util.ArrayList;

public class FoodItemListAdapter extends FoodItemAdapter {

    private class ViewHolder {
        private TextView fruitName;
    }

    public FoodItemListAdapter(Context context, ArrayList<FoodItem> objects) {
        super(context, R.layout.row_calendar_layout, objects);
        setNotifyOnChange(true);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_calendar_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.fruitName = (TextView) convertView.findViewById(R.id.rowText);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        FoodItem item = getItem(position);
        viewHolder.fruitName.setText(item.getName().toLowerCase());
        viewHolder.fruitName.setTextColor(item.isEnabled() ? Color.BLACK : Color.GRAY);
        return convertView;
    }
}