package com.karol.sezonnazdrowie.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.karol.sezonnazdrowie.R;
import com.karol.sezonnazdrowie.data.Database;
import com.karol.sezonnazdrowie.data.FoodItem;
import com.karol.sezonnazdrowie.view.MainActivity;
import com.karol.sezonnazdrowie.view.MainActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class ListFragment extends Fragment {

    private static final String TAG = "LISTFRAGMENT";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        String what = getArguments().getString(MainActivity.INTENT_WHAT);
        switch (what) {
            case MainActivity.INTENT_WHAT_FRUITS:
                ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.season_fruits));
                break;
            case MainActivity.INTENT_WHAT_VEGETABLES:
                ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.season_vegetables));
                break;
            case MainActivity.INTENT_WHAT_INCOMING:
                ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.season_incoming));
                break;
        }

        View view = inflater.inflate(R.layout.fragment_list, container, false);

        ArrayList<FoodItem> allFruits = Database.getInstance().getAllFruits();
        if (allFruits == null || allFruits.isEmpty()) {
            Database.getInstance().loadData(getActivity());
        }

        ArrayList<FoodItem> items = null;
        switch (what) {
            case MainActivity.INTENT_WHAT_FRUITS:
                items = Database.getInstance().getCurrentFruits();
                break;
            case MainActivity.INTENT_WHAT_VEGETABLES:
                items = Database.getInstance().getCurrentVegetables();
                break;
            case MainActivity.INTENT_WHAT_INCOMING:
                items = Database.getInstance().getIncomingItems();
                break;
        }

        ListView listView = view.findViewById(R.id.listView);
        ArrayAdapter adapter;
        if (what.equals(MainActivity.INTENT_WHAT_INCOMING)) {
            adapter = new IncomingAdapter(getActivity(), items);
        } else {
            adapter = new ArrayAdapter<>(getActivity(), R.layout.row_layout, R.id.rowText, items);
        }
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view1, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(MainActivity.INTENT_ITEM, (Parcelable) parent.getItemAtPosition(position));
                Navigation.findNavController(view1).navigate(R.id.action_food_detail, bundle);
            }});
        return view;
    }

    private class IncomingAdapter extends ArrayAdapter<FoodItem> {

        private class ViewHolder {
            TextView mName;
            TextView mSeason1;
        }

        IncomingAdapter(Context context, List<FoodItem> items) {
            super(context, R.layout.row_incoming_layout, items);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row_incoming_layout, parent, false);
                holder = new ViewHolder();
                holder.mName = convertView.findViewById(R.id.rowText);
                holder.mSeason1 = convertView.findViewById(R.id.rowSeason1Text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            FoodItem item = getItem(position);
            holder.mName.setText(item.getName());
            holder.mSeason1.setText(item.getNearestSeasonString());

            return convertView;
        }
    }
}
