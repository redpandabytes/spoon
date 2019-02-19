package uk.ac.aston.baulchjn.mobiledev.spoon;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

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

// TODO BROKEN
    private final String DATABASE_NAME = "RESTAURANT_DB";
//    private final String DATABASE_NAME = getContext().getResources().getString(R.string.restaurant_db_name);
    private RestaurantDatabase restaurantDatabase;


    private View view;

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

            TextView youAreBooking = view.findViewById(R.id.youAreBooking);
            youAreBooking.setText(getString(R.string.en_bookrestaurant_youarebooking, restaurant.getName()));


        } else {
            // TODO: Throw exception, but breaks it at the moment
//            throw new IllegalStateException("Ok sick yeah you wanna book a restaurant but didn't give me one...");
        }


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            restaurant = (RestaurantItem) getArguments().getSerializable(ARG_RESTAURANT);

        }

        restaurantDatabase = Room.databaseBuilder(getActivity().getApplicationContext(),
                RestaurantDatabase.class, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_book_restaurant, container, false);

        final Calendar myCalendar = Calendar.getInstance();

        final EditText edittext= view.findViewById(R.id.bookRestaurantDateEditText);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

            private void updateLabel(){
                String myFormat = "dd/MM/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);

                edittext.setText(sdf.format(myCalendar.getTime()));
            }

        };

        edittext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final EditText timeEditor = view.findViewById(R.id.bookRestaurantTimeEditText);
        final TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        timeEditor.setText(hourOfDay + ":" + minute);
                    }
                }, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true);

        timeEditor.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                timePickerDialog.show();
            }
        });




//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                RestaurantItem restaurantItem = new RestaurantItem();
//                restaurantItem.setDesc("Description");
//                restaurantItem.setName("Restaurant Nameeeeee");
//                restaurantDatabase.daoAccess().insertSingleRestaurantItem(restaurantItem);
//            }
//        }) .start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                RestaurantItem restaurantItem = restaurantDatabase.daoAccess().fetchOneRestaurantbyName("Restaurant Name");
                System.out.println("the restaurant you asked for is..." + restaurantItem);
            }
        }) .start();

//        TextView vicinityView = view.findViewById(R.id.restaurant_vicinity);
//        TextView tagsView = view.findViewById(R.id.restaurant_tags);
//
//        nameView.setText(name);
//        vicinityView.setText(vicinity);
//        tagsView.setText(tags != null ? tags.toString() : "");
//
        Button bookBtn = view.findViewById(R.id.createBookingBtn);

        bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Ready to make the restaurant booking now");
            }
        });


        return view;
    }


}
