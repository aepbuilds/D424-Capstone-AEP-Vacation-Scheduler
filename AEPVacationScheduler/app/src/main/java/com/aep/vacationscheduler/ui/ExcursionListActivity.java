package com.aep.vacationscheduler.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aep.vacationscheduler.R;
import com.aep.vacationscheduler.data.AppRepository;
import com.aep.vacationscheduler.data.Excursion;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;
import java.util.concurrent.Executors;

public class ExcursionListActivity extends AppCompatActivity {
    private AppRepository repository;
    private RecyclerView recyclerView;
    private ExcursionAdapter adapter;
    private int vacationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_list);

        repository = new AppRepository(getApplication());
        vacationId = getIntent().getIntExtra("vacationId", -1);

        recyclerView = findViewById(R.id.excursionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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
                adapter = new ExcursionAdapter(excursions, excursion -> {
                    Intent intent = new Intent(this, ExcursionDetailsActivity.class);
                    intent.putExtra("vacationId", vacationId);
                    intent.putExtra("excursionId", excursion.id);
                    startActivity(intent);
                });
                recyclerView.setAdapter(adapter);
            });
        });
    }
}