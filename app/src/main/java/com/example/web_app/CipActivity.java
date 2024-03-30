package com.example.web_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CipActivity extends AppCompatActivity {

    private EditText codeCipEditText;
    private EditText descriptionEditText;
    private Button validateButton;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cip);

        firestore = FirebaseFirestore.getInstance();

        codeCipEditText = findViewById(R.id.cipEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        validateButton = findViewById(R.id.validateButton);

        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateCip();
            }
        });
    }

    private void validateCip() {
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
    private void addSignalement(long codeCip, String designation, String userId, String description){
        Map<String, Object> signalement = new HashMap<>();
        signalement.put("CIP13", codeCip);
        signalement.put("designation", designation);
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
}