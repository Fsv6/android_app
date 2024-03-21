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

        // Définir la couleur bleue pour la première partie du texte
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.ecriture)), 0, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.bg)), 14, 53, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new UnderlineSpan(), 14, 53, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 14, 53, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.ecriture)), 54, 70, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.bgs)), 70, 99, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new UnderlineSpan(), 70, 99, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 70, 99, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // Action à effectuer lors du clic, par exemple ouvrir un lien
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com"));
                startActivity(intent);
            }
        };

        spannableString.setSpan(clickableSpan, 14, 53, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);




        // Activer la gestion des clics sur le TextView
        Condition.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());

        // Appliquer la SpannableString au TextView
        Condition.setText(spannableString);


    }
}