package com.karol.sezonnazdrowie.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.karol.sezonnazdrowie.R
import com.karol.sezonnazdrowie.data.FoodItem
import com.karol.sezonnazdrowie.model.MainViewModel
import com.karol.sezonnazdrowie.view.MainActivity
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_food_item_page.*

class FoodItemPageFragment : Fragment(), LayoutContainer {

    override lateinit var containerView: View

    private lateinit var foodItem: FoodItem // TODO: Shouldn't be here. It should be in model only

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: ")
        containerView = inflater.inflate(R.layout.fragment_food_item_page, container, false)

        return containerView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemName = arguments?.getString(MainActivity.INTENT_ITEM)
            ?: throw IllegalArgumentException("Missing arguments")

        foodItem = mainViewModel.database.getItem(itemName)
        mainViewModel.setActionBarTitle(foodItem.name)

        pagePreviewImageView.setImageResource(
            if (foodItem.image?.isEmpty() == true) { // TODO Should be taken from model
                android.R.drawable.ic_menu_gallery
            } else {
                resources.getIdentifier(
                    foodItem.image,
                    "drawable",
                    activity!!.packageName
                )
            }
        )

        addToShoppingListButton.setOnClickListener {
            mainViewModel.shoppingList.addItem(foodItem.name) // TODO: shoppingList should be private
            Toast.makeText(activity, R.string.added_to_shopping_list, Toast.LENGTH_SHORT).show()
        }

        if (foodItem.startDay1 == null) {
            TextView(activity).apply {
                setText(R.string.season_all_year)
                additionalTextsLayout.addView(this)
            }
        } else {
            var from = FoodItem.DATE_FORMAT_TEXT.format(foodItem.startDay1!!.date)
            var to = FoodItem.DATE_FORMAT_TEXT.format(foodItem.endDay1!!.date)
            TextView(activity).apply {
                text = getString(R.string.season_from_to, from, to)
                additionalTextsLayout.addView(this)
            }

            if (foodItem.startDay2 != null) {
                from = FoodItem.DATE_FORMAT_TEXT.format(foodItem.startDay2!!.date)
                to = FoodItem.DATE_FORMAT_TEXT.format(foodItem.endDay2!!.date)
                TextView(activity).apply {
                    text = getString(R.string.food_detail_item, from, to)
                    additionalTextsLayout.addView(this)
                }
            }
        }
        addSpacer()
        TextView(activity).apply {
            text = foodItem.desc
            additionalTextsLayout.addView(this)
        }
        addSpacer()
        if (foodItem.hasProximates()) {
            // add wartość odżywcza
            TextView(activity).apply {
                setText(R.string.proximates)
                additionalTextsLayout.addView(this)
            }

            addElementView(getString(R.string.proximate_water), foodItem.water)
            addElementView(getString(R.string.proximate_energy), foodItem.energy)
            addElementView(getString(R.string.proximate_protein), foodItem.protein)
            addElementView(getString(R.string.proximate_fat), foodItem.fat)
            addElementView(getString(R.string.proximate_carbohydrates), foodItem.carbohydrates)
            addElementView(getString(R.string.proximate_fiber), foodItem.fiber)
            addElementView(getString(R.string.proximate_sugars), foodItem.sugars)
            addSpacer()
        }

        if (foodItem.hasMinerals()) {
            // add zawartość minerałów
            TextView(activity).apply {
                setText(R.string.minerals)
                additionalTextsLayout.addView(this)
            }

            addElementView(getString(R.string.minerals_calcium), foodItem.calcium)
            addElementView(getString(R.string.minerals_iron), foodItem.iron)
            addElementView(getString(R.string.minerals_magnesium), foodItem.magnesium)
            addElementView(getString(R.string.minerals_phosphorus), foodItem.phosphorus)
            addElementView(getString(R.string.minerals_potassium), foodItem.potassium)
            addElementView(getString(R.string.minerals_sodium), foodItem.sodium)
            addElementView(getString(R.string.minerals_zinc), foodItem.zinc)
            addSpacer()
        }

        if (foodItem.hasVitamins()) {
            // add zawartość witamin
            TextView(activity).apply {
                setText(R.string.vitamins)
                additionalTextsLayout.addView(this)
            }

            addElementView(getString(R.string.vitamins_A), foodItem.vitA)
            addElementView(getString(R.string.vitamins_C), foodItem.vitC)
            addElementView(getString(R.string.vitamins_E), foodItem.vitE)
            addElementView(getString(R.string.vitamins_B1), foodItem.thiamin)
            addElementView(getString(R.string.vitamins_B2), foodItem.riboflavin)
            addElementView(getString(R.string.vitamins_B3), foodItem.niacin)
            addElementView(getString(R.string.vitamins_B6), foodItem.vitB6)
            addElementView(getString(R.string.vitamins_K), foodItem.vitK)
            addElementView(getString(R.string.vitamins_folate), foodItem.folate)
            addSpacer()
        }

        if (foodItem.hasProximates() || foodItem.hasMinerals() || foodItem.hasVitamins()) {
            TextView(activity).apply {
                setText(R.string.food_data_source)
                additionalTextsLayout.addView(this)
            }
            addSpacer()
        }
    }

    private fun addElementView(title: String, value: String?) {
        if (!value.isNullOrEmpty()) {
            TextView(activity).apply {
                text = getString(R.string.food_detail_item, title, value)
                additionalTextsLayout.addView(this)
            }
        }
    }

    private fun addSpacer() {
        TextView(activity).also { additionalTextsLayout.addView(it) }
    }

    companion object {

        private const val TAG = "FOODITEMPAGEFRAGMENT"
    }
}