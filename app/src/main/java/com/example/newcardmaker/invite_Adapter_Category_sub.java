package com.example.newcardmaker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.newcardmaker.invite_online_database.invite_ItemSubCat;

import java.util.ArrayList;

public class invite_Adapter_Category_sub extends RecyclerView.Adapter<invite_Adapter_Category_sub.MyViewHolder> {

    public Activity main_context;
    View main_itemView;
    private ArrayList<invite_ItemSubCat> arrayList_array;
    private int main_selectedPosition = 0;

    public invite_Adapter_Category_sub(Activity main_context, ArrayList<invite_ItemSubCat> arrayList) {
        this.arrayList_array = arrayList;
        this.main_context = main_context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        main_itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.invite_item_cat_portrait_name, parent, false);
        return new MyViewHolder(main_itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        try {
            holder.texxttt.setText(arrayList_array.get(position).getName());
            if (position == main_selectedPosition) {
                holder.texxttt.setBackgroundResource(R.drawable.invite_tab_selected_bg);
                holder.texxttt.setTextColor(Color.parseColor("#FFFFFF"));
            } else {
                holder.texxttt.setBackgroundResource(R.drawable.invite_background_2_border_black);
                holder.texxttt.setTextColor(Color.parseColor("#000000"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSelectedPosition(int position) {
        int previousPosition = main_selectedPosition;
        main_selectedPosition = position;
        notifyItemChanged(previousPosition);
        notifyItemChanged(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public int getItemCount() {
        return arrayList_array.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView texxttt;

        MyViewHolder(View view) {
            super(view);
            texxttt = view.findViewById(R.id.texxttt);
        }
    }
}
