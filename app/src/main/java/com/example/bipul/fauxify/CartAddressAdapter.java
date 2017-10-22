package com.example.bipul.fauxify;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


class CartAddressAdapter extends RecyclerView.Adapter<CartAddressAdapter.MyViewHolder> {

    private ArrayList<Address> mAddressListInCart;

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView userAddress;
        Context context;

        MyViewHolder(View view) {
            super(view);

            context = view.getContext();

            view.setOnClickListener(this);
            userAddress = (TextView) view.findViewById(R.id.useraddress_incart);
        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();
            Address addresscart = mAddressListInCart.get(position);

            Log.d("error", "onClick " + getAdapterPosition());

            CartActivity.checkAddressCondition();
            CartActivity.setCurrentAddress(addresscart.getUserAddress());
        }
    }

    CartAddressAdapter(ArrayList<Address> addressListInCart) {
        this.mAddressListInCart = addressListInCart;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rowlayout_address_cart, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Address address = mAddressListInCart.get(position);
        holder.userAddress.setText(address.getUserAddress());
    }

    @Override
    public int getItemCount() {
        return mAddressListInCart.size();
    }
}

