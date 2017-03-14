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

public class MyOrderDishesAdapter extends RecyclerView.Adapter<MyOrderDishesAdapter.MyViewHolder> {

    private ArrayList<CurrentOrder> dishesListInCart;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "error";
        public TextView dishname, dishprice, dishquantity, dishamount;


        Context context;


        public MyViewHolder(View view) {
            super(view);

            context = view.getContext();

            dishname = (TextView) view.findViewById(R.id.dishname_incart);
            dishprice = (TextView) view.findViewById(R.id.dishprice_incart);
            dishquantity = (TextView) view.findViewById(R.id.dishquantity_incart);
            dishamount = (TextView) view.findViewById(R.id.dishamount_incart);

        }


    }


    public MyOrderDishesAdapter(ArrayList<CurrentOrder> dishesListInCart) {
        this.dishesListInCart = dishesListInCart;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item_summary_rowlayout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CurrentOrder currentOrder = dishesListInCart.get(position);

        holder.dishname.setText(currentOrder.getCurrentdishName());
        holder.dishprice.setText(currentOrder.getCurrentdishPrice());
        holder.dishquantity.setText(String.valueOf(currentOrder.getCurrentdishQuantity()));
        holder.dishamount.setText(String.valueOf(Integer.parseInt(currentOrder.getCurrentdishPrice())*
                currentOrder.getCurrentdishQuantity()));

    }

    @Override
    public int getItemCount() {
        return dishesListInCart.size();
    }

}

