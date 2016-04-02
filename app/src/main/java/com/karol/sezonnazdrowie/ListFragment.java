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
        String what = getArguments().getString(FragmentsActivity.INTENT_WHAT);
        if (what.equals(FragmentsActivity.INTENT_WHAT_FRUITS))
            ((FragmentsActivity)getActivity()).setActionBarTitle(getString(R.string.season_fruits));
        else if (what.equals(FragmentsActivity.INTENT_WHAT_VEGETABLES))
            ((FragmentsActivity)getActivity()).setActionBarTitle(getString(R.string.season_vegetables));
		else if (what.equals(FragmentsActivity.INTENT_WHAT_INCOMING))
			((FragmentsActivity)getActivity()).setActionBarTitle(getString(R.string.season_incoming));
			
		if (mRoot != null)
            return mRoot;

        mRoot = inflater.inflate(R.layout.fragment_list, null);

        ArrayList<FoodItem> items = null;
        if (what.equals(FragmentsActivity.INTENT_WHAT_FRUITS)) {
            items = Database.getInstance().getCurrentFruits();
//            items = Database.getInstance().getAllFruits();
        } else if (what.equals(FragmentsActivity.INTENT_WHAT_VEGETABLES)) {
            items = Database.getInstance().getCurrentVegetables();
//            items = Database.getInstance().getAllVegetables();
        } else if (what.equals(FragmentsActivity.INTENT_WHAT_INCOMING))
			items = Database.getInstance().getIncomingItems();

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
        return mRoot;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mRoot != null && mRoot.getParent() != null)
            ((ViewGroup)mRoot.getParent()).removeView(mRoot);
    }
}
