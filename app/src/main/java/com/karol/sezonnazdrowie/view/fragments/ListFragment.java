package com.karol.sezonnazdrowie.view.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.karol.sezonnazdrowie.data.Database;
import com.karol.sezonnazdrowie.data.FoodItem;
import com.karol.sezonnazdrowie.R;
import com.karol.sezonnazdrowie.view.FragmentsActivity;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {

    private View mRoot = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String what = getArguments().getString(FragmentsActivity.INTENT_WHAT);
        switch (what) {
            case FragmentsActivity.INTENT_WHAT_FRUITS:
                ((FragmentsActivity) getActivity()).setActionBarTitle(getString(R.string.season_fruits));
                break;
            case FragmentsActivity.INTENT_WHAT_VEGETABLES:
                ((FragmentsActivity) getActivity()).setActionBarTitle(getString(R.string.season_vegetables));
                break;
            case FragmentsActivity.INTENT_WHAT_INCOMING:
                ((FragmentsActivity) getActivity()).setActionBarTitle(getString(R.string.season_incoming));
                break;
        }

        if (mRoot != null)
            return mRoot;

        mRoot = inflater.inflate(R.layout.fragment_list, null);

        if (Database.getInstance().getAllFruits() == null)
            Database.getInstance().loadData(getActivity());

        ArrayList<FoodItem> items = null;
        switch (what) {
            case FragmentsActivity.INTENT_WHAT_FRUITS:
                items = Database.getInstance().getCurrentFruits();
//            items = Database.getInstance().getAllFruits();
                break;
            case FragmentsActivity.INTENT_WHAT_VEGETABLES:
                items = Database.getInstance().getCurrentVegetables();
//            items = Database.getInstance().getAllVegetables();
                break;
            case FragmentsActivity.INTENT_WHAT_INCOMING:
                items = Database.getInstance().getIncomingItems();
                break;
        }

        ListView listView = (ListView) mRoot.findViewById(R.id.listView);
        ArrayAdapter adapter;
        if (what.equals(FragmentsActivity.INTENT_WHAT_INCOMING))
            adapter = new IncomingAdapter(getActivity(), R.layout.row_incoming_layout, items);
        else
            adapter = new ArrayAdapter<>(getActivity(), R.layout.row_layout, R.id.rowText, items);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = new FoodItemPageFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable(FragmentsActivity.INTENT_ITEM, (Parcelable) parent.getItemAtPosition(position));
                fragment.setArguments(bundle);
                ((FragmentsActivity) getActivity()).replaceFragments(fragment);
            }
        });
        return mRoot;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mRoot != null && mRoot.getParent() != null)
            ((ViewGroup) mRoot.getParent()).removeView(mRoot);
    }

    private class IncomingAdapter extends ArrayAdapter<FoodItem> {

        private class ViewHolder {
            TextView mName;
            TextView mSeason1;
        }

        public IncomingAdapter(Context context, int resource, List<FoodItem> items) {
            super(context, resource, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row_incoming_layout, parent, false);
                holder = new ViewHolder();
                holder.mName = (TextView) convertView.findViewById(R.id.rowText);
                holder.mSeason1 = (TextView) convertView.findViewById(R.id.rowSeason1Text);
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