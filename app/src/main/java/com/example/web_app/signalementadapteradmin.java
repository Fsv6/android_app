package com.example.web_app;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class signalementadapteradmin extends ArrayAdapter<Signalement> {
    private Activity activity;
    private int itemResourceId;
    private List<Signalement> items;

    public signalementadapteradmin(Activity activity, int itemResourceId, List<Signalement> items) {
        super(activity, itemResourceId, items);
        this.activity = activity;
        this.itemResourceId = itemResourceId;
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(itemResourceId, parent, false);
            holder = new ViewHolder();
            holder.cipTV = convertView.findViewById(R.id.cipTV);
            holder.dateTV = convertView.findViewById(R.id.dateTV);
            holder.checkBoxTraitement = convertView.findViewById(R.id.signalementCheckBox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Signalement signalement = getItem(position);
        if (signalement != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            holder.cipTV.setText(String.format("CIP: %s - %s", signalement.CIP13, signalement.designation));
            holder.dateTV.setText(dateFormat.format(signalement.Date));
            holder.checkBoxTraitement.setChecked(signalement.traité);

            holder.checkBoxTraitement.setOnClickListener(v -> {
                boolean isChecked = holder.checkBoxTraitement.isChecked();
                // Here, update the 'traité' status of the Signalement
                signalement.traité = isChecked;

                // Update Firestore document
                FirebaseFirestore.getInstance()
                        .collection("Signalements")
                        .document(String.valueOf(signalement.CIP13)) // Assure that CIP13 can be used as a unique ID
                        .update("traité", isChecked)
                        .addOnSuccessListener(aVoid -> {
                            // If the update was successful, you might want to refresh the ListView
                            // notifyDataSetChanged(); // Be cautious with this line; it can cause an infinite loop if not handled properly.
                        });

            });
        }

        return convertView;
    }

    static class ViewHolder {
        TextView cipTV;
        TextView dateTV;
        CheckBox checkBoxTraitement;
    }

}

