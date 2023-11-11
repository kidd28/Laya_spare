package com.capstone.texttospeech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.capstone.texttospeech.Adapter.CategoriesAdapter;
import com.capstone.texttospeech.Model.CategoriesModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Category extends AppCompatActivity {
    RecyclerView rv;
    ImageView add;
    CategoriesAdapter adapter;
    ArrayList<CategoriesModel> categoriesModels;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        rv = findViewById(R.id.rv);

        add = findViewById(R.id.add);


        Glide.with(this).load(R.drawable.add).centerCrop().into(add);


        categoriesModels = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);

        rv.setLayoutManager(layoutManager);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Category.this, AddCategory.class));
            }
        });
        loadCategories();

    }
    private void loadCategories() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ProvidedCategory");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()){
                    CategoriesModel model = snap.getValue(CategoriesModel.class);
                    categoriesModels.add(model);
                }
                adapter = new CategoriesAdapter(Category.this, categoriesModels);
                rv.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Category.this, MainActivity.class));
        finish();
    }
}