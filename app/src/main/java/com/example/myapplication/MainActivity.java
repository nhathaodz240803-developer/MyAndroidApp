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

        // Nút FAB chuyển sang tab xem ghi chú (SecondFragment)
        binding.fab.setOnClickListener(view ->
                navController.navigate(R.id.action_FirstFragment_to_SecondFragment)
        );
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
}