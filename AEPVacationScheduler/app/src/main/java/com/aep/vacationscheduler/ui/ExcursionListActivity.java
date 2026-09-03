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

/**
 * ExcursionListActivity displays a list of excursions associated with a specific vacation.
 * It allows users to add new excursions or view details of existing ones.
 */
public class ExcursionListActivity extends AppCompatActivity {
    private AppRepository repository;
    private RecyclerView recyclerView;
    private ExcursionAdapter adapter;
    private int vacationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_list);

        // Initialize repository and extract the vacation ID from the intent
        repository = new AppRepository(getApplication());
        vacationId = getIntent().getIntExtra("vacationId", -1);

        // Setup RecyclerView with a LinearLayoutManager
        recyclerView = findViewById(R.id.excursionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up the FloatingActionButton to navigate to the Excursion Details activity
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
        // Refresh the excursion list whenever the activity returns to the foreground
        refreshList();
    }

    /**
     * Fetches the list of excursions for the current vacation from the repository and updates the adapter.
     */
    private void refreshList() {
        List<Excursion> excursions = repository.getExcursionsForVacation(vacationId);
        adapter = new ExcursionAdapter(excursions, excursion -> {
            // Navigate to excursion details view when an item is clicked
            Intent intent = new Intent(this, ExcursionDetailsActivity.class);
            intent.putExtra("vacationId", vacationId);
            intent.putExtra("excursionId", excursion.id);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }
}