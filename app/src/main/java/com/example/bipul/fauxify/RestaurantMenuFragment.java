package com.example.bipul.fauxify;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 */
public class RestaurantMenuFragment extends Fragment {

    private ArrayList<Dishes> dishesList = new ArrayList<>();
    private RecyclerView dishesRecyclerView;
    private DishesAdapter dishesAdapter;

    public RestaurantMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restaurant_menu, container, false);

        dishesRecyclerView = (RecyclerView) view.findViewById(R.id.dishes_recycler_view);

        dishesAdapter = new DishesAdapter(dishesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        dishesRecyclerView.setLayoutManager(mLayoutManager);
        dishesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        dishesRecyclerView.setAdapter(dishesAdapter);

        Bundle bundle = getArguments();

        String message = bundle.getString("data");

        try {
            JSONObject jo = new JSONObject(message);
            Log.e("result1", String.valueOf(jo));

            ArrayList<String> dishDetails = new ArrayList<>();
            ArrayList<String> dishId = new ArrayList<>();

            Iterator iter = jo.keys();
            while (iter.hasNext()) {
                String key = (String) iter.next();
                dishId.add(key);
                dishDetails.add(jo.getString(key));
            }

            Log.e("subresult content1", String.valueOf(dishDetails));

            if (dishId.size() != dishesList.size()) {
                for (int j = 0; j <= (dishId.size() - 1); j++) {

                    if(RestaurantDetails.isVeg) {   //this is only for the case when VEGETARIAN option is switched on by user

                        JSONObject jsob = new JSONObject(dishDetails.get(j));

                        if(jsob.getInt("isveg")==1) {

                            Dishes dishes = new Dishes(jsob.getString("dishname"), jsob.getString("dishprice"), dishId.get(j), jsob.getInt("isveg"));
                            dishesList.add(dishes);
                        }

                    }

                    else{   // this option is for general case by default

                        JSONObject jsob = new JSONObject(dishDetails.get(j));
                        Dishes dishes = new Dishes(jsob.getString("dishname"), jsob.getString("dishprice"), dishId.get(j), jsob.getInt("isveg"));
                        dishesList.add(dishes);
                    }
                }
            }

            dishesAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }
}
