package com.example.newcardmaker;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/*import com.CrytonixCodings.ColorLightSplashEffectPhotoEditor.R;
import com.CrytonixCodings.ColorLightSplashEffectPhotoEditor.invite_online_database.invite_Item_OneImages_dialog;*/
import com.bumptech.glide.Glide;
import com.example.newcardmaker.invite_online_database.invite_Item_OneImages_dialog;

import java.util.ArrayList;


public class invite_Adapter_Image_Greeting_dialog_special extends RecyclerView.Adapter {
    public Activity context_des;
    View itemView_des;
    private ArrayList<invite_Item_OneImages_dialog> arrayList_des;

    public invite_Adapter_Image_Greeting_dialog_special(Activity context_des, ArrayList<invite_Item_OneImages_dialog> arrayList_des) {
        this.arrayList_des = arrayList_des;
        this.context_des = context_des;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        itemView_des = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.invite_item_sticker_card, parent, false);
        return new MyViewHolder(itemView_des);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            Glide.with(context_des).load(arrayList_des.get(position).getImageBig()).into(((MyViewHolder) holder).image);
        }
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public int getItemCount() {
        return arrayList_des.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        MyViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
        }
    }
}