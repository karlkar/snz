package com.karol.sezonnazdrowie.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.karol.sezonnazdrowie.R;
import com.karol.sezonnazdrowie.data.FoodItem;
import com.karol.sezonnazdrowie.view.FragmentsActivity;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class MainFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView dateTextView = view.findViewById(R.id.dateTextView);
        Button fruitsBtn = view.findViewById(R.id.fruitsBtn);
        Button vegetablesBtn = view.findViewById(R.id.vegetablesBtn);
        Button incomingSeasonBtn = view.findViewById(R.id.incomingSeasonBtn);
        Button calendarBtn = view.findViewById(R.id.calendarBtn);
        Button shopListBtn = view.findViewById(R.id.shopListBtn);

        Date today = new Date();
        dateTextView.setText(FoodItem.DATE_FORMAT_TEXT.format(today));

        fruitsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(FragmentsActivity.INTENT_WHAT, FragmentsActivity.INTENT_WHAT_FRUITS);
                Navigation.findNavController(v).getGraph().setStartDestination(R.id.listFragment);
                Navigation.findNavController(v).navigate(R.id.action_list, bundle);
            }
        });

        vegetablesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(FragmentsActivity.INTENT_WHAT, FragmentsActivity.INTENT_WHAT_VEGETABLES);
                Navigation.findNavController(v).getGraph().setStartDestination(R.id.listFragment);
                Navigation.findNavController(v).navigate(R.id.action_list, bundle);
            }
        });

        incomingSeasonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(FragmentsActivity.INTENT_WHAT, FragmentsActivity.INTENT_WHAT_INCOMING);
                Navigation.findNavController(v).getGraph().setStartDestination(R.id.listFragment);
                Navigation.findNavController(v).navigate(R.id.action_list, bundle);
            }
        });

        calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).getGraph().setStartDestination(R.id.calendarFragment);
                Navigation.findNavController(v).navigate(R.id.action_calendar);
            }
        });

        shopListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).getGraph().setStartDestination(R.id.shoppingListFragment);
                Navigation.findNavController(v).navigate(R.id.action_shopping_list);
            }
        });
    }
}
