package com.karol.sezonnazdrowie;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

/**
 * Created by Karol on 25.03.2016.
 */
public class ListFragment extends Fragment {

    private ListView mListView = null;
    private ArrayAdapter mAdapter = null;

    private View mRoot = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRoot != null)
            return mRoot;

        mRoot = inflater.inflate(R.layout.fragment_list, null);

        ArrayList<FoodItem> items = null;
        String what = getArguments().getString(FragmentsActivity.INTENT_WHAT);
        if (what.equals(FragmentsActivity.INTENT_WHAT_FRUITS)) {
            ((FragmentsActivity)getActivity()).setActionBarTitle(getString(R.string.season_fruits));
            items = Database.getInstance().getCurrentFruits();
//            items = Database.getInstance().getAllFruits();
        } else if (what.equals(FragmentsActivity.INTENT_WHAT_VEGETABLES)) {
            ((FragmentsActivity)getActivity()).setActionBarTitle(getString(R.string.season_vegetables));
            items = Database.getInstance().getCurrentVegetables();
//            items = Database.getInstance().getAllVegetables();
        }

        mListView = (ListView) mRoot.findViewById(R.id.listView);
        mAdapter = new ArrayAdapter<>(getActivity(), R.layout.row_layout, R.id.rowText, items);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = new FoodItemPageFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable(FragmentsActivity.INTENT_ITEM, (Parcelable) parent.getItemAtPosition(position));
                fragment.setArguments(bundle);
                ((FragmentsActivity) getActivity()).replaceFragments(fragment);
            }
        });

        final AdView adView = (AdView) mRoot.findViewById(R.id.adView);
        adView.setVisibility(View.GONE);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(getString(R.string.adMobTestDeviceNote5))
                .addTestDevice(getString(R.string.adMobTestDeviceS5))
                .build();
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                TransitionManager.beginDelayedTransition((ViewGroup) mRoot);
                adView.setVisibility(View.VISIBLE);
            }
        });
        adView.loadAd(adRequest);
        return mRoot;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mRoot != null && mRoot.getParent() != null)
            ((ViewGroup)mRoot.getParent()).removeView(mRoot);
    }
}
