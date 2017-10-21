package com.example.bipul.fauxify;

import android.content.Context;
import android.content.Intent;
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


class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.MyViewHolder> {

    private static ArrayList<MyOrders> orderList;

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView orderTimeTextView, totalPriceTextView, orderDeliveredTextView, orderConfirmedTextView,
                restNameTextView, dishDetailsTextView;
        Context context;
        ImageView redConfirmImageView, greenConfirmImageView, redDeliverImageView, greenDeliverImageView;

        MyViewHolder(View view) {
            super(view);

            context = view.getContext();
            view.setOnClickListener(this);

            orderTimeTextView = (TextView) view.findViewById(R.id.order_time);
            dishDetailsTextView = (TextView) view.findViewById(R.id.dish_details);
            totalPriceTextView = (TextView) view.findViewById(R.id.orderrow_totalprice);
            orderConfirmedTextView = (TextView) view.findViewById(R.id.orderrow_orderconfirmed);
            orderDeliveredTextView = (TextView) view.findViewById(R.id.orderrow_orderdelivered);
            restNameTextView = (TextView) view.findViewById(R.id.orderrow_restname);
            redConfirmImageView = (ImageView) view.findViewById(R.id.imageview_red_confirm);
            greenConfirmImageView = (ImageView) view.findViewById(R.id.imageview_green_confirm);
            redDeliverImageView = (ImageView) view.findViewById(R.id.imageview_red_deliver);
            greenDeliverImageView = (ImageView) view.findViewById(R.id.imageview_green_deliver);
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

    MyOrdersAdapter(ArrayList<MyOrders> orderList)
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
        holder.totalPriceTextView.setText(orders.getTotalprice());
        holder.orderConfirmedTextView.setText(orders.getOrderconfirmed());
        holder.orderDeliveredTextView.setText(orders.getOrderdelivered());
        holder.restNameTextView.setText(orders.getRestName());

        //below code is to format the dishlist and quantity into the desired format to show in myOrders section
        String dishInfo = orders.getCustomerorder();

        JSONArray jsonArray = null;
        String dishDetails = "";
        try {
            jsonArray = new JSONArray(dishInfo);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                if(i==jsonArray.length()-1){
                    dishDetails = dishDetails + String.valueOf(jsonObject.getInt("dishquantity"))
                                  + " " + jsonObject.getString("dishname");
                }else{
                    dishDetails = dishDetails + String.valueOf(jsonObject.getInt("dishquantity"))
                                  + " " + jsonObject.getString("dishname") + ", ";
                }
            }

            Log.d("dishinfo", dishDetails);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.dishDetailsTextView.setText(dishDetails);

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

        holder.orderTimeTextView.setText("Ordered on " + orderDate + " " + orderMonth + " " + orderYear + " at " + orderTiming);
        //02-10-2017 09:59:15 AM - timestamp format

        if (orders.getOrderconfirmed().equals("Confirmed")) {
            holder.redConfirmImageView.setVisibility(View.GONE);
            holder.greenConfirmImageView.setVisibility(View.VISIBLE);

        } else {
            holder.greenConfirmImageView.setVisibility(View.GONE);
            holder.redConfirmImageView.setVisibility(View.VISIBLE);
        }

        if (orders.getOrderdelivered().equals("Delivered")) {
            holder.redDeliverImageView.setVisibility(View.GONE);
            holder.greenDeliverImageView.setVisibility(View.VISIBLE);

        } else{
            holder.greenDeliverImageView.setVisibility(View.GONE);
            holder.redDeliverImageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}


