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


class DishesAdapter extends RecyclerView.Adapter<DishesAdapter.MyViewHolder> {

    private ArrayList<Dishes> mDishesList;
    static ArrayList<CurrentOrder> currentOrders = new ArrayList<>();

    private Button mAddDishButton, mRemoveDishButton, mAddToCartButton;
    private ImageView mVegImageView, mNonVegImageView;

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView dishName, dishPrice, dishCount;
        Context context;

        MyViewHolder(View view) {
            super(view);

            context = view.getContext();

            dishName = (TextView) view.findViewById(R.id.dish_name);
            dishPrice = (TextView) view.findViewById(R.id.dish_price);
            dishCount = (TextView) view.findViewById(R.id.dish_count);
            mVegImageView = (ImageView) view.findViewById(R.id.is_vegdish);
            mNonVegImageView = (ImageView) view.findViewById(R.id.is_nonvegdish);
        }
    }

    DishesAdapter(ArrayList<Dishes> dishesList) {
        this.mDishesList = dishesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rowlayout_dishes_restaurantmenufragment, parent, false);

        mAddDishButton = (Button) itemView.findViewById(R.id.add_dish);
        mRemoveDishButton = (Button) itemView.findViewById(R.id.remove_dish);
        mAddToCartButton = (Button) itemView.findViewById(R.id.button_add_to_cart);
        LinearLayout linearLayoutButtons = (LinearLayout) itemView.findViewById(R.id.linearlayout_buttons);

        if (!RestaurantDetailsActivity.restStatus.equals("open")) {
            linearLayoutButtons.setVisibility(View.INVISIBLE);
            mAddToCartButton.setVisibility(View.INVISIBLE);
        }

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        //holder.setIsRecyclable(false);

        final Integer[] count = {0};
        final Dishes dishes = mDishesList.get(position);
        holder.dishName.setText(dishes.getDishName());
        holder.dishPrice.setText(dishes.getDishPrice());

        if (dishes.getIsVeg() == 1) {
            mNonVegImageView.setVisibility(View.GONE);
            mVegImageView.setVisibility(View.VISIBLE);

        } else {
            mVegImageView.setVisibility(View.GONE);
            mNonVegImageView.setVisibility(View.VISIBLE);
        }

        mAddDishButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                count[0]++;
                holder.dishCount.setText(String.valueOf(count[0]));
            }
        });

        mRemoveDishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (count[0] > 0) {
                    count[0]--;
                    holder.dishCount.setText(String.valueOf(count[0]));
                }
            }
        });

        mAddToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (count[0] > 0) {
                    CurrentOrder currentOrder = new CurrentOrder(dishes.getDishName(), dishes.getDishPrice(), count[0]);
                    currentOrders.add(currentOrder);

                    //adding the SnackBar to display how many and which items have been added to cart
                    Snackbar snackbar = Snackbar
                            .make(holder.itemView, count[0] + " " + dishes.getDishName() + " added to Cart", Snackbar.LENGTH_LONG);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(ContextCompat.getColor(holder.context, R.color.colorPrimary));
                    TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(ContextCompat.getColor(holder.context, R.color.colorAccent));
                    snackbar.show();

                } else { //this means no item has been selected to add to cart. Let user select/reselect it
                    Toast.makeText(v.getContext(), R.string.select_item_quantity_tobe_added, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDishesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
