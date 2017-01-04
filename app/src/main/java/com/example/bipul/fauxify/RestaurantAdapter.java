package com.example.bipul.fauxify;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by bipul on 27-06-2016.
 */
public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.MyViewHolder> {

    public static ArrayList<Restaurant> restaurantList;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private static final String TAG = "error";
        public TextView resName, restype, resRating, resDeliveryTime, resMinimumOrder;
        public ImageView resImage;
        Context context;

        public MyViewHolder(View view) {
            super(view);

            context = view.getContext();
            view.setOnClickListener(this);


            resName = (TextView) view.findViewById(R.id.res_name);
            restype = (TextView) view.findViewById(R.id.res_type);
            //resRating = (TextView) view.findViewById(R.id.res_rating);
            resDeliveryTime = (TextView) view.findViewById(R.id.res_delivery_time);
            resMinimumOrder = (TextView) view.findViewById(R.id.res_min_order);
            resImage = (ImageView) view.findViewById(R.id.res_image);
        }


        @Override
        public void onClick(View v) {

            Intent intent;
            int position = getAdapterPosition();
            Restaurant restaurant = restaurantList.get(position);

            intent = new Intent(context, RestaurantDetails.class);
            intent.putExtra("restaurantName", restaurant.getResName());
            intent.putExtra("restaurantDeLTime", restaurant.getResDeliveryTime());
            intent.putExtra("restaurantMinOrder", restaurant.getResMinOrder());
            intent.putExtra("restaurantRating", restaurant.getResRating());
            intent.putExtra("restaurantType", restaurant.getResType());
            intent.putExtra("resId", restaurant.getResId());
            intent.putExtra("Deliveryfee", restaurant.getDeliveryFee());
            intent.putExtra("freeDeliveryAmount", restaurant.getFreeDeliveryAmount());

            context.startActivity(intent);

        }
    }


    public RestaurantAdapter(ArrayList<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurant_fragment_rowlayout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Restaurant restaurant = restaurantList.get(position);
        holder.resName.setText(restaurant.getResName());
        holder.restype.setText(restaurant.getResType());
        //holder.resRating.setText(restaurant.getResRating());
        holder.resDeliveryTime.setText(restaurant.getResDeliveryTime());
        holder.resMinimumOrder.setText("Min Order  " + MainActivity.rupeesymbol + restaurant.getResMinOrder());

        // holder.itemView.setEnabled(false);

    }


    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

}
