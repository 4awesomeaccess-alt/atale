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

    // Share File Function — WhatsApp dialog sathe
    private void shareJsonFile(Context context, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            Toast.makeText(context, "File not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);

        // Dialog banavo
        androidx.appcompat.app.AlertDialog.Builder dlg =
                new androidx.appcompat.app.AlertDialog.Builder(context);
        dlg.setTitle("Share Design");

        android.widget.LinearLayout root = new android.widget.LinearLayout(context);
        root.setOrientation(android.widget.LinearLayout.VERTICAL);
        int pad = (int) (16 * context.getResources().getDisplayMetrics().density);
        root.setPadding(pad, pad, pad, pad);

        // WhatsApp button
        android.widget.Button btnWA = new android.widget.Button(context);
        btnWA.setText("WhatsApp");
        btnWA.setBackgroundColor(android.graphics.Color.parseColor("#25D366"));
        btnWA.setTextColor(android.graphics.Color.WHITE);
        btnWA.setOnClickListener(v -> shareViaWhatsApp(context, "com.whatsapp", uri));
        root.addView(btnWA);

        // WhatsApp Business button
        android.widget.Button btnWAB = new android.widget.Button(context);
        btnWAB.setText("WhatsApp Business");
        btnWAB.setBackgroundColor(android.graphics.Color.parseColor("#128C7E"));
        btnWAB.setTextColor(android.graphics.Color.WHITE);
        android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, pad / 2, 0, 0);
        btnWAB.setLayoutParams(lp);
        btnWAB.setOnClickListener(v -> shareViaWhatsApp(context, "com.whatsapp.w4b", uri));
        root.addView(btnWAB);

        // Other Apps button
        android.widget.Button btnOther = new android.widget.Button(context);
        btnOther.setText("Other Apps");
        btnOther.setBackgroundColor(android.graphics.Color.parseColor("#1565C0"));
        btnOther.setTextColor(android.graphics.Color.WHITE);
        android.widget.LinearLayout.LayoutParams lp2 = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
        lp2.setMargins(0, pad / 2, 0, 0);
        btnOther.setLayoutParams(lp2);
        btnOther.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("application/json");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(intent, "Share Design via"));
        });
        root.addView(btnOther);

        dlg.setView(root);
        dlg.setNegativeButton("Cancel", null);
        dlg.show();
    }

    private void shareViaWhatsApp(Context context, String pkg, Uri uri) {
        try {
            context.getPackageManager().getPackageInfo(pkg, 0);
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            String name = pkg.contains("w4b") ? "WhatsApp Business" : "WhatsApp";
            Toast.makeText(context, name + " installed nathi!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent wa = new Intent(Intent.ACTION_SEND);
        wa.setPackage(pkg);
        wa.setType("application/json");
        wa.putExtra(Intent.EXTRA_STREAM, uri);
        wa.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            context.startActivity(wa);
        } catch (Exception e) {
            Toast.makeText(context, "Share karva ma error aavyo.", Toast.LENGTH_SHORT).show();
        }
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