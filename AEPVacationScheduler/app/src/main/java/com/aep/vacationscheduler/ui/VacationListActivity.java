package com.aep.vacationscheduler.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aep.vacationscheduler.R;
import com.aep.vacationscheduler.data.AppRepository;
import com.aep.vacationscheduler.data.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;
import java.util.concurrent.Executors;

public class VacationListActivity extends AppCompatActivity {
    private AppRepository repository;
    private RecyclerView recyclerView;
    private VacationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_list);

        repository = new AppRepository(getApplication());

        recyclerView = findViewById(R.id.vacationRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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
                adapter = new VacationAdapter(vacations, vacation -> {
                    Intent intent = new Intent(this, VacationDetailsActivity.class);
                    intent.putExtra("vacationId", vacation.id);
                    startActivity(intent);
                });
                recyclerView.setAdapter(adapter);
            });
        });
    }
}