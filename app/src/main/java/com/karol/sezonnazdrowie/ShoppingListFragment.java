package com.karol.sezonnazdrowie;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShoppingListFragment extends Fragment {

    public static final String PREF_SHOPPING_LIST = "SHOPPING_LIST";

    private ArrayAdapter<String> mAdapter = null;

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
        String shoppingSet = prefs.getString(PREF_SHOPPING_LIST, null);
        List<String> shoppingList;
        if (shoppingSet == null)
            shoppingList = new ArrayList<>();
        else
            shoppingList = new ArrayList<>(Arrays.asList(shoppingSet));
        ListView listView = (ListView) mRoot.findViewById(R.id.listView);
        mAdapter = new ArrayAdapter<>(getActivity(), R.layout.row_layout, R.id.rowText, shoppingList);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                        String newVal = "";
                        for (int i = 0; i < mAdapter.getCount(); ++i) {
                            if (i > 0)
                                newVal += ",";
                            newVal += mAdapter.getItem(i);
                        }
                        prefs.edit().putString(PREF_SHOPPING_LIST, newVal).apply();
                        Toast.makeText(getActivity(), getString(R.string.removed_product_name, selectedItem), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        Button addToListButton = (Button) mRoot.findViewById(R.id.addToShoppingListButton);
        addToListButton.setOnClickListener(new View.OnClickListener() {
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
                            String stringSet = prefs.getString(PREF_SHOPPING_LIST, "");
                            if (stringSet.length() > 0)
                                stringSet += ",";
                            stringSet += editText.getText().toString();
                            mAdapter.add(editText.getText().toString());
                            prefs.edit().putString(PREF_SHOPPING_LIST, stringSet).apply();
                            Toast.makeText(getActivity(), getString(R.string.added_to_shopping_list), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                builder.show();
                mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
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
