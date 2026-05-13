package com.example.newcardmaker;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.newcardmaker.invite_online_database.invite_Item_OneImages_frame;

import java.util.ArrayList;


public class invite_Adapter_Image_Greeting_frame extends RecyclerView.Adapter {
    public Activity main_context;
    View itemView;
    private ArrayList<invite_Item_OneImages_frame> arrayList;

    public invite_Adapter_Image_Greeting_frame(Activity main_context, ArrayList<invite_Item_OneImages_frame> arrayList) {
        this.arrayList = arrayList;
        this.main_context = main_context;
    }
    
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.invite_videlo_list_iem, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
//        if (holder instanceof MyViewHolder) {
//            Glide.with(main_context).load(arrayList.get(position).getImageBig()).placeholder(R.drawable.invite_loader).into(((MyViewHolder) holder).image);
//        }

        invite_Item_OneImages_frame item = arrayList.get(position);

        // ✅ null/empty check પહેલા
        String imageUrl = item.getImageBig();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(main_context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_background) // કોઈ પણ placeholder
                    .error(R.drawable.ic_launcher_background)
                    .into(((MyViewHolder) holder).image); // ✅ safe
        } else {
            // URL null હોય તો placeholder set કરો
            ((MyViewHolder) holder).image.setImageResource(R.drawable.ic_launcher_background);
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
        ImageView image;
        MyViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.iv_frame_image);
        }
    }


}