package uk.ac.aston.baulchjn.mobiledev.spoon;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import uk.ac.aston.baulchjn.mobiledev.spoon.home.RestaurantItem;


/**
 *
 */
public class BookRestaurantFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_RESTAURANT = "restaurant";

    // TODO: Rename and change types of parameters
    private String name;
    private String vicinity;
    private ArrayList<String> tags;
    private RestaurantItem restaurant;

    public BookRestaurantFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();

        Bundle bundle = FragmentStateContainer.getInstance().activeBundle;
        if (bundle != null) {
            restaurant = (RestaurantItem) bundle.getSerializable(ARG_RESTAURANT);
            Log.i("spoonlogcat", "Wooooo! We're gonna book a restaurant...." + restaurant.toString());
        } else {
            Log.i("spoonlogcat", "Ok sick yeah you wanna book a restaurant but didn't give me one...");
        }


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            restaurant = (RestaurantItem) getArguments().getSerializable(ARG_RESTAURANT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book_restaurant, container, false);
//        TextView nameView = view.findViewById(R.id.restaurant_name);
//        TextView vicinityView = view.findViewById(R.id.restaurant_vicinity);
//        TextView tagsView = view.findViewById(R.id.restaurant_tags);
//
//        nameView.setText(name);
//        vicinityView.setText(vicinity);
//        tagsView.setText(tags != null ? tags.toString() : "");
//
//        Button bookBtn = view.findViewById(R.id.book_btn);
//
//        bookBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                FragmentStateContainer.getInstance().bookRestaurant(restaurant);
//            }
//        });


        return view;
    }


}