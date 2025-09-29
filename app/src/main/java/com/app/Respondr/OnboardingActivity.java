package com.app.Respondr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private MaterialButton btnNext, btnSkip;
    private OnboardingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        initializeViews();
        setupViewPager();
        setClickListeners();
    }

    private void initializeViews() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        btnNext = findViewById(R.id.btnNext);
        btnSkip = findViewById(R.id.btnSkip);
    }

    private void setupViewPager() {
        adapter = new OnboardingAdapter(this);
        viewPager.setAdapter(adapter);

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // Tab text is not needed for dots, but we can set it if needed
        }).attach();

        // Listen to page changes to update button text
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateButtonText(position);
            }
        });
    }

    private void setClickListeners() {
        btnNext.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem < adapter.getItemCount() - 1) {
                viewPager.setCurrentItem(currentItem + 1);
            } else {
                navigateToMainApp();
            }
        });

        btnSkip.setOnClickListener(v -> navigateToMainApp());
    }

    private void updateButtonText(int position) {
        if (position == adapter.getItemCount() - 1) {
            btnNext.setText("Get Started");
        } else {
            btnNext.setText("Next");
        }
    }

    private void navigateToMainApp() {
        Intent intent = new Intent(this, ContactPermissionActivity.class);
        startActivity(intent);
        finish();
    }

    // Fragment adapter for onboarding pages
    private static class OnboardingAdapter extends FragmentStateAdapter {

        public OnboardingAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return OnboardingFragment.newInstance(
                        "Feature 1",
                        "Discover amazing features that will enhance your experience with our app.",
                        R.drawable.ic_feature_1
                    );
                case 1:
                    return OnboardingFragment.newInstance(
                        "Feature 2",
                        "Connect with others and share your experiences in a safe and secure environment.",
                        R.drawable.ic_feature_2
                    );
                case 2:
                    return OnboardingFragment.newInstance(
                        "Feature 3",
                        "Get personalized recommendations and insights tailored just for you.",
                        R.drawable.ic_feature_3
                    );
                default:
                    return OnboardingFragment.newInstance("", "", 0);
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}
