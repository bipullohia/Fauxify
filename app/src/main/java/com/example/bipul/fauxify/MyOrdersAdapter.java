package com.example.bipul.fauxify;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Bipul Lohia on 11/5/2016.
 */

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.MyViewHolder> {

    public static ArrayList<MyOrders> orderList;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private static final String TAG = "error";
        public TextView orderTime, totalPrice, orderDelivered, orderConfirmed, restName, dishDetails;
        Context context;
        ImageView redConfirm, greenConfirm, redDeliver, greenDeliver;

        public MyViewHolder(View view) {
            super(view);

            context = view.getContext();
            view.setOnClickListener(this);

            // orderId = (TextView) view.findViewById(R.id.orderrow_orderid);
            //totalItems = (TextView) view.findViewById(R.id.orderrow_totalitems);
            orderTime = (TextView) view.findViewById(R.id.orderTime);
            dishDetails = (TextView) view.findViewById(R.id.dishDetails);
            totalPrice = (TextView) view.findViewById(R.id.orderrow_totalprice);
            orderConfirmed = (TextView) view.findViewById(R.id.orderrow_orderconfirmed);
            orderDelivered = (TextView) view.findViewById(R.id.orderrow_orderdelivered);
            restName = (TextView) view.findViewById(R.id.orderrow_restname);
            redConfirm = (ImageView) view.findViewById(R.id.redConfirmImg);
            greenConfirm = (ImageView) view.findViewById(R.id.greenConfirmImg);
            redDeliver = (ImageView) view.findViewById(R.id.redDeliverImg);
            greenDeliver = (ImageView) view.findViewById(R.id.greenDeliverImg);
        }

        @Override
        public void onClick(View v) {

            int position= getAdapterPosition();
            Intent intent;
            intent = new Intent(context, MyOrderDetails.class);

            MyOrders orders = orderList.get(position);

            intent.putExtra("orderid",orders.getOrderId());
            intent.putExtra("RestName", orders.getRestName());
            intent.putExtra("totalitems",orders.getTotalitems());
            intent.putExtra("totalprice", orders.getTotalprice());
            intent.putExtra("customeraddress", orders.getCustomeraddress());
            intent.putExtra("ordertime", orders.getOrdertime());
            intent.putExtra("orderconfirmed", orders.getOrderconfirmed());
            intent.putExtra("orderdelivered", orders.getOrderdelivered());
            intent.putExtra("customerorder", orders.getCustomerorder());
            intent.putExtra("totalitemprice", orders.getTotalitemsprice());
            intent.putExtra("deliveryfee", orders.getDeliveryFee());

            context.startActivity(intent);

        }
    }

    public MyOrdersAdapter(ArrayList<MyOrders> orderList)
    {
        MyOrdersAdapter.orderList = orderList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)  {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.myordersfragment_rowlayout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MyOrders orders = orderList.get(position);
        holder.totalPrice.setText(orders.getTotalprice());
        holder.orderConfirmed.setText(orders.getOrderconfirmed());
        holder.orderDelivered.setText(orders.getOrderdelivered());
        holder.restName.setText(orders.getRestName());

        //below code is to format the dishlist and quantity into the desired format to show in myOrders section
        String dishInfo = orders.getCustomerorder();

        JSONArray jsonArray = null;
        String dishDetails = "";
        try {
            jsonArray = new JSONArray(dishInfo);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                if(i==jsonArray.length()-1){
                    dishDetails = dishDetails + String.valueOf(jsonObject.getInt("dishquantity"))+ " " + jsonObject.getString("dishname");
                }else{
                    dishDetails = dishDetails + String.valueOf(jsonObject.getInt("dishquantity"))+ " " + jsonObject.getString("dishname") + ", ";
                }
            }

            Log.d("dishinfo", dishDetails);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.dishDetails.setText(dishDetails);

        //following code formats the timestamp into proper desired format of date under restaurant name
        String timestamp = orders.getOrdertime();
        String orderDate = timestamp.substring(0, 2);
        Log.d("orderdate", orderDate);
        
        String orderMonth = "";
        String month = timestamp.substring(3,5);
        switch (month){
            
            case "01": orderMonth = "Jan"; break;
            case "02": orderMonth = "Feb"; break;
            case "03": orderMonth = "Mar"; break;
            case "04": orderMonth = "Apr"; break;
            case "05": orderMonth = "May"; break;
            case "06": orderMonth = "Jun"; break;
            case "07": orderMonth = "Jul"; break;
            case "08": orderMonth = "Aug"; break;
            case "09": orderMonth = "Sep"; break;
            case "10": orderMonth = "Oct"; break;
            case "11": orderMonth = "Nov"; break;
            case "12": orderMonth = "Dec"; break;
        }

        String orderYear = timestamp.substring(6, 10);
        String orderTiming = timestamp.substring(11, 16);
        if(timestamp.length()>19){
            orderTiming = orderTiming + timestamp.substring(20);
        }

        holder.orderTime.setText("Ordered on " + orderDate + " " + orderMonth + " " + orderYear + " at " + orderTiming);
        //02-10-2017 09:59:15 AM - timestamp format

        if (orders.getOrderconfirmed().equals("Confirmed")) {
            //holder.orderConfirmed.setTextColor(ContextCompat.getColor(holder.context, R.color.green));
            holder.redConfirm.setVisibility(View.GONE);
            holder.greenConfirm.setVisibility(View.VISIBLE);
        } else {
            //holder.orderConfirmed.setTextColor(ContextCompat.getColor(holder.context, R.color.red));
            holder.greenConfirm.setVisibility(View.GONE);
            holder.redConfirm.setVisibility(View.VISIBLE);
        }


        if (orders.getOrderdelivered().equals("Delivered")) {
            //holder.orderDelivered.setTextColor(ContextCompat.getColor(holder.context, R.color.green));
            holder.redDeliver.setVisibility(View.GONE);
            holder.greenDeliver.setVisibility(View.VISIBLE);

        } else{
            //holder.orderDelivered.setTextColor(ContextCompat.getColor(holder.context, R.color.red));
            holder.greenDeliver.setVisibility(View.GONE);
            holder.redDeliver.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

}


