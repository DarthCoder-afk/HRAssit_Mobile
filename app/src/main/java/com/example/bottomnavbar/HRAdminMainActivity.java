package com.example.bottomnavbar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class HRAdminMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FloatingActionButton fab;
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hrmain);

        bottomNavigationView = findViewById(R.id.hr_bottomNavigationView);
        fab = findViewById(R.id.hr_fab);
        drawerLayout = findViewById(R.id.hr_drawer_layout);

        NavigationView navigationView = drawerLayout.findViewById(R.id.hr_nav_view);
        Toolbar toolbar = findViewById(R.id.hr_toolbar);
        navigationView.setNavigationItemSelectedListener(this);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.hr_frame_layout, new HRHomeFragment()).commit();
            navigationView.setCheckedItem(R.id.hr_nav_home);
        }

        replaceFragment(new HRHomeFragment());

        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.hr_home) {
                replaceFragment(new HRHomeFragment());
            } else if (itemId == R.id.hr_notification) {
                replaceFragment(new HRNotificationFragment());
            }

            return true;
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });
    }
    //Outside oncreate

    private  void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.hr_frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void showBottomDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.hr_bottomheaderlayout);

        LinearLayout emp_request = dialog.findViewById(R.id.employee_requests_layout);
        ImageView cancelButton = dialog.findViewById(R.id.hr_cancelButton);

        emp_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Toast.makeText(HRAdminMainActivity.this,"Employee Request",Toast.LENGTH_SHORT).show();
                replaceFragment(new HREmployeeRequests());

            }
        });



        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        int itemId = item.getItemId();

        if (itemId == R.id.hr_nav_home) {
            replaceFragment(new HRHomeFragment());
        } else if (itemId == R.id.hr_nav_history) {
            replaceFragment(new HRHistory());
        } else if (itemId == R.id.hr_nav_profile) {
            replaceFragment(new HRProfile());
        } else if (itemId == R.id.hr_nav_logout) {
            startActivity(new Intent(this, Login.class));
        } else if (itemId == R.id.hr_nav_emplpoyees) {
            replaceFragment(new HREmployeeRequests()); }
        return true;
    }
}
