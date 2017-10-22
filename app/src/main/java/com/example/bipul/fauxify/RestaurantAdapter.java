package com.example.bipul.fauxify;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.MyViewHolder> {

    private static ArrayList<Restaurant> restaurantList;

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView resNameTextView, restypeTextView, resStatusTextView, resDeliveryTimeTextView, resMinimumOrderTextView;
        ImageView resImageView;
        Context context;

        MyViewHolder(View view) {
            super(view);

            context = view.getContext();
            view.setOnClickListener(this);

            resNameTextView = (TextView) view.findViewById(R.id.res_name);
            restypeTextView = (TextView) view.findViewById(R.id.res_type);
            resStatusTextView = (TextView) view.findViewById(R.id.res_status);
            resDeliveryTimeTextView = (TextView) view.findViewById(R.id.res_delivery_time);
            resMinimumOrderTextView = (TextView) view.findViewById(R.id.res_min_order);
            resImageView = (ImageView) view.findViewById(R.id.res_image);
        }

        @Override
        public void onClick(View v) {

            Intent intent;
            int position = getAdapterPosition();
            Restaurant restaurant = restaurantList.get(position);

            intent = new Intent(context, RestaurantDetailsActivity.class);
            intent.putExtra("restaurantName", restaurant.getResName());
            intent.putExtra("restaurantDeLTime", restaurant.getResDeliveryTime());
            intent.putExtra("restaurantMinOrder", restaurant.getResMinOrder());
            intent.putExtra("restaurantRating", restaurant.getResRating());
            intent.putExtra("restaurantType", restaurant.getResType());
            intent.putExtra("resId", restaurant.getResId());
            intent.putExtra("Deliveryfee", restaurant.getDeliveryFee());
            intent.putExtra("freeDeliveryAmount", restaurant.getFreeDeliveryAmount());
            intent.putExtra("restStatus", restaurant.getRestStatus());

            context.startActivity(intent);
        }
    }

    RestaurantAdapter(ArrayList<Restaurant> restaurantList) {
        RestaurantAdapter.restaurantList = restaurantList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rowlayout_restaurant_fragment, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Restaurant restaurant = restaurantList.get(position);
        holder.resNameTextView.setText(restaurant.getResName());
        holder.restypeTextView.setText(restaurant.getResType());
        holder.resDeliveryTimeTextView.setText(restaurant.getResDeliveryTime());
        holder.resMinimumOrderTextView.setText("Min Order  " + MainActivity.rupeesymbol + restaurant.getResMinOrder());

        if (!restaurant.getRestStatus().equals("open")) {

            holder.resStatusTextView.setVisibility(View.VISIBLE);
            holder.resImageView.setAlpha(0.2f);
            holder.resNameTextView.setTextColor(ContextCompat.getColor(holder.context, R.color.fadetext));
            holder.restypeTextView.setTextColor(ContextCompat.getColor(holder.context, R.color.fadetext));
            holder.resDeliveryTimeTextView.setTextColor(ContextCompat.getColor(holder.context, R.color.fadetext));
            holder.resMinimumOrderTextView.setTextColor(ContextCompat.getColor(holder.context, R.color.fadetext));
        }
        // holder.itemView.setEnabled(false);
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
