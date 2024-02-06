package com.capstone.texttospeech;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;
import static android.util.Log.e;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.UUID;

public class AddAudioFil extends AppCompatActivity {
    Button uploadAudio, uploadImage;
    Button upload;
    private static final int REQUEST_PICK_AUDIO = 2;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private final int PICK_IMAGE_REQUEST = 22;
    private static final int REQUEST_STORAGE_PERMISSION = 101;
    private static final int REQUEST_AUDIO_PERMISSION = 102;
    Uri audioUri;
    String fname;
    private Uri imagefilePath;

    boolean words;
    String category;

    ImageView AudioImage;
    TextView audioname;
    FirebaseStorage storage;
    StorageReference storageReference;
    EditText name;

    FirebaseUser user;
    private TextToSpeechHelper textToSpeechHelper;

    String ImageLink, AudioLink, FileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_audio_fil);

        uploadAudio = findViewById(R.id.uploadAudio);
        uploadImage = findViewById(R.id.uploadImage);
        upload = findViewById(R.id.upload);
        AudioImage = findViewById(R.id.CatgoryImage);
        audioname = findViewById(R.id.audioname);
        name = findViewById(R.id.Name);
        user = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();


        textToSpeechHelper = new TextToSpeechHelper(AddAudioFil.this, "Edit");

        storageReference = storage.getReference().child("ProvidedAudio");

        category = getIntent().getExtras().getString("Category");
        uploadAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String option[] = {"Select Audio", "Record Audio", "Text To Speech"};
                AlertDialog.Builder builder = new AlertDialog.Builder(AddAudioFil.this);
                builder.setTitle("Choose Action");
                builder.setItems(option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                if (ContextCompat.checkSelfPermission(AddAudioFil.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                                        != PackageManager.PERMISSION_GRANTED ||
                                        ContextCompat.checkSelfPermission(AddAudioFil.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                                != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(AddAudioFil.this,
                                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            REQUEST_STORAGE_PERMISSION);
                                } else {
                                    pickAudio();
                                }
                                break;
                            case 1:
                                if (ContextCompat.checkSelfPermission(AddAudioFil.this, RECORD_AUDIO)
                                        != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(AddAudioFil.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                                        != PackageManager.PERMISSION_GRANTED ||
                                        ContextCompat.checkSelfPermission(AddAudioFil.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                                != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(AddAudioFil.this, new String[]{RECORD_AUDIO},
                                            REQUEST_AUDIO_PERMISSION);
                                } else {
                                    if (ContextCompat.checkSelfPermission(AddAudioFil.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED ||
                                            ContextCompat.checkSelfPermission(AddAudioFil.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                                    != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(AddAudioFil.this,
                                                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                REQUEST_STORAGE_PERMISSION);
                                    } else {

                                    }
                                }
                                break;
                            case 2:
                                File myDirectory = new File(Environment.getExternalStorageDirectory(), "/AudioAAC");
                                if (!myDirectory.exists()) {
                                    checkPermissionForDir();
                                    if (checkPermissionForDir()) {
                                        myDirectory.mkdirs();

                                    } else {
                                        requestPermissionforDR();
                                    }
                                }

                                showTTSDialog();
                                break;
                        }
                    }
                });
                builder.create().show();

            }
        });
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
    }



    private void showTTSDialog() {
        LayoutInflater inflater = LayoutInflater.from(AddAudioFil.this);
        View dialogview = inflater.inflate(R.layout.ttsdialog, null);
        final AlertDialog dialog = new AlertDialog.Builder(AddAudioFil.this)
                .setView(dialogview)
                .setTitle("Input AAC word")
                .setPositiveButton("Save", null) //Set to null. We override the onclick
                .setNegativeButton("Cancel", null)
                .setNeutralButton("Play", null)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button save = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                save.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        //What ever you want to do with the value
                        EditText YouEditTextValue = dialogview.findViewById(R.id.etTTS);
                        //OR
                        String TTS = YouEditTextValue.getText().toString();
                        TTS = TTS.replaceAll("[^a-zA-Z0-9]", " ");

                        textToSpeechHelper.startConvert(TTS, TTS + ".mp3", "Save", "Filipino");
                        dialog.dismiss();
                    }
                });
                Button cancel = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                cancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                Button play = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                play.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        EditText YouEditTextValue = dialogview.findViewById(R.id.etTTS);
                        //OR
                        String TTS = YouEditTextValue.getText().toString();
                        TTS = TTS.replaceAll("[^a-zA-Z0-9]", " ");
                        textToSpeechHelper.startConvert(TTS, TTS + ".mp3", "Play", "Filipino");
                    }
                });
            }
        });
        dialog.show();

    }

    private void save() {
        String id = String.valueOf(System.currentTimeMillis());
        if (name.getText().toString() != null && !name.getText().toString().equals("")) {
            if (category != null && FileName != null && AudioLink != null && ImageLink != null) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ProvidedAudio").child("Filipino");
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("Name", name.getText().toString());
                hashMap.put("Category", category);
                hashMap.put("FileName", FileName);
                hashMap.put("FileLink", AudioLink);
                hashMap.put("ImageLink", ImageLink);
                hashMap.put("Id", id);
                hashMap.put("UserUID", "Admin");
                reference.child(name.getText().toString()).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //  Toast.makeText(AddAudio.this, "Success",Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(AddAudioFil.this, Category.class);
                        i.putExtra("Category", category);
                        startActivity(i);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
            } else {
                Toast.makeText(AddAudioFil.this, "Please add image or audio", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(AddAudioFil.this, "Please enter AAC Name", Toast.LENGTH_SHORT).show();
        }
    }

    private void SelectImage() {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_PICK_AUDIO:
                if (RESULT_OK == resultCode) {
                    if (data != null) {
                        audioUri = data.getData();
                        try {
                            //call the getPath uri with context and uri
                            //To get path from uri
                            String path = getPath(this, audioUri);
                            File file = new File(path);
                            fname = file.getName();
                            fname = fname.replaceAll("\\..*", "");
                            audioname.setText(fname + ".mp3");
                            FileName = fname;
                            uploadAudio(audioUri, fname);
                        } catch (Exception e) {
                            e("Err", e.toString() + "");
                        }
                    }
                }
                break;
            case PICK_IMAGE_REQUEST:
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    // Get the Uri of data
                    imagefilePath = data.getData();
                    try {
                        // Setting image on image view using Bitmap
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagefilePath);
                        AudioImage.setImageBitmap(bitmap);
                        uploadImage(category);
                    } catch (IOException e) {
                        // Log the exception
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public void uploadAudio(Uri audioUri, String filename) {
        String AudioId = String.valueOf(System.currentTimeMillis());
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Audio...");
        progressDialog.show();
        String filePathAndName = category + "/" + filename;
        StorageReference ref = storageReference.child(filePathAndName);
        ref.putFile(audioUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                if (uriTask.isSuccessful()) {
                    String downloadUri = uriTask.getResult().toString();
                    AudioLink = downloadUri;
                    progressDialog.dismiss();
                    Toast.makeText(AddAudioFil.this, "Uploaded", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        }).addOnProgressListener(
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

    public void uploadAduioFromTTS(Uri audioUri, String filename) {
        String AudioId = String.valueOf(System.currentTimeMillis());
        audioname.setText(filename + ".mp3");
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Audio...");
        progressDialog.show();
        String filePathAndName = category + "/" + filename + "_" + AudioId;
        StorageReference ref = storageReference.child(filePathAndName);
        ref.putFile(audioUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                if (uriTask.isSuccessful()) {
                    String downloadUri = uriTask.getResult().toString();
                    FileName = filename;
                    audioname.setText(filename);
                    Toast.makeText(AddAudioFil.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    AudioLink = downloadUri;
                    progressDialog.dismiss();
                }
            }
        });
    }

    public String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private void uploadImage(String categoryName) {
        if (imagefilePath != null) {
            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading Image...");
            progressDialog.show();
            // Defining the child of storageReference
            StorageReference ref = storageReference.child(categoryName + "_" + "Images/" + UUID.randomUUID().toString());
            // adding listeners on upload
            // or failure of image
            ref.putFile(imagefilePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!uriTask.isSuccessful()) ;
                                    if (uriTask.isSuccessful()) {
                                        String ImagedownloadUri = uriTask.getResult().toString();
                                        ImageLink = ImagedownloadUri;
                                    }
                                    progressDialog.dismiss();
                                    Toast.makeText(AddAudioFil.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast.makeText(AddAudioFil.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Storage permissions granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Storage permissions denied", Toast.LENGTH_SHORT).show();
                }
                break;

            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean READ_EXTERNAL_STORAGE = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean WRITE_EXTERNAL_STORAGE = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (READ_EXTERNAL_STORAGE && WRITE_EXTERNAL_STORAGE) {
                        Toast.makeText(this, "Storage permissions granted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Storage permissions denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case REQUEST_AUDIO_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Audio permission granted", Toast.LENGTH_SHORT).show();
                    if (ContextCompat.checkSelfPermission(AddAudioFil.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(AddAudioFil.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddAudioFil.this,
                                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_STORAGE_PERMISSION);
                    } else {

                    }
                } else {
                    Toast.makeText(this, "Audio permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void pickAudio() {
        Intent pickAudioIntent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickAudioIntent, REQUEST_PICK_AUDIO);
    }

    private void requestPermissionforDR() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(AddAudioFil.this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private boolean checkPermissionForDir() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int result = ContextCompat.checkSelfPermission(AddAudioFil.this, READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(AddAudioFil.this, WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(AddAudioFil.this, Category.class);
        i.putExtra("Category", category);
        startActivity(i);
        finish();
    }

}