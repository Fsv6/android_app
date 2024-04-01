package com.example.web_app;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.web_app.databinding.ActivityMainBinding;
import com.example.web_app.databinding.ActivityProfileBinding;
import com.example.web_app.fragment.OneFragment;
import com.example.web_app.fragment.TwoFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class Profile extends AppCompatActivity {
    protected final int home = 1;
    protected final int param = 2;
    protected final int profil = 3;

    private ActivityProfileBinding binding;
    private Profile activity;
    private ViewPagerAdapter adapter;
    private TextView namerecupe, mailrecupe;
    private FirebaseFirestore fStor;
    private FirebaseAuth auth;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        activity = this;
        fStor = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        namerecupe = findViewById(R.id.name_recupe);
        mailrecupe = findViewById(R.id.mail_recupe);
        userID = auth.getCurrentUser().getUid();

        initView();
        loadUserData();
        MeowBottomNavigation bottomNavigation = findViewById(R.id.bottomnav);
        bottomNavigation.add(new MeowBottomNavigation.Model(profil, R.drawable.baseline_person_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(home, R.drawable.baseline_home_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(param, R.drawable.ic_parametres));
        bottomNavigation.show(3, true);
        bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                return null;
            }
        });



        bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                Intent intent;
                switch (model.getId()) {
                    case home:
                        intent = new Intent(Profile.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case param:
                        intent = new Intent(Profile.this, ParameterActivity.class);
                        startActivity(intent);
                        break;
                    case profil:
                        intent = new Intent(Profile.this, Profile.class);
                        startActivity(intent);
                        break;
                }
                return null;
            }
        });
    }

    private void initView() {
        setupViewPager(binding.viewPager);


        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position)-> {tab.setText(adapter.mFragmentTitleList.get(position));
//                tab.setCustomView(R.layout.custom_tab);
                }).attach();

        for (int i = 0; i < binding.tabLayout.getTabCount(); i++){

            TextView tv = (TextView) LayoutInflater.from(activity)
                    .inflate(R.layout.custom_tab, null);

            binding.tabLayout.getTabAt(i).setCustomView(tv);
        }
    }

    private void setupViewPager(ViewPager2 viewPager) {
        adapter =new ViewPagerAdapter(activity.getSupportFragmentManager(),
                activity.getLifecycle()     );
        adapter.addFragment(new OneFragment(), "Vos signalements");
        adapter.addFragment(new TwoFragment(), "Vos renouvellements");


        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);

    }



    class ViewPagerAdapter extends FragmentStateAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String>mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(@NonNull @NotNull FragmentManager fragmentManager, @NonNull @NotNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        public void addFragment(Fragment fragment, String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @NonNull
        @NotNull
        @Override
        public Fragment createFragment(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getItemCount() {
            return mFragmentList.size();
        }
    }
    private void loadUserData() {
        DocumentReference documentReference = fStor.collection("users").document(userID);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        String userNom = document.getString("Nom");
                        String userPrenom = document.getString("Prenom");

                        namerecupe.setText(userNom + " " + userPrenom);

                        String userEmail = document.getString("Email");
                        mailrecupe.setText(userEmail);
                    }
                }
            }
        });
    }
}