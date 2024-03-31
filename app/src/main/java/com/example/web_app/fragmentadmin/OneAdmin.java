package com.example.web_app.fragmentadmin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.web_app.R;
import com.example.web_app.Signalement;
import com.example.web_app.fragment.OneFragment;
import com.example.web_app.signalementadapter;
import com.example.web_app.signalementadapteradmin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OneAdmin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OneAdmin extends Fragment {
    private FirebaseFirestore db;



    public OneAdmin() {

    }

    public static com.example.web_app.fragment.OneFragment newInstance() {
        return new com.example.web_app.fragment.OneFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_1admin, container, false);

        ListView listView = view.findViewById(R.id.scrollView1A);
        db.collection("Signalements")
                .whereEqualTo("traité", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Signalement> signalements = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                // Récupérer les données de chaque signalement et les ajouter à la liste
                                Signalement signalement = document.toObject(Signalement.class);
                                signalements.add(signalement);
                            }

                            // Trier les signalements par ordre croissant de leur heure
                            Collections.sort(signalements, new Comparator<Signalement>() {
                                @Override
                                public int compare(Signalement s1, Signalement s2) {
                                    return s2.getDate().compareTo(s1.getDate()); // Comparaison des timestamps
                                }
                            });

                            // Créer et configurer l'adaptateur pour afficher les signalements dans la ListView
                            signalementadapteradmin adapter = new signalementadapteradmin(getActivity(),
                                    R.layout.item_signalement_admin,
                                    signalements);
                            listView.setAdapter(adapter);
                        } else {
                            // Gérer les erreurs de récupération des données depuis Firestore
                            // Vous pouvez afficher un message d'erreur ou effectuer une autre action ici
                            Exception e = task.getException();
                            Log.e("Firebase", "Error getting documents: " + e);
                        }
                    }
                });

        return view;
    }

}
