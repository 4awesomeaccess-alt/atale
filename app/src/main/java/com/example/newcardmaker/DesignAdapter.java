package com.example.newcardmaker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newcardmaker.Activity.MainActivity;

import java.io.File;
import java.util.List;

public class DesignAdapter extends RecyclerView.Adapter<DesignAdapter.MyViewHolder> {

    private List<DesignModel> designList;

    public DesignAdapter(List<DesignModel> designList) {
        this.designList = designList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_design, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DesignModel model = designList.get(position);
        holder.fileName.setText(model.getFileName());

        // 1. EDIT: Click on item to Edit
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), MainActivity.class);
            intent.putExtra("FILE_PATH", model.getFilePath());
            v.getContext().startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            showDeleteDialog(v.getContext(), model.getFilePath(), position);
        });

        // 2. SHARE: Click on Share Icon
        holder.btnShare.setOnClickListener(v -> {
            shareJsonFile(v.getContext(), model.getFilePath());
        });
    }

    // Share File Function
    private void shareJsonFile(Context context, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            Toast.makeText(context, "File not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        // FileProvider no upyog jethi secure rite file share thai shake
        Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Permission farajiyat che
        context.startActivity(Intent.createChooser(intent, "Share Design via"));
    }

    @Override
    public int getItemCount() {
        return designList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView fileName;
        ImageView btnShare, btnDelete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.txt_file_name);
            btnShare = itemView.findViewById(R.id.btn_share_item); // Check ID in XML
            btnDelete = itemView.findViewById(R.id.btn_delete_item);
        }
    }

    private void showDeleteDialog(Context context, String filePath, int position) {
        new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle("Delete Design?")
                .setMessage("Are you sure you want to delete this design?")
                .setPositiveButton("Yes, Delete", (dialog, which) -> {
                    File file = new File(filePath);
                    if (file.exists()) {
                        if (file.delete()) {
                            // List mathi item kadhi nakho ane adapter refresh karo
                            designList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, designList.size());
                            Toast.makeText(context, "Design Deleted!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error deleting file", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

}