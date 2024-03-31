package com.example.web_app.fragment;

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
import com.example.web_app.signalementadapter;
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

public class OneFragment extends Fragment {
    private FirebaseFirestore db;

    private FirebaseAuth auth;


    public OneFragment() {
        // Required empty public constructor
    }

    public static OneFragment newInstance() {
        return new OneFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_one, container, false);

        // Récupérer la référence de la ListView
        ListView listView = view.findViewById(R.id.scrollView);

        // Récupérer l'ID de la personne connectée (vous devez implémenter cette partie)
        String userID = auth.getCurrentUser().getUid();


        // Récupérer les données des signalements depuis Firestore
        /*db.collection("Signalements")
                .whereEqualTo("user_id", userID)
               *//* .whereEqualTo("traité", true)// Filtrer par l'ID de l'utilisateur connecté*//*
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

                            // Créer et configurer l'adaptateur pour afficher les signalements dans la ListView
                            signalementadapter adapter = new signalementadapter(getActivity(),
                                    R.layout.item_signalement,
                                    signalements);
                            listView.setAdapter(adapter);
                        } else {
                            // Gérer les erreurs de récupération des données depuis Firestore
                            // Vous pouvez afficher un message d'erreur ou effectuer une autre action ici
                            Exception e = task.getException();
                            Log.e("Firebase", "Error getting documents: " + e);
                        }
                    }
                });*/
        db.collection("Signalements")
                .whereEqualTo("user_id", userID)
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
                            signalementadapter adapter = new signalementadapter(getActivity(),
                                    R.layout.item_signalement,
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

/*import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.web_app.R;
import com.example.web_app.Signalement;
import com.example.web_app.signalementadapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class OneFragment extends Fragment {
    private FirebaseFirestore db;

    public OneFragment() {
        // Required empty public constructor
    }

    public static OneFragment newInstance() {
        return new OneFragment();
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
        View view = inflater.inflate(R.layout.fragment_one, container, false);

        // Récupérer la référence de la ListView
        ListView listView = view.findViewById(R.id.scrollView);

        // Récupérer les données des signalements depuis Firestore
        db.collection("Signalements").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Signalement> signalements = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        // Récupérer les données de chaque signalement et les ajouter à la liste
                        Signalement signalement = document.toObject(Signalement.class);
                        signalements.add(signalement);
                    }

                    // Créer et configurer l'adaptateur pour afficher les signalements dans la ListView
                    signalementadapter adapter = new signalementadapter(getActivity(),
                            R.layout.item_signalement,
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
}*/
