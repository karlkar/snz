package com.karol.sezonnazdrowie;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Karol on 25.03.2016.
 */
public class FoodItemPageFragment extends Fragment {

    private FoodItem mItem;

    private ImageView mPagePreviewImageView;
    private Button mAddToShoppingListBtn;
    private LinearLayout mAdditionalTextsLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_item_page, null);
        mItem = getArguments().getParcelable("ITEM");
        ((FragmentsActivity) getActivity()).setActionBarTitle(mItem.getName());

        mPagePreviewImageView = (ImageView) view.findViewById(R.id.pagePreviewImageView);
        if (!mItem.getImage().isEmpty())
            mPagePreviewImageView.setImageResource(getResources().getIdentifier(mItem.getImage(), "drawable", getActivity().getPackageName()));
        else
            mPagePreviewImageView.setImageResource(android.R.drawable.ic_menu_gallery);

        mAddToShoppingListBtn = (Button) view.findViewById(R.id.addToShoppingListButton);
        mAddToShoppingListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                Set<String> stringSet = prefs.getStringSet(ShoppingListFragment.PREF_SHOPPING_LIST, new HashSet<String>(1));
                stringSet.add(mItem.getName());
                prefs.edit().putStringSet(ShoppingListFragment.PREF_SHOPPING_LIST, stringSet).commit();
                Toast.makeText(getActivity(), "DODANO DO LISTY ZAKUPÓW!", Toast.LENGTH_SHORT).show();
            }
        });

        mAdditionalTextsLayout = (LinearLayout) view.findViewById(R.id.additionalTextsLayout);
        TextView tmp = new TextView(getActivity());
        tmp.setText(mItem.getDesc());
        mAdditionalTextsLayout.addView(tmp);
        addSpacer();
        if (mItem.hasProximates()) {
            // add wartość odżywcza
            TextView title = new TextView(getActivity());
            title.setText("Wartość odżywcza (100g)");
            mAdditionalTextsLayout.addView(title);

            addElementView("woda", mItem.getWater());
            addElementView("kalorie", mItem.getEnergy());
            addElementView("białka", mItem.getProtein());
            addElementView("tłuszcze", mItem.getFat());
            addElementView("węglowodany", mItem.getCarbohydrates());
            addElementView("błonnik", mItem.getFiber());
            addElementView("cukry", mItem.getSugars());
            addSpacer();
        }

        if (mItem.hasMinerals()) {
            // add zawartość minerałów
            TextView title = new TextView(getActivity());
            title.setText("Zawartość mikro- i makroelementów (100g)");
            mAdditionalTextsLayout.addView(title);

            addElementView("wapń", mItem.getCalcium());
            addElementView("żelazo", mItem.getIron());
            addElementView("magnez", mItem.getMagnesium());
            addElementView("fosfor", mItem.getPhosphorus());
            addElementView("potas", mItem.getPotassium());
            addElementView("sód", mItem.getSodium());
            addElementView("cynk", mItem.getZinc());
            addSpacer();
        }

        if (mItem.hasVitamins()) {
            // add zawartość witamin
            TextView title = new TextView(getActivity());
            title.setText("Witaminy (100g)");
            mAdditionalTextsLayout.addView(title);

            addElementView("witamina A", mItem.getVitA());
            addElementView("witamina C", mItem.getVitC());
            addElementView("witamina E", mItem.getVitE());
            addElementView("witamina B1 (tiamina)", mItem.getThiamin());
            addElementView("witamina B2 (ryboflawina)", mItem.getRiboflavin());
            addElementView("witamina B3 (niacyna)", mItem.getNiacin());
            addElementView("witamina B6", mItem.getVitB6());
            addElementView("witamina K", mItem.getVitK());
            addElementView("kwas foliowy", mItem.getFolate());
            addSpacer();
        }

        if (mItem.hasProximates() || mItem.hasMinerals() || mItem.hasVitamins()) {
            TextView title = new TextView(getActivity());
            title.setText("Źródło: USDA National Nutrient Database");
            mAdditionalTextsLayout.addView(title);
            addSpacer();
        }

        AdView adView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(getString(R.string.adMobTestDeviceNote5))
                .addTestDevice(getString(R.string.adMobTestDeviceS5))
                .build();
        adView.loadAd(adRequest);
        return view;
    }

    private void addElementView(String title, String value) {
        if (!value.isEmpty()) {
            TextView tmp = new TextView(getActivity());
            tmp.setText(title + " - " + value);
            mAdditionalTextsLayout.addView(tmp);
        }
    }

    private void addSpacer() {
        TextView tmp = new TextView(getActivity());
        mAdditionalTextsLayout.addView(tmp);
    }
}