package com.aep.vacationscheduler;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.aep.vacationscheduler.R;

/**
 * MainActivity serves as the entry point of the application, providing
 * navigation to the vacation list screen [Requirement C].
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the button to navigate to the Vacation List activity [Requirement C]
        Button btnGoToVacations = findViewById(R.id.btnGoToVacations);
        btnGoToVacations.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, VacationListActivity.class);
            startActivity(intent);
        });
    }
}