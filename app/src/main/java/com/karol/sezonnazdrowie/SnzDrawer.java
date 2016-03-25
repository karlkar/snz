package com.karol.sezonnazdrowie;
import android.widget.ListView;
import android.content.Context;
import android.util.*;
import android.widget.*;
import java.util.*;
import android.view.*;

public class SnzDrawer extends ListView
{
	private class DrawerAdapter extends ArrayAdapter<String> {
		
		public DrawerAdapter(Context ctx, ArrayList<String> elems) {
			super(ctx, android.R.layout.simple_list_item_1, elems);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View view = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
				view = convertView;
			} else {
				view = convertView;
			}
			// TODO: Implement this method
			return view;
		}
		
		
	}
	
	public SnzDrawer(Context context) {
		super(context);
	}

	public SnzDrawer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
		
	public SnzDrawer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}	
}
