package com.example.web_app;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class signalementadapteradmin extends ArrayAdapter<Signalement> {
    Activity activity;
    int itemResourceId;
    List<Signalement> items;

    public signalementadapteradmin(Activity activity, int itemResourceId,
                              List<Signalement> items){
        super(activity, itemResourceId, items);
        this.activity = activity;
        this.itemResourceId = itemResourceId;
        this.items = items;
    }
    @Override

    public View getView(int position, View convertView, ViewGroup parent){
        View layout = convertView;
        if (convertView == null){
            LayoutInflater inflater = activity.getLayoutInflater();
            layout = inflater.inflate(itemResourceId, parent, false);
        }

        TextView mergedTextView = layout.findViewById(R.id.cipTV); // TextView unique pour afficher la fusion des informations

        Signalement signalement = items.get(position);

        // Séparer la désignation en mots en utilisant l'espace comme délimiteur
        String[] words = signalement.designation.split(" ");

        // Récupérer le premier mot de la désignation
        String firstWord = words[0];

        // Fusionner le premier mot avec le CIP dans une seule chaîne de caractères
        String mergedText = firstWord + " (CIP: " + signalement.CIP13 + ")";

        // Comparer le timestamp du signalement avec le timestamp actuel pour déterminer depuis combien de temps il a été ajouté
        long currentTime = System.currentTimeMillis();
        long signalementTime = signalement.Date.getTime(); // Obtenez le temps en millisecondes directement à partir de l'objet Timestamp
        long difference = currentTime - signalementTime;
        long minutes = difference / (1000 * 60);
        long hours = difference / (1000 * 60 * 60);
        long days = difference / (1000 * 60 * 60 * 24);

        // Déterminer le message à afficher en fonction de la différence de temps
        String timeAgoMessage;
        if (minutes < 60) {
            timeAgoMessage = "Il y a " + minutes + " minutes";
        } else if (hours < 24) {
            timeAgoMessage = "Il y a " + hours + " heures";
        } else {
            timeAgoMessage = "Il y a " + days + " jours";
        }

        // Ajouter le message de temps écoulé à la chaîne fusionnée
        mergedText += "\n" + timeAgoMessage;

        // Définir le texte du TextView avec la chaîne fusionnée
        mergedTextView.setText(mergedText);

        return layout;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
