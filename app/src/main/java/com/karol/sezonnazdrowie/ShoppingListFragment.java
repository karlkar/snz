package com.karol.sezonnazdrowie;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Karol on 28.03.2016.
 */
public class ShoppingListFragment extends Fragment {

    public static final String PREF_SHOPPING_LIST = "SHOPPING_LIST";

    private ListView mListView = null;
    private ArrayAdapter mAdapter = null;
    private Button mAddToListButton = null;

    private View mRoot = null;
    private InputMethodManager mInputMethodManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((FragmentsActivity)getActivity()).setActionBarTitle(getString(R.string.shopping_list));

        if (mRoot != null)
            return mRoot;

        mInputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        mRoot = inflater.inflate(R.layout.fragment_shopping_list, null);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Set<String> shoppingSet = prefs.getStringSet(PREF_SHOPPING_LIST, null);
        ArrayList shoppingList;
        if (shoppingSet == null)
            shoppingList = new ArrayList();
        else
            shoppingList = new ArrayList(shoppingSet);
        mListView = (ListView) mRoot.findViewById(R.id.listView);
        mAdapter = new ArrayAdapter<>(getActivity(), R.layout.row_layout, R.id.rowText, shoppingList);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String selectedItem = (String) parent.getItemAtPosition(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.shopping_list_delete_dialog_title));
                builder.setMessage(getString(R.string.shopping_list_delete_dialog_message, selectedItem));
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAdapter.remove(selectedItem);
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        Set<String> stringSet = prefs.getStringSet(PREF_SHOPPING_LIST, new HashSet<String>(1));
                        stringSet.remove(selectedItem);
                        prefs.edit().clear().putStringSet(PREF_SHOPPING_LIST, stringSet).apply();
                        Toast.makeText(getActivity(), getString(R.string.removed_product_name, selectedItem), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        mAddToListButton = (Button) mRoot.findViewById(R.id.addToShoppingListButton);
        mAddToListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.shopping_list_add_dialog_title));
                builder.setMessage(getString(R.string.shopping_list_add_dialog_message));
                final EditText editText = new EditText(getActivity());
                builder.setView(editText);
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton(getString(R.string.add), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText().toString().isEmpty()) {
                            Toast.makeText(getActivity(), R.string.empty_product_name_message, Toast.LENGTH_LONG).show();
                        } else {
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                            Set<String> stringSet = prefs.getStringSet(PREF_SHOPPING_LIST, new HashSet<String>(1));
                            stringSet.add(editText.getText().toString());
                            mAdapter.add(editText.getText().toString());
                            prefs.edit().clear().putStringSet(PREF_SHOPPING_LIST, stringSet).apply();
                            Toast.makeText(getActivity(), getString(R.string.added_to_shopping_list), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                builder.show();
                mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
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
