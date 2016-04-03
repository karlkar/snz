package com.karol.sezonnazdrowie;
import android.content.Intent;
import android.graphics.Color;
import android.widget.ListView;
import android.content.Context;
import android.util.*;
import android.widget.*;
import java.util.*;
import android.view.*;

public class SnzDrawer extends ListView {
	public SnzDrawer(Context context) {
		super(context);
        init(context);
	}

	public SnzDrawer(Context context, AttributeSet attrs) {
		super(context, attrs);
        init(context);
	}
		
	public SnzDrawer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
        init(context);
	}

    private void init(final Context context) {
        ArrayList<String> list = new ArrayList<>();
        list.add(context.getString(R.string.season_fruits));
        list.add(context.getString(R.string.season_vegetables));
        list.add(context.getString(R.string.season_incoming));
        list.add(context.getString(R.string.calendar));
        list.add(context.getString(R.string.shopping_list));
        list.add(context.getString(R.string.settings));
		setAdapter(new ArrayAdapter<>(context, R.layout.drawer_row_layout, R.id.rowText, list));
    }
}
