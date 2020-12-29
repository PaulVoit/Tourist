package ru.jo4j.tourist.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import at.markushi.ui.CircleButton;
import ru.jo4j.tourist.R;
import ru.jo4j.tourist.database.SQLStore;
import ru.jo4j.tourist.model.Mark;

public class TouristAdapter extends RecyclerView.Adapter<TouristAdapter.TouristHolder> {

    private SQLStore store;
    private AppCompatActivity activity;

    public TouristAdapter(AppCompatActivity activity, SQLStore store) {
        this.activity = activity;
        this.store = store;
    }

    public class TouristHolder extends RecyclerView.ViewHolder {
        private View view;

        public TouristHolder(@NonNull View view) {
            super(view);
            this.view = itemView;
        }
    }

    @NonNull
    @Override
    public TouristHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_mark, parent, false);
        return new TouristHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TouristHolder holder, int i) {
        TextView title = holder.view.findViewById(R.id.item_mark_title);
        TextView latitude = holder.view.findViewById(R.id.item_mark_latitude);
        TextView longitude = holder.view.findViewById(R.id.item_mark_longitude);
        CircleButton mMap = holder.view.findViewById(R.id.item_mark_show);
        title.setText(store.getMarks().get(i).getTitle());
        latitude.setText(String.valueOf(store.getMarks().get(i).getLatitude()));
        longitude.setText(String.valueOf(store.getMarks().get(i).getLongitude()));
        mMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("mark", store.getMarks().get(i).getId());
                Objects.requireNonNull(activity).setResult(Activity.RESULT_OK, intent);
                activity.onBackPressed();
            }
        });
    }

    @Override
    public int getItemCount() {
        return store.getMarks().size();
    }

}
