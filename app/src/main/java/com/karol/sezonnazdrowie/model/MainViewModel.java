package com.karol.sezonnazdrowie.model;

import android.app.Application;

import com.karol.sezonnazdrowie.R;
import com.karol.sezonnazdrowie.data.Database;
import com.karol.sezonnazdrowie.data.FoodItem;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class MainViewModel extends AndroidViewModel {

    private final Database mDatabase = new Database();
    private MutableLiveData<String> mActionBarTitle = new MutableLiveData<>();
    private boolean mSettingsItemChanged = false;

    public MainViewModel(@NonNull Application application) {
        super(application);

        mActionBarTitle.postValue(application.getString(R.string.app_name));

        ArrayList<FoodItem> allFruits = mDatabase.getAllFruits();
        if (allFruits == null || allFruits.size() == 0) {
            mDatabase.loadData(application);
        }
    }

    public void setActionBarTitle(String title) {
        mActionBarTitle.postValue(title);
    }

    public LiveData<String> getActionBarTitle() {
        return mActionBarTitle;
    }

    public Database getDatabase() {
        return mDatabase;
    }

    public boolean isSettingsItemChanged() {
        return mSettingsItemChanged;
    }

    public void setSettingsItemChanged(boolean settingsItemChanged) {
        mSettingsItemChanged = settingsItemChanged;
    }
}
