package com.karol.sezonnazdrowie;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ListActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private TextView mActionBarTitle;
    private SnzDrawer mDrawer;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mActionBarTitle = (TextView)toolbar.findViewById(R.id.action_bar_title);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        Fragment fg = new ListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("WHAT", getIntent().getStringExtra("WHAT"));
        fg.setArguments(bundle);
        getFragmentManager().beginTransaction().add(R.id.contentView, fg).addToBackStack(null).commit();

        mDrawer = (SnzDrawer) findViewById(R.id.left_drawer);
        mDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = (String) parent.getItemAtPosition(position);
                if (text.equals("SEZON NA WARZYWA")) {
                    Fragment fragment = new ListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("WHAT", "VEGETABLES");
                    fragment.setArguments(bundle);
                    replaceFragments(fragment);
                } else if (text.equals("SEZON NA OWOCE")) {
                    Fragment fragment = new ListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("WHAT", "FRUITS");
                    fragment.setArguments(bundle);
                    replaceFragments(fragment);
                } else if (text.equals("WKRÓTCE SEZON NA")) {

                } else if (text.equals("KALENDARZ")) {
                    Intent intent = new Intent(ListActivity.this, CalendarActivity.class);
                    startActivity(intent);
                } else if (text.equals("LISTA ZAKUPÓW")) {

                }
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void replaceFragments(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        if (fragment instanceof ListFragment)
            getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentTransaction.replace(R.id.contentView, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void setActionBarTitle(String text) {
        mActionBarTitle.setText(text);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 1)
            getFragmentManager().popBackStack();
        else
            super.onBackPressed();
    }

    public static class ListFragment extends Fragment {

        ListView mListView = null;
        FoodItemAdapter mAdapter = null;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_list, null);

            ArrayList<FoodItem> items = null;
            String what = getArguments().getString("WHAT");
            if (what.equals("FRUITS")) {
                ((ListActivity)getActivity()).setActionBarTitle("SEZON NA OWOCE");
                items = Database.getInstance().getCurrentFruits();
//            items = Database.getInstance().getAllFruits();
            } else if (what.equals("VEGETABLES")) {
                ((ListActivity)getActivity()).setActionBarTitle("SEZON NA WARZYWA");
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
                    ((ListActivity) getActivity()).replaceFragments(fragment);
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

    public static class FoodItemPageFragment extends Fragment {

        private FoodItem mItem;

        private ImageView mPagePreviewImageView;
        private LinearLayout mAdditionalTextsLayout;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_food_item_page, null);
            mItem = getArguments().getParcelable("ITEM");
            ((ListActivity) getActivity()).setActionBarTitle(mItem.getName());

            mPagePreviewImageView = (ImageView) view.findViewById(R.id.pagePreviewImageView);
            if (!mItem.getImage().isEmpty())
                mPagePreviewImageView.setImageResource(getResources().getIdentifier(mItem.getImage(), "drawable", getActivity().getPackageName()));
            else
                mPagePreviewImageView.setImageResource(android.R.drawable.ic_menu_gallery);

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
}
