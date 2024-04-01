package com.example.web_app;


import static com.example.web_app.localDb.AppDatabase.MIGRATION_1_2;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.web_app.localDb.AppDatabase;
import com.example.web_app.localDb.SignalementEntity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CipActivity extends AppCompatActivity {

    private EditText codeCipEditText;
    private EditText descriptionEditText;
    private Button validateButton;
    private Button dataMatrixButton;
    private FirebaseFirestore firestore;

    private AppDatabase appDatabase;
    private List<SignalementEntity> offlineSignalements = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cip);
        


        firestore = FirebaseFirestore.getInstance();
        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "signalements-db").addMigrations(MIGRATION_1_2).allowMainThreadQueries().build();

        codeCipEditText = findViewById(R.id.cipEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        validateButton = findViewById(R.id.validateButton);
        dataMatrixButton = findViewById(R.id.dataMButton);

        dataMatrixButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CipActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkAvailable()) {
                    validateCipOnline();
                } else {
                    saveOfflineSignalement();

                }
            }
        });
    }

    private void saveOfflineSignalement() {
        try {
            SignalementEntity signalementEntity = new SignalementEntity();
            String codeCipStr = codeCipEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();
            long codeCip = Long.parseLong(codeCipStr);
            signalementEntity.setCIP(codeCip);
            signalementEntity.setDescription(description);
            signalementEntity.setDate(new Date());
            signalementEntity.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
            signalementEntity.setTraite(false);
            offlineSignalements.add(signalementEntity);
            new Thread(() -> appDatabase.signalementDao().insert(signalementEntity)).start();
            Toast.makeText(CipActivity.this, "Le signalement a été sauvegardé en local", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(CipActivity.this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void validateCipOnline() {
        String codeCipStr = codeCipEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        long codeCip = Long.parseLong(codeCipStr);
        if (TextUtils.isEmpty(codeCipStr)){
            Toast.makeText(this, "Veuillez entrer un code CIP", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        firestore.collection("Medicaments")
                .whereEqualTo("CIP13", codeCip)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot document = task.getResult();
                            if (!document.isEmpty()){
                                String denomination = document.getDocuments().get(0).getString("Denomination");
                                addSignalement(codeCip, denomination, userId, description);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(CipActivity.this);
                                builder.setTitle("Code CIP invalide");
                                builder.setMessage("Le code CIP spécifié n'existe pas dans la base de données.");
                                builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                                builder.create().show();
                            }
                        } else {
                            Toast.makeText(CipActivity.this, "Erreur : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
    private void addSignalement(long codeCip, String denomination, String userId, String description){
        Map<String, Object> signalement = new HashMap<>();
        signalement.put("CIP13", codeCip);
        signalement.put("denomination", denomination);
        signalement.put("user_id", userId);
        signalement.put("Date", new Date());
        signalement.put("traité", false);
        signalement.put("description", description);

        firestore.collection("Signalements").add(signalement)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Intent intent = new Intent(CipActivity.this, ConfirmationSignalment.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CipActivity.this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}