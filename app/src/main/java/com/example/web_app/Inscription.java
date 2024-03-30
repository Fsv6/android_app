package com.example.web_app;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Inscription extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText Inscr_nom, inscr_prenom, inscr_mail,inscr_motdepasse;
    private Button signupButton;
    private TextView loginRedirectText;
    private FirebaseFirestore fStor;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);
        fStor = FirebaseFirestore.getInstance();

        auth = FirebaseAuth.getInstance();

        Inscr_nom = findViewById(R.id.inscr_nom);
        inscr_prenom = findViewById(R.id.inscr_prenom);
        inscr_mail = findViewById(R.id.inscr_email);
        inscr_motdepasse = findViewById(R.id.inscr_motdepasse);

        signupButton = findViewById(R.id.btninscription);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = inscr_mail.getText().toString().trim();
                String pass = inscr_motdepasse.getText().toString().trim();
                String nom = Inscr_nom.getText().toString().trim();
                String prenom = inscr_prenom.getText().toString().trim();




                if (mail.isEmpty()){
                    inscr_mail.setError("L'email ne peut être vide");
                }
                if (pass.isEmpty()){
                    inscr_motdepasse.setError("Mot de passe ne peut être vide");
                } else{
                    auth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Inscription.this, "Inscription réussie", Toast.LENGTH_SHORT).show();
                                userID = auth.getCurrentUser().getUid();
                                DocumentReference documentReference = fStor.collection("users").document(userID);

                                Map<String, String> user = new HashMap<>();
                                user.put("Nom",nom);
                                user.put("Prenom",prenom);
                                user.put("Email",mail);


                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG,"Onsucces:le profil de l'utilisateur est"+ userID);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG,"En cas d'échec"+ e.toString());

                                    }
                                });

                                startActivity(new Intent(Inscription.this, Connexion.class));
                            } else {
                                Toast.makeText(Inscription.this, "Échec de l'inscription" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

        TextView Condition = findViewById(R.id.condition);
        String texteComplet = "J’accepte les Conditions d’utilisation de PharmAssist et reconnais la Politique de confidentialité.";

        // Créer une SpannableString
        SpannableString spannableString = new SpannableString(texteComplet);

        // Définir les ClickableSpans pour chaque partie du texte
        ClickableSpan conditionsClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                afficherDialogueConditions();
            }
        };
        ClickableSpan confidentialiteClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                afficherDialogueConfidentialite();
            }
        };

        // Appliquer les ClickableSpans au TextView
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 70, 99, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 14, 53, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(conditionsClickableSpan, 14, 53, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(confidentialiteClickableSpan, 70, 99, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Activer la gestion des clics sur le TextView
        Condition.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());

        // Appliquer la SpannableString au TextView
        Condition.setText(spannableString);
    }

    private void afficherDialogueConditions() {
        String messageConditions = "En acceptant nos conditions d'utilisation, vous vous engagez à respecter les règles suivantes :\n" +
                "1. Vous ne devez pas utiliser notre service à des fins illicites.\n" +
                "2. Vous ne devez pas violer les droits de propriété intellectuelle d'autrui.\n" +
                "3. Vous ne devez pas perturber le fonctionnement normal de notre service.\n" +
                "4. Vous ne devez pas diffuser de contenu offensant ou inapproprié.\n" +
                "En cas de violation de ces règles, nous nous réservons le droit de suspendre ou de résilier votre compte.";

        // Création du dialogue
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Conditions d'utilisation")
                .setMessage(messageConditions)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void afficherDialogueConfidentialite() {
        // Message spécifique pour la Politique de confidentialité
        String messageConfidentialite = "Dans le cadre de notre politique de confidentialité, nous nous engageons à protéger vos informations personnelles. " +
                "Nous ne partagerons jamais vos données avec des tiers sans votre consentement.\n" +
                "Vous devez également accepter de ne pas partager votre identifiant ou mot de passe avec d'autres utilisateurs.";

        // Création du dialogue
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Politique de confidentialité")
                .setMessage(messageConfidentialite)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}