package com.example.myapplication;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.*;

public class SecondFragment extends Fragment {

    private ListView     lvNotes;
    private TextView     tvEmpty;
    private ArrayAdapter<String> adapter;
    private final List<String> notesList = new ArrayList<>();
    private final List<Long>   noteIds   = new ArrayList<>();

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lvNotes = view.findViewById(R.id.lvNotes);
        tvEmpty = view.findViewById(R.id.tvEmpty);

        adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, notesList);
        lvNotes.setAdapter(adapter);

        // Xóa ghi chú khi nhấn giữ
        lvNotes.setOnItemLongClickListener((parent, v, pos, id) -> {
            long noteId = noteIds.get(pos);
            Uri deleteUri = Uri.parse(NoteProvider.CONTENT_URI + "/" + noteId);
            // Xóa qua ContentProvider
            requireContext().getContentResolver().delete(deleteUri, null, null);
            Toast.makeText(requireContext(), "🗑 Deleted!", Toast.LENGTH_SHORT).show();
            loadNotes(); // Refresh
            return true;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNotes(); // Load mỗi khi quay lại tab này
    }

    private void loadNotes() {
        notesList.clear();
        noteIds.clear();

        // Query qua ContentProvider
        Cursor cursor = requireContext().getContentResolver()
                .query(NoteProvider.CONTENT_URI, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                long   id      = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                String title   = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CONTENT));
                noteIds.add(id);
                notesList.add("📌 " + title + "\n    " + content);
            }
            cursor.close();
        }

        adapter.notifyDataSetChanged();

        // Hiện/ẩn thông báo trống
        tvEmpty.setVisibility(notesList.isEmpty() ? View.VISIBLE : View.GONE);
        lvNotes.setVisibility(notesList.isEmpty() ? View.GONE : View.VISIBLE);
    }
}