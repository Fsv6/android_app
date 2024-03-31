package com.example.web_app;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import kotlin.Unit;

import com.bumptech.glide.Glide;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {
    protected final int home = 1;
    protected final int liste = 2;
    protected final int profil = 3;
    private TextView TextViewGreeting;
    private FirebaseFirestore fStor;
    private FirebaseAuth auth;
    private String userID;
    private Button Scan, btnCip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fStor = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userID = auth.getCurrentUser().getUid();
        TextViewGreeting = findViewById(R.id.textViewGreeting);
        Scan = findViewById(R.id.scan);
        Scan.setOnClickListener(v -> {
            Scanecode();
        });
        btnCip = findViewById(R.id.button_cip);
        btnCip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CipActivity.class);
                startActivity(intent);
            }
        });
        loadUserData();
        MeowBottomNavigation bottomNavigation = findViewById(R.id.bottomnav);
        bottomNavigation.add(new MeowBottomNavigation.Model(profil, R.drawable.baseline_person_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(home, R.drawable.baseline_home_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(liste, R.drawable.ic_parametres));
        bottomNavigation.show(1, true);
        bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                /* Toast.makeText(MainActivity.this,"Iteam click"+model.getId(),Toast.LENGTH_SHORT).show();*/
                return null;
            }
        });


        bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                Intent intent;
                switch (model.getId()) {
                    case home:
                        // Créer un Intent pour l'activité Home
                        intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case liste:
                        // Créer un Intent pour l'activité Dashboard
                        intent = new Intent(MainActivity.this, ParameterActivity.class);
                        startActivity(intent);
                        break;
                    case profil:
                        // Créer un Intent pour l'activité Profil
                        intent = new Intent(MainActivity.this, Profile.class);
                        startActivity(intent);
                        break;
                }
                return null;
            }
        });
    }

    private void loadUserData() {
        DocumentReference documentReference = fStor.collection("users").document(userID);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String userName = document.getString("Nom");
                        String userSName = document.getString("Prenom");
                        if (userSName != null) {
                            TextViewGreeting.setText("Bonjour " + userName + " " + userSName + " !");
                        } else {
                            TextViewGreeting.setText("Bonjour " + userName + " !");
                        }


                    }
                }
            }
        });
    }

    private void Scanecode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("volume up to flash");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barcodeLauncher.launch(options);
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            String contents = result.getContents();
            int startIndex = contents.indexOf("3400");

            if (startIndex != -1 && contents.length() >= startIndex + 13) {
                String extractedCode = contents.substring(startIndex, startIndex + 13);
                validateCodeFromScan(extractedCode);
            } else {
                // Gérer le cas où "3400" n'est pas trouvé dans la chaîne ou la longueur restante est insuffisante
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Code CIP invalide");
                builder.setMessage("Le code CIP spécifié est invalide.");
                builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                builder.create().show();
            }
        }
    });

    /*private void validateCodeFromScan(String extractedCode) {
        long codeCip = Long.parseLong(extractedCode);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fStor.collection("Medicaments")
                .whereEqualTo("CIP13", codeCip)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot document = task.getResult();
                            if (!document.isEmpty()) {
                                DocumentSnapshot medDocument = document.getDocuments().get(0);
                                String medNom = medDocument.getString("Denomination");
                                if (medNom != null) {
                                    // Afficher le nom du médicament
                                    Toast.makeText(MainActivity.this, "Le médicament est : " + medNom, Toast.LENGTH_SHORT).show();
                                    // Enregistrer le signalement
                                    addSignalement(codeCip, medNom, userId, "");
                                } else {
                                    Toast.makeText(MainActivity.this, "Le nom du médicament n'est pas disponible pour ce code CIP.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Le code CIP spécifié n'existe pas dans la base de données.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Erreur : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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

        fStor.collection("Signalements").add(signalement)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Intent intent = new Intent(MainActivity.this, ConfirmationSignalment.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }*/
    private void validateCodeFromScan(String extractedCode) {
        long codeCip = Long.parseLong(extractedCode);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fStor.collection("Medicaments")
                .whereEqualTo("CIP13", codeCip)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot document = task.getResult();
                            if (!document.isEmpty()) {
                                DocumentSnapshot medDocument = document.getDocuments().get(0);
                                String medNom = medDocument.getString("Denomination");
                                if (medNom != null) {
                                    // Afficher le nom du médicament
                                    Toast.makeText(MainActivity.this, "Le médicament est : " + medNom, Toast.LENGTH_SHORT).show();
                                    // Demander une description optionnelle
                                    showDescriptionDialog(codeCip, medNom, userId);
                                } else {
                                    Toast.makeText(MainActivity.this, "Le nom du médicament n'est pas disponible pour ce code CIP.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Le code CIP spécifié n'existe pas dans la base de données.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Erreur : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showDescriptionDialog(long codeCip, String medNom, String userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Description (optionnel)");
        final EditText input = new EditText(MainActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String description = input.getText().toString().trim();
                // Enregistrer le signalement avec la description optionnelle
                addSignalement(codeCip, medNom, userId, description);
            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Si l'utilisateur annule, enregistrez le signalement sans description
                addSignalement(codeCip, medNom, userId, "");
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void addSignalement(long codeCip, String designation, String userId, String description){
        Map<String, Object> signalement = new HashMap<>();
        signalement.put("CIP13", codeCip);
        signalement.put("designation", designation);
        signalement.put("user_id", userId);
        signalement.put("Date", new Date());
        signalement.put("traité", false);
        signalement.put("description", description);

        fStor.collection("Signalements").add(signalement)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Intent intent = new Intent(MainActivity.this, ConfirmationSignalment.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
