package com.karol.sezonnazdrowie;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

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
        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), R.layout.row_layout, R.id.rowText, items);
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
            ((ViewGroup)mRoot.getParent()).removeView(mRoot);
    }
}
