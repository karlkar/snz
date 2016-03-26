package com.karol.sezonnazdrowie;

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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

/**
 * Created by Karol on 25.03.2016.
 */
public class ListFragment extends Fragment {

    ListView mListView = null;
    FoodItemAdapter mAdapter = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, null);

        ArrayList<FoodItem> items = null;
        String what = getArguments().getString("WHAT");
        if (what.equals("FRUITS")) {
            ((FragmentsActivity)getActivity()).setActionBarTitle("SEZON NA OWOCE");
            items = Database.getInstance().getCurrentFruits();
//            items = Database.getInstance().getAllFruits();
        } else if (what.equals("VEGETABLES")) {
            ((FragmentsActivity)getActivity()).setActionBarTitle("SEZON NA WARZYWA");
            items = Database.getInstance().getCurrentVegetables();
//            items = Database.getInstance().getAllVegetables();
        }

        mListView = (ListView) view.findViewById(R.id.listView);
        mAdapter = new FoodItemAdapter(getActivity(), items);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = new FoodItemPageFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("ITEM", (Parcelable) parent.getItemAtPosition(position));
                fragment.setArguments(bundle);
                ((FragmentsActivity) getActivity()).replaceFragments(fragment);
            }
        });

        AdView adView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(getString(R.string.adMobTestDeviceNote5))
                .addTestDevice(getString(R.string.adMobTestDeviceS5))
                .build();
        adView.loadAd(adRequest);
        return view;
    }

    private class FoodItemAdapter extends ArrayAdapter<FoodItem> {

        private class ViewHolder {
            TextView mText;
        }

        public FoodItemAdapter(Context context, ArrayList<FoodItem> objects) {
            super(context, R.layout.row_layout, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.row_layout, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.mText = (TextView) view.findViewById(R.id.rowText);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            FoodItem item = getItem(position);
            viewHolder.mText.setText(item.getName());

            return view;
        }
    }
}
