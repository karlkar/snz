package com.karol.sezonnazdrowie.view.fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.karol.sezonnazdrowie.R;
import com.karol.sezonnazdrowie.data.FoodItem;
import com.karol.sezonnazdrowie.view.FragmentsActivity;

import java.util.HashSet;
import java.util.Set;

public class FoodItemPageFragment extends Fragment {

    private static final String TAG = "FOODITEMPAGEFRAGMENT";
    private FoodItem mItem;

    private ImageView mPagePreviewImageView;
    private ImageView mAddToShoppingListImageView;
    private LinearLayout mAdditionalTextsLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_food_item_page, container, false);
        mItem = getArguments().getParcelable(FragmentsActivity.INTENT_ITEM);
        ((FragmentsActivity) getActivity()).setActionBarTitle(mItem.getName());

        mPagePreviewImageView = (ImageView) view.findViewById(R.id.pagePreviewImageView);
        if (!mItem.getImage().isEmpty())
            mPagePreviewImageView.setImageResource(getResources().getIdentifier(mItem.getImage(), "drawable", getActivity().getPackageName()));
        else
            mPagePreviewImageView.setImageResource(android.R.drawable.ic_menu_gallery);

        mAddToShoppingListImageView = (ImageView) view.findViewById(R.id.addToShoppingListButton);
        mAddToShoppingListImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                Set<String> stringSet = prefs.getStringSet(ShoppingListFragment.PREF_SHOPPING_LIST, null);
                if (stringSet == null)
                    stringSet = new HashSet<>();
                else
                    stringSet = new HashSet<>(stringSet);
                stringSet.add(mItem.getName());
                prefs.edit().clear().putStringSet(ShoppingListFragment.PREF_SHOPPING_LIST, stringSet).apply();
                Toast.makeText(getActivity(), R.string.added_to_shopping_list, Toast.LENGTH_SHORT).show();
            }
        });

        mAdditionalTextsLayout = (LinearLayout) view.findViewById(R.id.additionalTextsLayout);
        if (mItem.getStartDay1() == null) {
            TextView tmp = new TextView(getActivity());
            tmp.setText(R.string.season_all_year);
            mAdditionalTextsLayout.addView(tmp);
        } else {
            String from = FoodItem.DATE_FORMAT_TEXT.format(mItem.getStartDay1().getDate());
            String to = FoodItem.DATE_FORMAT_TEXT.format(mItem.getEndDay1().getDate());
            TextView tmp = new TextView(getActivity());
            tmp.setText(getString(R.string.season_from_to, from, to));
            mAdditionalTextsLayout.addView(tmp);
            if (mItem.getStartDay2() != null) {
                from = FoodItem.DATE_FORMAT_TEXT.format(mItem.getStartDay2().getDate());
                to = FoodItem.DATE_FORMAT_TEXT.format(mItem.getEndDay2().getDate());
                tmp = new TextView(getActivity());
                tmp.setText(getString(R.string.food_detail_item, from, to));
                mAdditionalTextsLayout.addView(tmp);
            }
        }
        addSpacer();
        TextView tmp = new TextView(getActivity());
        tmp.setText(mItem.getDesc());
        mAdditionalTextsLayout.addView(tmp);
        addSpacer();
        if (mItem.hasProximates()) {
            // add wartość odżywcza
            TextView title = new TextView(getActivity());
            title.setText(R.string.proximates);
            mAdditionalTextsLayout.addView(title);

            addElementView(getString(R.string.proximate_water), mItem.getWater());
            addElementView(getString(R.string.proximate_energy), mItem.getEnergy());
            addElementView(getString(R.string.proximate_protein), mItem.getProtein());
            addElementView(getString(R.string.proximate_fat), mItem.getFat());
            addElementView(getString(R.string.proximate_carbohydrates), mItem.getCarbohydrates());
            addElementView(getString(R.string.proximate_fiber), mItem.getFiber());
            addElementView(getString(R.string.proximate_sugars), mItem.getSugars());
            addSpacer();
        }

        if (mItem.hasMinerals()) {
            // add zawartość minerałów
            TextView title = new TextView(getActivity());
            title.setText(R.string.minerals);
            mAdditionalTextsLayout.addView(title);

            addElementView(getString(R.string.minerals_calcium), mItem.getCalcium());
            addElementView(getString(R.string.minerals_iron), mItem.getIron());
            addElementView(getString(R.string.minerals_magnesium), mItem.getMagnesium());
            addElementView(getString(R.string.minerals_phosphorus), mItem.getPhosphorus());
            addElementView(getString(R.string.minerals_potassium), mItem.getPotassium());
            addElementView(getString(R.string.minerals_sodium), mItem.getSodium());
            addElementView(getString(R.string.minerals_zinc), mItem.getZinc());
            addSpacer();
        }

        if (mItem.hasVitamins()) {
            // add zawartość witamin
            TextView title = new TextView(getActivity());
            title.setText(R.string.vitamins);
            mAdditionalTextsLayout.addView(title);

            addElementView(getString(R.string.vitamins_A), mItem.getVitA());
            addElementView(getString(R.string.vitamins_C), mItem.getVitC());
            addElementView(getString(R.string.vitamins_E), mItem.getVitE());
            addElementView(getString(R.string.vitamins_B1), mItem.getThiamin());
            addElementView(getString(R.string.vitamins_B2), mItem.getRiboflavin());
            addElementView(getString(R.string.vitamins_B3), mItem.getNiacin());
            addElementView(getString(R.string.vitamins_B6), mItem.getVitB6());
            addElementView(getString(R.string.vitamins_K), mItem.getVitK());
            addElementView(getString(R.string.vitamins_folate), mItem.getFolate());
            addSpacer();
        }

        if (mItem.hasProximates() || mItem.hasMinerals() || mItem.hasVitamins()) {
            TextView title = new TextView(getActivity());
            title.setText(R.string.food_data_source);
            mAdditionalTextsLayout.addView(title);
            addSpacer();
        }
        return view;
    }

    private void addElementView(String title, String value) {
        if (!value.isEmpty()) {
            TextView tmp = new TextView(getActivity());
            tmp.setText(getString(R.string.food_detail_item, title, value));
            mAdditionalTextsLayout.addView(tmp);
        }
    }

    private void addSpacer() {
        TextView tmp = new TextView(getActivity());
        mAdditionalTextsLayout.addView(tmp);
    }
}