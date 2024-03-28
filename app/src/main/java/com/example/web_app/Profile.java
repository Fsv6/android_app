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
    protected final int liste = 2;
    protected final int profil = 3;

    private ActivityProfileBinding binding;
    private Profile activity;
    private ViewPagerAdapter adapter;
    private TextView namerecupe, mailrecupe;
    private FirebaseFirestore fStor;
    private FirebaseAuth auth;
    private String userID;
    private ImageView Image, Profile_imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        activity = this;
        Profile_imageView = findViewById(R.id.profile_imageView);
        Image = findViewById(R.id.imageupload);
        fStor = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        Profile_imageView.setImageResource(R.drawable.inconu3);

        namerecupe = findViewById(R.id.name_recupe);
        mailrecupe = findViewById(R.id.mail_recupe);
        userID = auth.getCurrentUser().getUid();

        initView();
        loadUserData();
        MeowBottomNavigation bottomNavigation = findViewById(R.id.bottomnav);
        bottomNavigation.add(new MeowBottomNavigation.Model(profil, R.drawable.baseline_person_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(home, R.drawable.baseline_home_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(liste, R.drawable.baseline_list_24));
        bottomNavigation.show(3, true);
        bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                /* Toast.makeText(MainActivity.this,"Iteam click"+model.getId(),Toast.LENGTH_SHORT).show();*/
                return null;
            }
        });



        bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                Intent intent;
                switch (model.getId()) {
                    case home:
                        // Créer un Intent pour l'activité Home
                        intent = new Intent(Profile.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case liste:
                        // Créer un Intent pour l'activité Dashboard
                        intent = new Intent(Profile.this, ConfirmationSignalment.class);
                        startActivity(intent);
                        break;
                    case profil:
                        // Créer un Intent pour l'activité Profil
                        intent = new Intent(Profile.this, Profile.class);
                        startActivity(intent);
                        break;
                }
                return null;
            }
        });
        Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Profile.this, UploadPhoto.class));
            }
        });
    }

    private void initView() {
        setupViewPager(binding.viewPager);
//        binding.tabLayout.setupWithViewPager(binding.viewPager);


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
        adapter.addFragment(new OneFragment(), "Papaya");
        adapter.addFragment(new TwoFragment(), "Coders");


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
                        String imageUrl = document.getString("imageUrl");
                        if (imageUrl != null) {
                            // Utilise Glide pour charger et afficher l'image à partir de l'URL
                            Glide.with(Profile.this).load(imageUrl).into(Profile_imageView);
                        }
                        String userName = document.getString("Nom");
                        namerecupe.setText(userName);

                        String userEmail = document.getString("Email");
                        mailrecupe.setText(userEmail);
                    }
                }
            }
        });
    }
}