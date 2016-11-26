package com.example.bipul.fauxify;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Bipul Lohia on 9/12/2016.
 */
public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.MyViewHolder> {

    public static ArrayList<CurrentOrder> itemSummaryList;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        public TextView dishNameInCart, dishPriceInCart, dishQuantityInCart, dishAmountInCart;
        Context context;

        public MyViewHolder(View view) {
            super(view);

            context = view.getContext();
            view.setOnLongClickListener(this);


            dishNameInCart = (TextView) view.findViewById(R.id.dishname_incart);
            dishPriceInCart = (TextView) view.findViewById(R.id.dishprice_incart);
            dishQuantityInCart = (TextView) view.findViewById(R.id.dishquantity_incart);
            dishAmountInCart = (TextView) view.findViewById(R.id.dishamount_incart);
        }


        @Override
        public boolean onLongClick(View v) {

            AlertDialog.Builder alertbuilder = new AlertDialog.Builder(context);
            alertbuilder.setMessage("Do you want to remove this dish?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    delete(getAdapterPosition());

                                    CartActivity.prepareDetails();
                                }
                            }

                    )


                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = alertbuilder.create();
            alert.setTitle("Alert!");
            alert.show();


            return true;
        }
    }

    private void delete(int position) {
        itemSummaryList.remove(position);
        DishesAdapter.currentOrders.remove(position);
        notifyItemRemoved(position);
    }


    public CartItemAdapter(ArrayList<CurrentOrder> itemSummaryList) {
        CartItemAdapter.itemSummaryList = itemSummaryList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item_summary_rowlayout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CurrentOrder currentOrder = itemSummaryList.get(position);
        holder.dishNameInCart.setText(currentOrder.getCurrentdishName());
        holder.dishPriceInCart.setText(currentOrder.getCurrentdishPrice());
        holder.dishQuantityInCart.setText(String.valueOf(currentOrder.getCurrentdishQuantity()));
        holder.dishAmountInCart.setText(
                String.valueOf(Integer.parseInt(currentOrder.getCurrentdishPrice()) * currentOrder.getCurrentdishQuantity()));
    }

    @Override
    public int getItemCount() {
        return itemSummaryList.size();
    }

}

