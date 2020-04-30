package com.karol.sezonnazdrowie.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.karol.sezonnazdrowie.R
import com.karol.sezonnazdrowie.data.FoodItem
import com.karol.sezonnazdrowie.model.MainViewModel
import com.karol.sezonnazdrowie.view.MainActivity
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.row_incoming_layout.view.*

class ListFragment : Fragment(), LayoutContainer {
    override lateinit var containerView: View

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        containerView = inflater.inflate(R.layout.fragment_list, container, false)
        return containerView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val what = requireArguments().getString(MainActivity.INTENT_WHAT)
            ?: throw IllegalArgumentException("Missing arguments")
        when (what) {
            MainActivity.INTENT_WHAT_FRUITS -> mainViewModel.setActionBarTitle(getString(R.string.season_fruits))
            MainActivity.INTENT_WHAT_VEGETABLES -> mainViewModel.setActionBarTitle(getString(R.string.season_vegetables))
            MainActivity.INTENT_WHAT_INCOMING -> mainViewModel.setActionBarTitle(getString(R.string.season_incoming))
        }

        val items: List<FoodItem> = when (what) {
            MainActivity.INTENT_WHAT_FRUITS -> mainViewModel.database.currentFruits
            MainActivity.INTENT_WHAT_VEGETABLES -> mainViewModel.database.currentVegetables
            MainActivity.INTENT_WHAT_INCOMING -> mainViewModel.database.incomingItems
            else -> throw IllegalArgumentException("Unknown target for ListFragment: $what")
        }

        val adapter = if (what == MainActivity.INTENT_WHAT_INCOMING) {
            IncomingAdapter(requireActivity(), items)
        } else {
            ArrayAdapter(requireActivity(), R.layout.row_layout, R.id.rowText, items)
        }
        listView.adapter = adapter

        listView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view1, position, _ ->
                val bundle = Bundle()
                    .apply {
                        putString(
                            MainActivity.INTENT_ITEM,
                            (parent.getItemAtPosition(position) as FoodItem).name
                        )
                    }
                Navigation.findNavController(view1).navigate(R.id.action_food_detail, bundle)
            }
    }

    private inner class IncomingAdapter internal constructor(
        context: Context,
        items: List<FoodItem>
    ) : ArrayAdapter<FoodItem>(context, R.layout.row_incoming_layout, items) {

        private inner class ViewHolder {
            internal lateinit var name: TextView
            internal lateinit var season1: TextView
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view: View
            val holder = if (convertView == null) {
                val inflater =
                    parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val inflatedView: View =
                    inflater.inflate(R.layout.row_incoming_layout, parent, false)
                view = inflatedView
                ViewHolder().apply {
                    name = inflatedView.rowText
                    season1 = inflatedView.rowSeason1Text
                    inflatedView.tag = this
                }
            } else {
                view = convertView
                convertView.tag as ViewHolder
            }

            val item = getItem(position) ?: throw IllegalStateException("None of the items should be null")
            holder.name.text = item.name
            holder.season1.text = item.nearestSeasonString

            return view
        }
    }

    companion object {

        private const val TAG = "LISTFRAGMENT"
    }
}
