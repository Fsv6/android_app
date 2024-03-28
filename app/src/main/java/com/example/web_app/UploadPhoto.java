package com.example.web_app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UploadPhoto extends AppCompatActivity {
    StorageReference storageReference;
    LinearProgressIndicator progressIndicator;
    Uri image;
    Button uploadImage, selectImage;
    ImageView imageView;
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {
                    uploadImage.setEnabled(true);
                    image = result.getData().getData();
                    Glide.with(getApplicationContext()).load(image).into(imageView);
                }
            } else {
                Toast.makeText(UploadPhoto.this, "Please select an image", Toast.LENGTH_SHORT).show();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);

        FirebaseApp.initializeApp(UploadPhoto.this);
        storageReference = FirebaseStorage.getInstance().getReference();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressIndicator = findViewById(R.id.progress);

        imageView = findViewById(R.id.imageView);
        selectImage = findViewById(R.id.selectImage);
        uploadImage = findViewById(R.id.uploadImage);

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);
            }
        });
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage(image);
            }
        });
    }

    private void storeImageUrlInFirestore(String imageUrl) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> imageMap = new HashMap<>();
            imageMap.put("imageUrl", imageUrl);
            db.collection("users").document(userId).update(imageMap)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(UploadPhoto.this, "Image URL stored in Firestore", Toast.LENGTH_SHORT).show();
                        // Passer à l'activité suivante avec l'URL de l'image
                        Intent intent = new Intent(UploadPhoto.this, Profile.class);
                        intent.putExtra("imageUrl", imageUrl);
                        startActivity(intent);
                        finish(); // Optionnel, si vous souhaitez fermer cette activité après le téléchargement
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(UploadPhoto.this, "Failed to store image URL in Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void uploadImage(Uri file) {
        StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
        ref.putFile(file).addOnSuccessListener(taskSnapshot -> {
            // Récupérer l'URL de téléchargement de l'image
            ref.getDownloadUrl().addOnSuccessListener(uri -> {
                // Stoker l'URL de l'image dans Firebase Firestore
                storeImageUrlInFirestore(uri.toString());
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(UploadPhoto.this, "Failed!" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }).addOnProgressListener(taskSnapshot -> {
            progressIndicator.setMax(Math.toIntExact(taskSnapshot.getTotalByteCount()));
            progressIndicator.setProgress(Math.toIntExact(taskSnapshot.getBytesTransferred()));
        });
    }

}