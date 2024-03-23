package com.example.web_app;

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
import android.view.View;
import android.widget.TextView;

public class Inscription extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

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
        // Message spécifique pour les Conditions d'utilisation
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