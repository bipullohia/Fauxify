package com.example.bipul.fauxify;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Bipul Lohia on 9/6/2016.
 */
public class DishesAdapter extends RecyclerView.Adapter<DishesAdapter.MyViewHolder> {

    private ArrayList<Dishes> dishesList;
    public static ArrayList<CurrentOrder> currentOrders = new ArrayList<>();
    public ArrayList<String> currentDish = new ArrayList<>();

    private Button addDish, removeDish, addToCart;
    ImageView imgVeg, imgNonveg;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView dishName, dishPrice, dishCount;
        Context context;

        public MyViewHolder(View view) {
            super(view);
            context = view.getContext();

            dishName = (TextView) view.findViewById(R.id.dish_name);
            dishPrice = (TextView) view.findViewById(R.id.dish_price);
            dishCount = (TextView) view.findViewById(R.id.dish_count);
            imgVeg = (ImageView) view.findViewById(R.id.isVegDish);
            imgNonveg = (ImageView) view.findViewById(R.id.isNonvegDish);

        }
    }

    public DishesAdapter(ArrayList<Dishes> dishesList) {
        this.dishesList = dishesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dishes_restaurantmenufragment_rowlayout, parent, false);

        addDish = (Button) itemView.findViewById(R.id.add_dish);
        removeDish = (Button) itemView.findViewById(R.id.remove_dish);
        addToCart = (Button) itemView.findViewById(R.id.addtocart);
        LinearLayout linearLayoutButtons = (LinearLayout) itemView.findViewById(R.id.linearlayoutbuttons);

        if (!RestaurantDetails.restStatus.equals("open")) {
            linearLayoutButtons.setVisibility(View.INVISIBLE);
            addToCart.setVisibility(View.INVISIBLE);
        }

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


        //holder.setIsRecyclable(false);


        final Integer[] count = {0};
        final Dishes dishes = dishesList.get(position);
        holder.dishName.setText(dishes.getDishName());
        holder.dishPrice.setText(dishes.getDishPrice());

        if (dishes.getIsVeg() == 1) {

            imgNonveg.setVisibility(View.GONE);
            imgVeg.setVisibility(View.VISIBLE);

        } else {

            imgVeg.setVisibility(View.GONE);
            imgNonveg.setVisibility(View.VISIBLE);
        }

        Log.e("clicked", "check " + String.valueOf(dishes.getDishName() + dishes.getDishPrice()));

        addDish.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                count[0]++;
                holder.dishCount.setText(String.valueOf(count[0]));

            }
        });

        removeDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (count[0] > 0) {
                    count[0]--;
                    holder.dishCount.setText(String.valueOf(count[0]));
                }
            }
        });

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (count[0] > 0) {
                    CurrentOrder currentOrder = new CurrentOrder(dishes.getDishName(), dishes.getDishPrice(), count[0]);
                    currentOrders.add(currentOrder);

                    //adding the snackbar to display how many items have been added to cart
                    Snackbar snackbar = Snackbar
                            .make(holder.itemView, count[0] + " " + dishes.getDishName() + " added to Cart", Snackbar.LENGTH_LONG);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(ContextCompat.getColor(holder.context, R.color.colorPrimary));
                    TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(ContextCompat.getColor(holder.context, R.color.colorAccent));
                    snackbar.show();

                    Log.e("the count:", String.valueOf(currentOrder));
                } else {
                    Toast.makeText(v.getContext(), "Select/Reselect the Item quantity to be added", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return dishesList.size();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

}
