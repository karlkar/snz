package com.karol.sezonnazdrowie.model;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.karol.sezonnazdrowie.R;
import com.karol.sezonnazdrowie.data.FoodItem;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SnzAdapter extends RecyclerView.Adapter<SnzAdapter.SnzViewHolder> {

    private static ColorMatrixColorFilter mGrayScaleFilter;
    static {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        mGrayScaleFilter = new ColorMatrixColorFilter(matrix);
    }

    public interface OnItemClickListener {
        void onClicked(FoodItem position, int adapterPosition);
    }

    List<FoodItem> mItems;
    private boolean mGridMode;
    OnItemClickListener mOnItemClickListener;

    public SnzAdapter(
            @NonNull List<FoodItem> items,
            boolean gridMode,
            @NonNull OnItemClickListener onItemClickListener) {
        mItems = items;
        mGridMode = gridMode;
        mOnItemClickListener = onItemClickListener;
    }

    public void setGridMode(boolean gridViewMode) {
        mGridMode = gridViewMode;
    }

    @Override
    public SnzViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item, parent, false);
        return new SnzViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SnzViewHolder holder, int position) {
        final FoodItem item = mItems.get(position);
        if (mGridMode) {
            Context context = holder.image.getContext();
            Picasso.with(context).load(
                    context.getResources().getIdentifier("mini_" + item.getImage(), "drawable",
                            context.getPackageName()))
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(holder.image);
            holder.image.setColorFilter(item.isEnabled() ? null : mGrayScaleFilter);
            holder.text.setVisibility(View.GONE);
            holder.image.setVisibility(View.VISIBLE);
        } else {
            holder.text.setText(item.getName().toLowerCase());
            holder.text.setTextColor(item.isEnabled() ? Color.BLACK : Color.GRAY);
            holder.text.setVisibility(View.VISIBLE);
            holder.image.setVisibility(View.GONE);
        }
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onClicked(item, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void enableItemsAt(CalendarDay date) {
        for (int i = 0; i < mItems.size(); ++i) {
            FoodItem cur = mItems.get(i);
            cur.setEnabled(cur.existsAt(date));
        }
        sortItems();
    }

    public void enableItemAt(int position) {
        for (int i = 0; i < mItems.size(); ++i) {
            mItems.get(i).setEnabled(i == position);
        }
        sortItems();
    }

    private void sortItems() {
        Collections.sort(mItems, new Comparator<FoodItem>() {
            @Override
            public int compare(FoodItem lhs, FoodItem rhs) {
                if (lhs.isEnabled() == rhs.isEnabled())
                    return lhs.compareTo(rhs);
                return lhs.isEnabled() ? -1 : 1;
            }
        });
    }

    class SnzViewHolder extends RecyclerView.ViewHolder {
        View root;
        ImageView image;
        TextView text;

        public SnzViewHolder(View itemView) {
            super(itemView);
            root = itemView;
            image = (ImageView) itemView.findViewById(R.id.image);
            text = (TextView) itemView.findViewById(R.id.text);
        }
    }
}