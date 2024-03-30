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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class CipActivity extends AppCompatActivity {

    private EditText codeCipEditText;
    private Button validateButton;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cip);

        firestore = FirebaseFirestore.getInstance();

        codeCipEditText = findViewById(R.id.usernameEditText);
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
        long codeCip = Long.parseLong(codeCipStr);
        if (TextUtils.isEmpty(codeCipStr)){
            Toast.makeText(this, "Veuillez entrer un code CIP", Toast.LENGTH_SHORT).show();
            return;
        }
        firestore.collection("Medicaments")
                .whereEqualTo("CIP13", codeCip)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot document = task.getResult();
                            if (!document.isEmpty()){
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("signale_manquant", "true");
                                firestore.collection("Medicaments")
                                        .document(document.getDocuments().get(0).getId())
                                        .update(updates);
                                Intent intent = new Intent(CipActivity.this, ConfirmationSignalment.class);
                                startActivity(intent);
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
}

