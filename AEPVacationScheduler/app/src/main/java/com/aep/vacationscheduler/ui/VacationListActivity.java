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

/**
 * VacationListActivity displays a list of all scheduled vacations [Requirement C].
 * It allows users to navigate to a detailed view of a vacation or add a new one [Requirement B1].
 */
public class VacationListActivity extends AppCompatActivity {
    private AppRepository repository;
    private RecyclerView recyclerView;
    private VacationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_list);

        // Initialize the repository to handle data operations [Requirement B1]
        repository = new AppRepository(getApplication());

        // Setup RecyclerView with a LinearLayoutManager [Requirement C]
        recyclerView = findViewById(R.id.vacationRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up the FloatingActionButton to navigate to the Vacation Details activity for adding a new vacation [Requirement B1]
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
        // Refresh the list whenever the activity returns to the foreground
        refreshList();
    }

    /**
     * Fetches the current list of vacations from the repository and updates the RecyclerView adapter [Requirement C].
     */
    private void refreshList() {
        List<Vacation> vacations = repository.getAllVacations();
        adapter = new VacationAdapter(vacations, vacation -> {
            // Navigate to details view when a vacation item is clicked [Requirement B3a, C]
            Intent intent = new Intent(this, VacationDetailsActivity.class);
            intent.putExtra("vacationId", vacation.id);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }
}