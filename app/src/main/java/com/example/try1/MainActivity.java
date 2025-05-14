package com.example.try1;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.google.android.material.navigation.NavigationView;

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.try1.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_grid, R.id.nav_cam1, R.id.nav_cam2, R.id.nav_cam3, R.id.nav_cam4, R.id.nav_cam5)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        NavigationView nav = (NavigationView)findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_grid) {
                    navController.navigate(R.id.nav_grid);
                    DrawerLayout mDrawerLayout;
                    mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
                    mDrawerLayout.closeDrawers();
                } else if (id == R.id.nav_cam1) {
                    navController.navigate(R.id.nav_cam1);
                    DrawerLayout mDrawerLayout;
                    mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
                    mDrawerLayout.closeDrawers();
                } else if (id == R.id.nav_cam2) {
                    navController.navigate(R.id.nav_cam2);
                    DrawerLayout mDrawerLayout;
                    mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
                    mDrawerLayout.closeDrawers();
                } else if (id == R.id.nav_cam3) {
                    navController.navigate(R.id.nav_cam3);
                    DrawerLayout mDrawerLayout;
                    mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
                    mDrawerLayout.closeDrawers();
                } else if (id == R.id.nav_cam4) {
                    navController.navigate(R.id.nav_cam4);
                    DrawerLayout mDrawerLayout;
                    mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
                    mDrawerLayout.closeDrawers();
                } else if (id == R.id.nav_cam5) {
                    navController.navigate(R.id.nav_cam5);
                    DrawerLayout mDrawerLayout;
                    mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
                    mDrawerLayout.closeDrawers();
                }

                return true;
            }
        });

        //landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //no dim
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //fullscreen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_update:

                String urlString = "http://192.168.1.141/app-release.apk";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.android.chrome");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    // no chrome
                    intent.setPackage(null);
                    startActivity(intent);
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}
