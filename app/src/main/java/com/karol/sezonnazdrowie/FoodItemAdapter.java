package com.karol.sezonnazdrowie;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.Comparator;
import java.util.List;

public abstract class FoodItemAdapter extends ArrayAdapter<FoodItem> {

    public FoodItemAdapter(Context context, int resource, List<FoodItem> objects) {
        super(context, resource, objects);
    }

    public void enableItemsAt(CalendarDay date) {
        for (int i = 0; i < getCount(); ++i) {
            FoodItem cur = getItem(i);
            cur.setEnabled(cur.existsAt(date));
        }
    }

    public void enableItemAt(int position) {
        for (int i = 0; i < getCount(); ++i) {
            getItem(i).setEnabled(i == position);
        }
    }

    public void sortItems() {
        sort(new Comparator<FoodItem>() {
            @Override
            public int compare(FoodItem lhs, FoodItem rhs) {
                if (lhs.isEnabled() == rhs.isEnabled())
                    return lhs.compareTo(rhs);
                return lhs.isEnabled() ? -1 : 1;
            }
        });
    }
}
