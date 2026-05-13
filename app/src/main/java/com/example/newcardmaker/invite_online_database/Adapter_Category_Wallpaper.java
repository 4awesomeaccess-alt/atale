package com.example.newcardmaker.invite_online_database;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.newcardmaker.R;

import java.util.ArrayList;

public class Adapter_Category_Wallpaper extends RecyclerView.Adapter<Adapter_Category_Wallpaper.MyViewHolder> {

    public Activity context;
    View itemView;
    private ArrayList<invite_ItemSubCat> arrayList;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public Adapter_Category_Wallpaper(Activity context, ArrayList<invite_ItemSubCat> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cat_portrait, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        try {
           /* Glide.with(context)
                    .load(arrayList.get(position).getImageBig())
                    .placeholder(R.drawable.portrait_loader)
                    .into(holder.imageView);*/

            holder.textView_name.setText(arrayList.get(position).getName());
            holder.itemView.setSelected(position == selectedPosition);
            boolean isSelected = position == selectedPosition;
            holder.itemView.setSelected(isSelected);

            // ✅ TEXT COLOR CHANGE
            if (isSelected) {
                holder.textView_name.setTextColor(
                        context.getResources().getColor(R.color.white)
                );
            } else {
                holder.textView_name.setTextColor(
                        context.getResources().getColor(R.color.black)
                );
            }


        } catch (Exception e) {
            Log.e("#exxx", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView_name;
//        ImageView imageView;

        MyViewHolder(View view) {
            super(view);
            textView_name = view.findViewById(R.id.textView);
//            tx_ad = view.findViewById(R.id.tx_ad);
//            imageView = view.findViewById(R.id.imageView_cat);

        }
    }

    public void setSelectedPosition(int position) {
        int oldPos = selectedPosition;
        selectedPosition = position;

        if (oldPos != RecyclerView.NO_POSITION) notifyItemChanged(oldPos);
        notifyItemChanged(selectedPosition);
    }


}
