package com.emon.simpleblogapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MyRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
    TextView titleTV, discripTV;
    ImageView imageView;
    OnLongClickListener onLongClickListener;

    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public MyRecyclerViewHolder(View itemView) {
        super(itemView);
        titleTV = (TextView) itemView.findViewById(R.id.titleTV);
        discripTV = (TextView) itemView.findViewById(R.id.discripTV);
        imageView = itemView.findViewById(R.id.picsow);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public boolean onLongClick(View v) {
        onLongClickListener.onLongClick(v, getAdapterPosition());
        return false;
    }

//    @Override
//    public void onClick(View v) {
//        onLongClickListener.onClick(v,getAdapterPosition());
//    }
//

}

