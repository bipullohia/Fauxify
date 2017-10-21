package com.example.bipul.fauxify;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Bipul Lohia on 10/10/2016.
 */

class MyOrderDishesAdapter extends RecyclerView.Adapter<MyOrderDishesAdapter.MyViewHolder> {

    private ArrayList<CurrentOrder> mDishesListInCart;

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView dishName, dishPrice, dishQuantity, dishAmount;

        Context context;

        MyViewHolder(View view) {
            super(view);

            context = view.getContext();

            dishName = (TextView) view.findViewById(R.id.dishname_incart);
            dishPrice = (TextView) view.findViewById(R.id.dishprice_incart);
            dishQuantity = (TextView) view.findViewById(R.id.dishquantity_incart);
            dishAmount = (TextView) view.findViewById(R.id.dishamount_incart);
        }
    }

    MyOrderDishesAdapter(ArrayList<CurrentOrder> dishesListInCart) {
        this.mDishesListInCart = dishesListInCart;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item_summary_rowlayout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CurrentOrder currentOrder = mDishesListInCart.get(position);

        holder.dishName.setText(currentOrder.getCurrentdishName());
        holder.dishPrice.setText(currentOrder.getCurrentdishPrice());
        holder.dishQuantity.setText(String.valueOf(currentOrder.getCurrentdishQuantity()));
        holder.dishAmount.setText(String.valueOf(Integer.parseInt(currentOrder.getCurrentdishPrice())*
                currentOrder.getCurrentdishQuantity()));
    }

    @Override
    public int getItemCount() {
        return mDishesListInCart.size();
    }
}

