package com.capstone.texttospeech.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.capstone.texttospeech.AudioList;
import com.capstone.texttospeech.Model.CategoriesModel;
import com.capstone.texttospeech.R;

import java.util.ArrayList;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.HolderAdapter> {

    Context context;
    ArrayList<CategoriesModel> categoriesModels;

    public CategoriesAdapter(Context context, ArrayList<CategoriesModel> model){
        this.context = context;
        this.categoriesModels = model;
    }

    @NonNull
    @Override
    public HolderAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list, parent, false);
        return new HolderAdapter(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderAdapter holder, int position) {
        CategoriesModel categoriesModel = categoriesModels.get(position);
        String category = categoriesModel.getCategory();
        String image = categoriesModel.getImageLink();

        holder.text.setText(category);
        Glide.with(context).load(image).centerCrop().into(holder.img);
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, AudioList.class);
                i.putExtra("Category", category);
                context.startActivity(i);
                ((Activity)context).finish();
            }
        });
    }
    @Override
    public int getItemCount() {
        return categoriesModels.size();
    }

    public class HolderAdapter extends RecyclerView.ViewHolder {
        ImageView img;
        TextView text;
        CardView card;
        public HolderAdapter(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.img);
            text = itemView.findViewById(R.id.name);
            card = itemView.findViewById(R.id.card);
        }
    }
}
