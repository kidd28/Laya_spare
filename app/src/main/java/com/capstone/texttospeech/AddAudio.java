package com.capstone.texttospeech;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AddAudio extends AppCompatActivity {
    Button Filipino, English;
    String category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_audio2);
        Filipino = findViewById(R.id.Filipino);
        English = findViewById(R.id.English);
        category = getIntent().getExtras().getString("Category");
        Filipino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AddAudio.this, AddAudioFil.class);
                i.putExtra("Category", category);
                startActivity(i);


            }
        });
        English.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AddAudio.this, AddAudioEng.class);
                i.putExtra("Category", category);
                startActivity(i);
            }
        });
    }
}