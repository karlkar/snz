package com.karol.sezonnazdrowie.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import com.karol.sezonnazdrowie.R
import com.karol.sezonnazdrowie.SnzApplication
import com.karol.sezonnazdrowie.data.ShoppingList
import com.karol.sezonnazdrowie.data.SnzDatabase

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val database: SnzDatabase = (application as SnzApplication).database // TODO: Should be private
    private val _actionBarTitle =
        MutableLiveData<String>().apply { postValue(application.getString(R.string.app_name)) }
    var isSettingsItemChanged = false
    val shoppingList: ShoppingList = ShoppingList(application)

    val actionBarTitle: LiveData<String>
        get() = _actionBarTitle

    fun setActionBarTitle(title: String) {
        _actionBarTitle.postValue(title)
    }
}
