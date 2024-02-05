package com.capstone.texttospeech;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AddCategory extends AppCompatActivity {
 Button Filipino, English;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category2);

        Filipino = findViewById(R.id.Filipino);
        English = findViewById(R.id.English);

        Filipino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddCategory.this, AddCategoryFil.class));
            }
        });
        English.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddCategory.this, AddCategoryEng.class));
            }
        });
    }
}