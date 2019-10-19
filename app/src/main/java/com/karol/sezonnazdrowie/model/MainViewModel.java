package com.karol.sezonnazdrowie.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.karol.sezonnazdrowie.R;
import com.karol.sezonnazdrowie.SnzApplication;
import com.karol.sezonnazdrowie.data.ShoppingList;
import com.karol.sezonnazdrowie.data.SnzDatabase;

public class MainViewModel extends AndroidViewModel {

    private final SnzDatabase mDatabase;
    private MutableLiveData<String> mActionBarTitle = new MutableLiveData<>();
    private boolean mSettingsItemChanged = false;
    private ShoppingList mShoppingList;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mDatabase = ((SnzApplication)application).getDatabase();
        mShoppingList = new ShoppingList(application);

        mActionBarTitle.postValue(application.getString(R.string.app_name));
    }

    public void setActionBarTitle(String title) {
        mActionBarTitle.postValue(title);
    }

    public LiveData<String> getActionBarTitle() {
        return mActionBarTitle;
    }

    public SnzDatabase getDatabase() {
        return mDatabase;
    }

    public boolean isSettingsItemChanged() {
        return mSettingsItemChanged;
    }

    public void setSettingsItemChanged(boolean settingsItemChanged) {
        mSettingsItemChanged = settingsItemChanged;
    }

    public ShoppingList getShoppingList() {
        return mShoppingList;
    }
}
