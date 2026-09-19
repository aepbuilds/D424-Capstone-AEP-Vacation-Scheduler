package com.aep.vacationscheduler.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aep.vacationscheduler.R;
import com.aep.vacationscheduler.data.AppRepository;
import com.aep.vacationscheduler.data.Excursion;
import com.aep.vacationscheduler.data.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;
import java.util.concurrent.Executors;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import java.util.ArrayList;

public class ExcursionListActivity extends AppCompatActivity {
    private AppRepository repository;
    private RecyclerView recyclerView;
    private ExcursionAdapter adapter;
    private int vacationId;

    private List<Excursion> allExcursions = new ArrayList<>();
    private EditText etSearch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_list);

        repository = new AppRepository(getApplication());
        vacationId = getIntent().getIntExtra("vacationId", -1);

        recyclerView = findViewById(R.id.excursionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        etSearch = findViewById(R.id.etSearchExcursions);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterExcursions(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        FloatingActionButton fab = findViewById(R.id.fabAddExcursion);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, ExcursionDetailsActivity.class);
            intent.putExtra("vacationId", vacationId);
            startActivity(intent);
        });

        refreshList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Excursion> excursions = repository.getExcursionsForVacation(vacationId);
            runOnUiThread(() -> {
                allExcursions = excursions;
                filterExcursions(etSearch.getText().toString());
            });
        });
    }

    private void filterExcursions(String query) {
        List<Excursion> filtered = new ArrayList<>();
        String lower = query.toLowerCase().trim();
        for (Excursion e : allExcursions) {
            if (e.title.toLowerCase().contains(lower)) {
                filtered.add(e);
            }
        }
        ExcursionAdapter adapter = new ExcursionAdapter(filtered, excursion -> {
            Intent intent = new Intent(this, ExcursionDetailsActivity.class);
            intent.putExtra("vacationId", vacationId);
            intent.putExtra("excursionId", excursion.id);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }
}