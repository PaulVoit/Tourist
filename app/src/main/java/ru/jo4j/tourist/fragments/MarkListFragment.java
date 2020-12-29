package ru.jo4j.tourist.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.jo4j.tourist.R;
import ru.jo4j.tourist.SpaceItemDecoration;
import ru.jo4j.tourist.adapters.TouristAdapter;
import ru.jo4j.tourist.database.SQLStore;
import ru.jo4j.tourist.database.TouristBaseHelper;
import ru.jo4j.tourist.database.TouristDbSchema;
import ru.jo4j.tourist.model.Mark;

public class MarkListFragment extends Fragment {

    private RecyclerView recycler;
    private TouristAdapter adapter;
    private SQLStore store;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mark_recycler_view, container, false);
        this.recycler = view.findViewById(R.id.marks);
        this.recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.store = new SQLStore(getContext());
        updateUI();
        return view;
    }

    private void updateUI() {
        if (adapter == null) {
            adapter = new TouristAdapter((AppCompatActivity) getActivity(), store);
            recycler.setAdapter(adapter);
            recycler.addItemDecoration(new SpaceItemDecoration(8));
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }
}
