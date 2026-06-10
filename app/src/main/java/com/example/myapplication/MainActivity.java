package com.example.myapplication;                  // namespace cua app

import android.os.Bundle;                           // container luu du lieu
import androidx.appcompat.app.AppCompatActivity;    // class cha cho activity hien dai
import androidx.navigation.NavController;           // chuyen man hinh fragment
import androidx.navigation.Navigation;              // helper class de lay NavController
import androidx.navigation.ui.AppBarConfiguration;  // cau hinh: toolbar, back button, navigation
import androidx.navigation.ui.NavigationUI;         // ket noi toolbar voi navigation
import com.example.myapplication.databinding.ActivityMainBinding;   // view binding: android tu generate class tu XML layout

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // ===== FAB điều hướng 2 chiều =====
        binding.fab.setOnClickListener(view -> {
            if (navController.getCurrentDestination() == null) return;
            int currentId = navController.getCurrentDestination().getId();

            if (currentId == R.id.FirstFragment) {
                navController.navigate(R.id.action_FirstFragment_to_SecondFragment);
            } else if (currentId == R.id.SecondFragment) {
                navController.navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });

        // ===== Đổi icon FAB theo fragment =====
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.SecondFragment) {
                // Đang ở danh sách → icon để quay về trang thêm note
                binding.fab.setImageResource(android.R.drawable.ic_menu_edit);
            } else {
                // Đang ở trang thêm note → icon để xem danh sách
                binding.fab.setImageResource(android.R.drawable.ic_menu_agenda);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
}