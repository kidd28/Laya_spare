package com.capstone.texttospeech;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.colorpicker.ColorPickerDialog;
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog;
import com.github.dhaval2404.colorpicker.listener.ColorListener;
import com.github.dhaval2404.colorpicker.model.ColorShape;
import com.github.dhaval2404.colorpicker.model.ColorSwatch;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class AddCategoryFil extends AppCompatActivity {

    Button  upload,uploadImg,colorPicker, materialColor;
    EditText categoryName;
    ImageView image, back;
    private final int PICK_IMAGE_REQUEST = 22;

    private Uri filePath;
    String[] storagePermission;


    FirebaseStorage storage;
    StorageReference storageReference;
    String categoryname, Selectedcolor;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        upload = findViewById(R.id.save);
        uploadImg = findViewById(R.id.uploadImage);
        categoryName = findViewById(R.id.EditText);
        image = findViewById(R.id.CatgoryImage);
        colorPicker = findViewById(R.id.colorPicker);
        materialColor = findViewById(R.id.materialColor);


        storage = FirebaseStorage.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        storageReference = storage.getReference().child("ProvidedCategory");

        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryname = categoryName.getText().toString();
                uploadImage(categoryname);
            }
        });




        colorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ColorPickerDialog
                        .Builder(AddCategoryFil.this)
                        .setTitle("Pick Color")
                        .setColorShape(ColorShape.SQAURE)
                        .setDefaultColor(R.color.white)
                        .setColorListener(new ColorListener() {
                            @Override
                            public void onColorSelected(int color, @NotNull String colorHex) {
                                Selectedcolor = colorHex; // Handle Color Selection
                            }
                        })
                        .show();
            }
        });

        materialColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialColorPickerDialog
                        .Builder(AddCategoryFil.this)
                        .setTitle("Pick Color")
                        .setColorShape(ColorShape.SQAURE)
                        .setColorSwatch(ColorSwatch._300)
                        .setDefaultColor(R.color.white)
                        .setColorListener(new ColorListener() {
                            @Override
                            public void onColorSelected(int color, @NotNull String colorHex) {
                                Selectedcolor = colorHex;
                            }
                        })
                        .show();
            }
        });
    }

    private void SelectImage() {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the Uri of data
            filePath = data.getData();
            try {
                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                image.setImageBitmap(bitmap);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(String categoryName) {
        if (filePath != null) {
            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            // Defining the child of storageReference
            StorageReference ref = storageReference.child("Category_Images/" + UUID.randomUUID().toString());
            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!uriTask.isSuccessful()) ;
                                    if (uriTask.isSuccessful()) {
                                        String downloadUri = uriTask.getResult().toString();
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ProvidedCategory").child("Filipino");
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("Category", categoryName);
                                        hashMap.put("ImageLink", downloadUri);
                                        hashMap.put("Color", Selectedcolor);
                                        hashMap.put("UserUID", "Admin");

                                        reference.child(categoryName).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                startActivity(new Intent(AddCategoryFil.this, Category.class));
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });
                                    }
                                    progressDialog.dismiss();
                                    Toast.makeText(AddCategoryFil.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast.makeText(AddCategoryFil.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {
                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress
                                            = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                                }
                            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AddCategoryFil.this, Category.class));
        finish();
    }
}