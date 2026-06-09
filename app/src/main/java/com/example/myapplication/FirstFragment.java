package com.example.myapplication;

import android.content.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class FirstFragment extends Fragment {

    private static final String PREF_NAME    = "MyAppPrefs";
    private static final String KEY_USERNAME = "username";

    private SharedPreferences prefs;
    private TextView  tvWelcome, tvServiceStatus;
    private EditText  etUsername, etTitle, etContent;

    // BroadcastReceiver nhận tín hiệu từ NoteService
    private final BroadcastReceiver backupReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (tvServiceStatus != null)
                tvServiceStatus.setText("✅ Backup complete!");
        }
    };

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ view
        tvWelcome       = view.findViewById(R.id.tvWelcome);
        tvServiceStatus = view.findViewById(R.id.tvServiceStatus);
        etUsername      = view.findViewById(R.id.etUsername);
        etTitle         = view.findViewById(R.id.etTitle);
        etContent       = view.findViewById(R.id.etContent);

        prefs = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Load tên đã lưu
        String savedName = prefs.getString(KEY_USERNAME, "");
        if (!savedName.isEmpty()) {
            tvWelcome.setText("Hello, " + savedName + "!");
            etUsername.setText(savedName);
        }

        // ===== Nút lưu tên - SharedPreferences =====
        view.findViewById(R.id.btnSaveName).setOnClickListener(v -> {
            String name = etUsername.getText().toString().trim();
            if (!name.isEmpty()) {
                prefs.edit().putString(KEY_USERNAME, name).apply();
                tvWelcome.setText("Hello, " + name + "!");
                Toast.makeText(requireContext(), "✅ Name saved!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Please enter your name!", Toast.LENGTH_SHORT).show();
            }
        });

        // ===== Nút thêm ghi chú - ContentProvider → Database =====
        view.findViewById(R.id.btnAddNote).setOnClickListener(v -> {
            String title   = etTitle.getText().toString().trim();
            String content = etContent.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter the title!", Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_TITLE,   title);
            values.put(DatabaseHelper.COLUMN_CONTENT, content);

            // Insert qua ContentProvider
            requireContext().getContentResolver().insert(NoteProvider.CONTENT_URI, values);

            etTitle.setText("");
            etContent.setText("");
            Toast.makeText(requireContext(), "✅ Notes have been added!", Toast.LENGTH_SHORT).show();
        });

        // ===== Nút start Service =====
        view.findViewById(R.id.btnStartService).setOnClickListener(v -> {
            tvServiceStatus.setText("⏳ Service is running...");
            Intent serviceIntent = new Intent(requireContext(), NoteService.class);
            serviceIntent.putExtra("action", "BACKUP");
            requireContext().startService(serviceIntent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Đăng ký nhận broadcast từ Service
        IntentFilter filter = new IntentFilter("com.example.myapplication.BACKUP_DONE");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requireContext().registerReceiver(backupReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            ContextCompat.registerReceiver(requireContext(), backupReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        requireContext().unregisterReceiver(backupReceiver);
    }
}