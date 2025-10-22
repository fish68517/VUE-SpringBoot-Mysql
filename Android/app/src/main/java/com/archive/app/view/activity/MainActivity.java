package com.archive.app.view.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.archive.app.R;
import com.archive.app.view.fragment.InboundFragment;
import com.archive.app.view.fragment.OutboundFragment;
import com.archive.app.view.fragment.ProfileFragment;
import com.archive.app.view.fragment.QueryFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


/*
  Required dependencies in app/build.gradle.kts (or build.gradle):
  implementation("com.google.android.material:material:1.11.0") // Or latest
  implementation("androidx.navigation:navigation-fragment:2.7.7") // Optional for Nav Component
  implementation("androidx.navigation:navigation-ui:2.7.7") // Optional for Nav Component
  implementation("com.journeyapps:zxing-android-embedded:4.3.0")
  implementation("com.squareup.retrofit2:retrofit:2.9.0")
  implementation("com.squareup.retrofit2:converter-gson:2.9.0")
  implementation("com.squareup.okhttp3:logging-interceptor:4.11.0") // Optional logging
 */

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("企业仓库管理");
        setSupportActionBar(toolbar);


        // Load the default fragment
        if (savedInstanceState == null) {
            loadFragment(new InboundFragment());
        }



        // Handle bottom navigation item selection
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_inbound) {
                    selectedFragment = new InboundFragment();
                } else if (itemId == R.id.navigation_outbound) {
                    selectedFragment = new OutboundFragment();
                } else if (itemId == R.id.navigation_query) {
                    selectedFragment = new QueryFragment();
                } else if (itemId == R.id.navigation_profile) {
                    selectedFragment = new ProfileFragment();
                }

                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                    return true;
                }
                return false;
            }
        });
    }

    // Method to replace fragments in the container
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        // transaction.addToBackStack(null); // Optional: Add transaction to back stack
        transaction.commit();
    }
}
