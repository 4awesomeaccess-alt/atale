package com.example.newcardmaker;

import static android.util.Log.ASSERT;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.newcardmaker.invite_online_database.invite_Methods;

import java.util.ArrayList;

public class Adapter_Image_Wallpaper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public Activity context;
    View itemView;
    private invite_Methods methods;
    private ArrayList<Item_OneImages> arrayList;
    private ArrayList<Item_OneImages> arrayListTemp;

    public interface OnImageClickListener {
        void onImageClick(String imageUrl);
    }

    private OnImageClickListener listener;

    public Adapter_Image_Wallpaper(Activity context,
                                   Boolean isUserData,
                                   ArrayList<Item_OneImages> arrayList,
                                   ArrayList<Item_OneImages> arrayListTemp,
                                   OnImageClickListener listener) {
        this.arrayList = arrayList;
        this.arrayListTemp = arrayListTemp;
        this.context = context;
        this.listener = listener;
        methods = new invite_Methods(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photoeditor, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {

            Log.println(ASSERT, "arrayList11", arrayList.get(position).getImageBig());

            try {
                Glide.with(context)
                        .load(arrayList.get(position).getImageBig())
                        .placeholder(R.drawable.portrait_loader)
                        .into(((MyViewHolder) holder).imageView);

            } catch (Exception e) {
                Log.println(ASSERT, "error", e.getMessage());
                e.printStackTrace();
            }

            holder.itemView.setOnClickListener(v -> {
                String imageUrl = arrayList.get(holder.getAdapterPosition()).getImageBig();

                if (listener != null) {
                    listener.onImageClick(imageUrl);
                }
            });
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
        ImageView imageView;

        MyViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.ic_image);
        }
    }
}