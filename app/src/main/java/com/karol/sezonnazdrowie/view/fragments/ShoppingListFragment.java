package com.karol.sezonnazdrowie.view.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.karol.sezonnazdrowie.R;
import com.karol.sezonnazdrowie.model.MainViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class ShoppingListFragment extends Fragment {

    private static final String TAG = "SHOPPINGLISTFRAGMENT";

    private ArrayAdapter<String> mAdapter = null;
    private InputMethodManager mInputMethodManager;
    private MainViewModel mMainViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        mMainViewModel.setActionBarTitle(getString(R.string.shopping_list));

        mInputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);

        List<String> shoppingList = mMainViewModel.getShoppingList().getItems();
        ListView listView = view.findViewById(R.id.listView);
        mAdapter = new ArrayAdapter<>(getActivity(), R.layout.row_layout, R.id.rowText, shoppingList);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view1, int position, long id) {
                final String selectedItem = (String) parent.getItemAtPosition(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.shopping_list_delete_dialog_title));
                builder.setMessage(getString(R.string.shopping_list_delete_dialog_message, selectedItem));
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        mAdapter.remove(selectedItem);
                        mMainViewModel.getShoppingList().deleteItem(selectedItem);
                        Toast.makeText(
                                getActivity(),
                                getString(R.string.removed_product_name, selectedItem),
                                Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        ImageView addToListButton = view.findViewById(R.id.addToShoppingListButton);
        addToListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.shopping_list_add_dialog_title));
                builder.setMessage(getString(R.string.shopping_list_add_dialog_message));
                final EditText editText = new EditText(getActivity());
                editText.setLines(1);
                editText.setSingleLine();
                editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                builder.setView(editText);
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton(
                        getString(R.string.add),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                acceptInput(editText, dialog);
                            }
                        });
                final AlertDialog alertDialog = builder.create();
                editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v1, int actionId, KeyEvent event) {
                        acceptInput(editText, alertDialog);
                        return true;
                    }
                });
                alertDialog.show();
                mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });
        return view;
    }

    private void acceptInput(EditText editText, DialogInterface alertDialog) {
        if (editText.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), R.string.empty_product_name_message, Toast.LENGTH_LONG).show();
        } else {
            mMainViewModel.getShoppingList().addItem(editText.getText().toString());
            mAdapter.add(editText.getText().toString());
            mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            Toast.makeText(getActivity(), getString(R.string.added_to_shopping_list), Toast.LENGTH_LONG).show();
            alertDialog.dismiss();
        }
    }
}
