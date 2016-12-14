package com.karol.sezonnazdrowie.model;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.karol.sezonnazdrowie.R;
import com.karol.sezonnazdrowie.data.FoodItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FoodItemGridAdapter extends FoodItemAdapter {

    private static ColorMatrixColorFilter mGrayScaleFilter;
    static {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        mGrayScaleFilter = new ColorMatrixColorFilter(matrix);
    }

    private class ViewHolder {
        private ImageView gridImage;
    }

    public FoodItemGridAdapter(Context context, ArrayList<FoodItem> objects) {
        super(context, R.layout.grid_layout, objects);
        setNotifyOnChange(true);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.gridImage = (ImageView) convertView.findViewById(R.id.gridImageView);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        FoodItem item = getItem(position);

        Picasso.with(getContext()).load(
                getContext().getResources().getIdentifier("mini_" + item.getImage(), "drawable",
                        getContext().getPackageName()))
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(viewHolder.gridImage);

        viewHolder.gridImage.setColorFilter(item.isEnabled() ? null : mGrayScaleFilter);

        return convertView;
    }
}