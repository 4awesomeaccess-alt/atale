package com.example.newcardmaker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
/*
import com.CrytonixCodings.ColorLightSplashEffectPhotoEditor.R;
import com.CrytonixCodings.ColorLightSplashEffectPhotoEditor.invite_online_database.invite_ItemSubCat;*/

import com.example.newcardmaker.invite_online_database.invite_ItemSubCat;

import java.util.ArrayList;

public class invite_Adapter_Category_sub_dialog extends RecyclerView.Adapter<invite_Adapter_Category_sub_dialog.MyViewHolder> {

    public Activity context;
    View itemView;
    private ArrayList<invite_ItemSubCat> arrayList_array;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public invite_Adapter_Category_sub_dialog(Activity context, ArrayList<invite_ItemSubCat> arrayList) {
        this.arrayList_array = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.invite_item_cat_portrait_name, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        try {
            holder.texxttt.setText(arrayList_array.get(position).getName());
            if (position == selectedPosition) {
                holder.texxttt.setBackgroundResource(R.drawable.invite_background_2_border);
                holder.texxttt.setTextColor(Color.parseColor("#166349"));
            } else {
                holder.texxttt.setBackgroundResource(R.drawable.invite_background_2_border_black);
                holder.texxttt.setTextColor(Color.parseColor("#000000"));
            }
        } catch (Exception e) {
            Log.e("#exxx", e.getMessage());
            e.printStackTrace();
        }
    }
    public void setSelectedPosition(int position) {
        int previousPosition = selectedPosition;
        selectedPosition = position;
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
