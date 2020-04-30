package com.karol.sezonnazdrowie.view.fragments

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.karol.sezonnazdrowie.R
import com.karol.sezonnazdrowie.model.MainViewModel
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_shopping_list.*

class ShoppingListFragment : Fragment(), LayoutContainer {

    override lateinit var containerView: View

    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var inputMethodManager: InputMethodManager
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: ")
        mainViewModel.setActionBarTitle(getString(R.string.shopping_list))

        containerView = inflater.inflate(R.layout.fragment_shopping_list, container, false)
        return containerView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val shoppingList = mainViewModel.shoppingList.items
        adapter = ArrayAdapter(requireActivity(), R.layout.row_layout, R.id.rowText, shoppingList)
        listView.adapter = adapter

        listView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val selectedItem = parent.getItemAtPosition(position) as String
                AlertDialog.Builder(requireActivity())
                    .setTitle(getString(R.string.shopping_list_delete_dialog_title))
                    .setMessage(
                        getString(
                            R.string.shopping_list_delete_dialog_message,
                            selectedItem
                        )
                    )
                    .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
                    .setPositiveButton(getString(R.string.delete)) { dialog, _ ->
                        adapter.remove(selectedItem)
                        mainViewModel.shoppingList.deleteItem(selectedItem)
                        Toast.makeText(
                            activity,
                            getString(R.string.removed_product_name, selectedItem),
                            Toast.LENGTH_LONG
                        ).show()
                        dialog.dismiss()
                    }
                    .show()
            }

        addToShoppingListButton.setOnClickListener {
            val builder = AlertDialog.Builder(requireActivity())
                .setTitle(getString(R.string.shopping_list_add_dialog_title))
                .setMessage(getString(R.string.shopping_list_add_dialog_message))
            val editText = EditText(activity).apply {
                setLines(1)
                setSingleLine()
                imeOptions = EditorInfo.IME_ACTION_DONE
            }
            val alertDialog = builder.setView(editText)
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.add)) { dialog, _ ->
                    acceptInput(editText, dialog)
                }
                .create()
            editText.setOnEditorActionListener { _, _, _ ->
                acceptInput(editText, alertDialog)
                true
            }
            alertDialog.show()
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
    }

    private fun acceptInput(editText: EditText, alertDialog: DialogInterface) {
        if (editText.text.toString().isEmpty()) {
            Toast.makeText(activity, R.string.empty_product_name_message, Toast.LENGTH_LONG).show()
        } else {
            mainViewModel.shoppingList.addItem(editText.text.toString())
            adapter.add(editText.text.toString())
            inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
            Toast.makeText(activity, getString(R.string.added_to_shopping_list), Toast.LENGTH_LONG)
                .show()
            alertDialog.dismiss()
        }
    }

    companion object {

        private const val TAG = "SHOPPINGLISTFRAGMENT"
    }
}
