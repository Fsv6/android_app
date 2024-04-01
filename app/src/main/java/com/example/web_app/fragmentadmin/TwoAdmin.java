package com.example.web_app.fragmentadmin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.web_app.R;
import com.example.web_app.Signalement;
import com.example.web_app.signalementadapteradmin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TwoAdmin extends Fragment {
    private FirebaseFirestore db;
    private ListView listView;
    private List<Signalement> signalements = new ArrayList<>();

    public TwoAdmin() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_2admin, container, false);
        listView = view.findViewById(R.id.scrollView2A);
        updateListView();
        return view;
    }

    private void updateListView() {
        db.collection("Signalements")
                .whereEqualTo("traité", true)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e("TwoAdmin", "Listen failed.", e);
                        return;
                    }

                    signalements.clear();
                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        Signalement signalement = doc.toObject(Signalement.class);
                        signalements.add(signalement);
                    }
                    // Sort by date if necessary, then notify the adapter of a data change
                    Collections.sort(signalements, new Comparator<Signalement>() {
                        @Override
                        public int compare(Signalement s1, Signalement s2) {
                            return s2.getDate().compareTo(s1.getDate());
                        }
                    });

                    signalementadapteradmin adapter = new signalementadapteradmin(getActivity(), R.layout.item_signalement_admin, signalements);
                    listView.setAdapter(adapter);
                });
    }
}
