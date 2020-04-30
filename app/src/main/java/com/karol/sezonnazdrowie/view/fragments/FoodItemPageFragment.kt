package com.karol.sezonnazdrowie.view.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.karol.sezonnazdrowie.R
import com.karol.sezonnazdrowie.data.FoodItem
import com.karol.sezonnazdrowie.model.FoodItemPageViewModel
import com.karol.sezonnazdrowie.model.MainViewModel
import com.karol.sezonnazdrowie.view.MainActivity
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_food_item_page.*

class FoodItemPageFragment : Fragment(), LayoutContainer {

    override lateinit var containerView: View

    private val mainViewModel: MainViewModel by viewModels()
    private val foodItemPageViewModel: FoodItemPageViewModel by viewModels()

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

        val itemName = requireArguments().getString(MainActivity.INTENT_ITEM)
            ?: throw IllegalArgumentException("Missing arguments")

        val foodItem = mainViewModel.database.getItem(itemName)
        foodItemPageViewModel.setFoodItem(foodItem)
        mainViewModel.setActionBarTitle(foodItem.name)

        pagePreviewImageView.setImageResource(
            foodItemPageViewModel.getThumbnailResource(view.context)
        )

        addToShoppingListButton.setOnClickListener {
            mainViewModel.shoppingList.addItem(foodItem.name)
            Toast.makeText(view.context, R.string.added_to_shopping_list, Toast.LENGTH_SHORT).show()
        }

        if (foodItem.startDay1 == null) {
            TextView(view.context).apply {
                setText(R.string.season_all_year)
                additionalTextsLayout.addView(this)
            }
        } else {
            var from = FoodItem.DATE_FORMAT_TEXT.format(foodItem.startDay1)
            var to = FoodItem.DATE_FORMAT_TEXT.format(foodItem.endDay1!!)
            TextView(view.context).apply {
                text = getString(R.string.season_from_to, from, to)
                additionalTextsLayout.addView(this)
            }

            if (foodItem.startDay2 != null) {
                from = FoodItem.DATE_FORMAT_TEXT.format(foodItem.startDay2)
                to = FoodItem.DATE_FORMAT_TEXT.format(foodItem.endDay2!!)
                TextView(view.context).apply {
                    text = getString(R.string.food_detail_item, from, to)
                    additionalTextsLayout.addView(this)
                }
            }
        }

        foodItem.desc?.let { addDescriptionBlock(view.context, it) }
        addSpacer(view.context)
        if (foodItem.hasProximates()) {
            addProximatesBlock(view.context, foodItem)
        }

        if (foodItem.hasMinerals()) {
            addMineralsBlock(view.context, foodItem)
        }

        if (foodItem.hasVitamins()) {
            addVitaminsBlock(view.context, foodItem)
        }

        if (foodItem.hasProximates() || foodItem.hasMinerals() || foodItem.hasVitamins()) {
            TextView(view.context).apply {
                setText(R.string.food_data_source)
                additionalTextsLayout.addView(this)
            }
            addSpacer(view.context)
        }
    }

    private fun addDescriptionBlock(context: Context, description: String) {
        addSpacer(context)
        TextView(context).apply {
            text = description
            additionalTextsLayout.addView(this)
        }
    }

    private fun addProximatesBlock(context: Context, foodItem: FoodItem) {
        TextView(context).apply {
            setText(R.string.proximates)
            additionalTextsLayout.addView(this)
        }

        listOf(
            R.string.proximate_water to foodItem.water,
            R.string.proximate_energy to foodItem.energy,
            R.string.proximate_protein to foodItem.protein,
            R.string.proximate_fat to foodItem.fat,
            R.string.proximate_carbohydrates to foodItem.carbohydrates,
            R.string.proximate_fiber to foodItem.fiber,
            R.string.proximate_sugars to foodItem.sugars
        ).forEach { addElementView(context, getString(it.first), it.second) }
        addSpacer(context)
    }

    private fun addMineralsBlock(context: Context, foodItem: FoodItem) {
        TextView(context).apply {
            setText(R.string.minerals)
            additionalTextsLayout.addView(this)
        }

        addElementView(context, getString(R.string.minerals_calcium), foodItem.calcium)
        addElementView(context, getString(R.string.minerals_iron), foodItem.iron)
        addElementView(context, getString(R.string.minerals_magnesium), foodItem.magnesium)
        addElementView(context, getString(R.string.minerals_phosphorus), foodItem.phosphorus)
        addElementView(context, getString(R.string.minerals_potassium), foodItem.potassium)
        addElementView(context, getString(R.string.minerals_sodium), foodItem.sodium)
        addElementView(context, getString(R.string.minerals_zinc), foodItem.zinc)
        addSpacer(context)
    }

    private fun addVitaminsBlock(context: Context, foodItem: FoodItem) {
        TextView(context).apply {
            setText(R.string.vitamins)
            additionalTextsLayout.addView(this)
        }

        addElementView(context, getString(R.string.vitamins_A), foodItem.vitA)
        addElementView(context, getString(R.string.vitamins_C), foodItem.vitC)
        addElementView(context, getString(R.string.vitamins_E), foodItem.vitE)
        addElementView(context, getString(R.string.vitamins_B1), foodItem.thiamin)
        addElementView(context, getString(R.string.vitamins_B2), foodItem.riboflavin)
        addElementView(context, getString(R.string.vitamins_B3), foodItem.niacin)
        addElementView(context, getString(R.string.vitamins_B6), foodItem.vitB6)
        addElementView(context, getString(R.string.vitamins_K), foodItem.vitK)
        addElementView(context, getString(R.string.vitamins_folate), foodItem.folate)
        addSpacer(context)
    }

    private fun addElementView(context: Context, title: String, value: String?) {
        if (!value.isNullOrEmpty()) {
            TextView(context).apply {
                text = getString(R.string.food_detail_item, title, value)
                additionalTextsLayout.addView(this)
            }
        }
    }

    private fun addSpacer(context: Context) {
        TextView(context).also { additionalTextsLayout.addView(it) }
    }

    companion object {

        private const val TAG = "FOODITEMPAGEFRAGMENT"
    }
}