package com.aep.vacationscheduler.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aep.vacationscheduler.R;
import com.aep.vacationscheduler.data.AppRepository;
import com.aep.vacationscheduler.data.Vacation;
import com.aep.vacationscheduler.util.SearchFilter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;
import java.util.concurrent.Executors;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import java.util.ArrayList;

public class VacationListActivity extends AppCompatActivity {
    private AppRepository repository;
    private RecyclerView recyclerView;

    private List<Vacation> allVacations = new ArrayList<>();
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_list);

        repository = new AppRepository(getApplication());

        recyclerView = findViewById(R.id.vacationRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        etSearch = findViewById(R.id.etSearchVacations);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterVacations(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        FloatingActionButton fab = findViewById(R.id.fabAddVacation);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, VacationDetailsActivity.class);
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
            List<Vacation> vacations = repository.getAllVacations();
            runOnUiThread(() -> {
                allVacations = vacations;
                filterVacations(etSearch.getText().toString());
            });
        });
    }

    private void filterVacations(String query) {
        List<Vacation> filtered = SearchFilter.filterVacationsByTitle(allVacations, query);
        VacationAdapter adapter = new VacationAdapter(filtered, vacation -> {
            Intent intent = new Intent(this, VacationDetailsActivity.class);
            intent.putExtra("vacationId", vacation.id);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }
}