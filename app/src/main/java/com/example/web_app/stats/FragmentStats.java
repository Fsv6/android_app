package com.example.web_app.stats;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.web_app.Admin;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.fragment.app.Fragment;

import com.example.web_app.R;
import com.example.web_app.Signalement;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.QuerySnapshot;

public class FragmentStats extends Fragment {
    private TextView textViewNbTraites;
    private TextView textViewNbNonTraites;
    private ImageView imageViewBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        textViewNbTraites = view.findViewById(R.id.textViewNbTraites);
        textViewNbNonTraites = view.findViewById(R.id.textViewNbNonTraites);

        afficherStatistiques();

        imageViewBack = view.findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Admin.class);
                startActivity(intent);
            }
        });

        return view;

    }

    private void afficherStatistiques() {
        CollectionReference signalementsRef = FirebaseFirestore.getInstance().collection("Signalements");

        signalementsRef.whereEqualTo("traité", true)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int nbSignalementsTraites = queryDocumentSnapshots.size();
                        textViewNbTraites.setText("Nombre de signalements traités : " + nbSignalementsTraites);
                    }
                });

        signalementsRef.whereEqualTo("traité", false)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int nbSignalementsNonTraites = queryDocumentSnapshots.size();
                        textViewNbNonTraites.setText("Nombre de signalements non traités : " + nbSignalementsNonTraites);
                    }
                });
    }

}
