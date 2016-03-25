package com.karol.sezonnazdrowie;
import android.content.Intent;
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

			((TextView)view.findViewById(android.R.id.text1)).setText(getItem(position));
			return view;
		}
	}
	
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
        list.add("SEZON NA WARZYWA");
        list.add("SEZON NA OWOCE");
        list.add("WKRÓTCE SEZON NA");
        list.add("KALENDARZ");
        list.add("LISTA ZAKUPÓW");
        setAdapter(new DrawerAdapter(context, list));
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = (String) parent.getItemAtPosition(position);
                if (text.equals("SEZON NA WARZYWA")) {
                    Intent intent = new Intent(context, ListActivity.class);
                    intent.putExtra("WHAT", "VEGETABLES");
                    context.startActivity(intent);
                } else if (text.equals("SEZON NA OWOCE")) {
                    Intent intent = new Intent(context, ListActivity.class);
                    intent.putExtra("WHAT", "FRUITS");
                    context.startActivity(intent);
                } else if (text.equals("WKRÓTCE SEZON NA")) {

                } else if (text.equals("KALENDARZ")) {
                    Intent intent = new Intent(context, CalendarActivity.class);
                    context.startActivity(intent);
                } else if (text.equals("LISTA ZAKUPÓW")) {

                }
            }
        });
    }
}
