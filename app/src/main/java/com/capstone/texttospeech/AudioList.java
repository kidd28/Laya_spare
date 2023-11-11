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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.capstone.texttospeech.Adapter.AudioAdapter;
import com.capstone.texttospeech.Model.AudioModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.common.returnsreceiver.qual.This;

import java.util.ArrayList;
import java.util.Objects;

public class AudioList extends AppCompatActivity {
    RecyclerView rv;
    ImageView add;

    TextView cat;
    ArrayList<AudioModel> audioModels;
    String category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_list);

        rv = findViewById(R.id.rv);
        cat = findViewById(R.id.cat);
        add = findViewById(R.id.add);

        category = getIntent().getExtras().getString("Category");
        cat.setText(category);
        Glide.with(this).load(R.drawable.add).centerCrop().into(add);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AudioList.this, AddAudio.class);
                i.putExtra("Category", category);
                startActivity(i);
            }
        });

        audioModels = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);

        rv.setLayoutManager(layoutManager);
        loadAudio();

    }
    private void loadAudio() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ProvidedAudio");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()){
                    if (Objects.equals(snap.child("Category").getValue(), category)) {
                                AudioModel model = snap.getValue(AudioModel.class);
                                audioModels.add(model);
                    }
                }
                AudioAdapter adapter = new AudioAdapter(AudioList.this, audioModels);
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
        startActivity(new Intent(AudioList.this, Category.class));
        finish();
    }
}