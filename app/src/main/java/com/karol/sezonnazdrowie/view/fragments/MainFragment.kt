package com.karol.sezonnazdrowie.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.karol.sezonnazdrowie.R
import com.karol.sezonnazdrowie.data.FoodItem
import com.karol.sezonnazdrowie.view.MainActivity
import kotlinx.android.synthetic.main.fragment_main.*
import java.util.Date

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val today = Date()
        dateTextView.text = FoodItem.DATE_FORMAT_TEXT.format(today)

        fruitsBtn.setOnClickListener { v ->
            val bundle = Bundle().apply {
                putString(MainActivity.INTENT_WHAT, MainActivity.INTENT_WHAT_FRUITS)
            }
            Navigation.findNavController(v).graph.startDestination = R.id.listFragment
            Navigation.findNavController(v).navigate(R.id.action_list, bundle)
        }

        vegetablesBtn.setOnClickListener { v ->
            val bundle = Bundle().apply {
                putString(MainActivity.INTENT_WHAT, MainActivity.INTENT_WHAT_VEGETABLES)
            }
            Navigation.findNavController(v).graph.startDestination = R.id.listFragment
            Navigation.findNavController(v).navigate(R.id.action_list, bundle)
        }

        incomingSeasonBtn.setOnClickListener { v ->
            val bundle = Bundle().apply {
                putString(MainActivity.INTENT_WHAT, MainActivity.INTENT_WHAT_INCOMING)
            }
            Navigation.findNavController(v).graph.startDestination = R.id.listFragment
            Navigation.findNavController(v).navigate(R.id.action_list, bundle)
        }

        calendarBtn.setOnClickListener { v ->
            Navigation.findNavController(v).graph.startDestination = R.id.calendarFragment
            Navigation.findNavController(v).navigate(R.id.action_calendar)
        }

        shopListBtn.setOnClickListener { v ->
            Navigation.findNavController(v).graph.startDestination = R.id.shoppingListFragment
            Navigation.findNavController(v).navigate(R.id.action_shopping_list)
        }
    }
}
