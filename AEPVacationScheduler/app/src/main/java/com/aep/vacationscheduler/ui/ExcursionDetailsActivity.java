package com.aep.vacationscheduler.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.aep.vacationscheduler.R;
import com.aep.vacationscheduler.data.AppRepository;
import com.aep.vacationscheduler.data.Excursion;
import com.aep.vacationscheduler.data.Vacation;
//import com.aep.vacationscheduler.notifications.NotificationHelper;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * ExcursionDetailsActivity handles adding, updating, and deleting excursions.
 * Satisfies Requirements B4, B5a-e, and C (Detailed Excursion View).
 */
public class ExcursionDetailsActivity extends AppCompatActivity {
    private AppRepository repository;
    private EditText etTitle, etDate;
    private Button btnSave, btnDelete;
    private Excursion currentExcursion;
    private int excursionId = -1;
    private int vacationId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_details);

        // Initialize repository and UI components [Requirement C]
        repository = new AppRepository(getApplication());
        etTitle = findViewById(R.id.etExcursionTitle);
        etDate = findViewById(R.id.etExcursionDate);
        btnSave = findViewById(R.id.btnSaveExcursion);
        btnDelete = findViewById(R.id.btnDeleteExcursion);

        // Get IDs from intent to determine if we are editing or adding [Requirement B5b]
        vacationId = getIntent().getIntExtra("vacationId", -1);
        excursionId = getIntent().getIntExtra("excursionId", -1);

        if (excursionId != -1) {
            currentExcursion = repository.getExcursionById(excursionId);
            etTitle.setText(currentExcursion.title);
            etDate.setText(currentExcursion.date);
        }

        // Use DatePickerDialog for consistent date input formatting [Requirement B5c]
        etDate.setOnClickListener(v -> showDatePicker(etDate));

        // Set click listeners for action buttons [Requirement B5b]
        btnSave.setOnClickListener(v -> saveExcursion());
        btnDelete.setOnClickListener(v -> deleteExcursion());
    }

    /**
     * Displays a DatePickerDialog to help the user select a date [Requirement B5c].
     */
    private void showDatePicker(EditText editText) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
            Calendar calendar = Calendar.getInstance();
            calendar.set(year1, month1, dayOfMonth);
            editText.setText(sdf.format(calendar.getTime()));
        }, year, month, day);
        datePickerDialog.show();
    }

    /**
     * Validates inputs and saves the excursion to the database [Requirement B5b].
     * Also schedules a notification for the excursion date [Requirement B5d].
     */
    private void saveExcursion() {
        String title = etTitle.getText().toString();
        String date = etDate.getText().toString();

        // 1. Basic validation: Ensure no fields are empty
        if (title.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Date format and logic validation [Requirement B5c]
        String dateFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        try {
            Date excursionDate = sdf.parse(date);
            Vacation vacation = repository.getVacationById(vacationId);
            Date vacStart = sdf.parse(vacation.startDate);
            Date vacEnd = sdf.parse(vacation.endDate);

            // Validation: Excursion date must be between vacation start and end dates [Requirement B5e]
            if (excursionDate.before(vacStart) || excursionDate.after(vacEnd)) {
                Toast.makeText(this, R.string.error_excursion_date, Toast.LENGTH_LONG).show();
                return;
            }
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        Excursion excursion = new Excursion(vacationId, title, date);
        if (excursionId != -1) {
            excursion.id = excursionId;
            repository.updateExcursion(excursion, () -> {
                // Schedule notification for the excursion [Requirement B5d]
                //NotificationHelper.scheduleExcursionNotification(this, excursion);
                //finish();
            });
        } else {
            repository.insertExcursion(excursion, () -> {
                // Schedule notification for the excursion [Requirement B5d]
                //NotificationHelper.scheduleExcursionNotification(this, excursion);
                //finish();
            });
        }
    }

    /**
     * Deletes the current excursion from the database [Requirement B5b].
     */
    private void deleteExcursion() {
        if (currentExcursion == null) return;
        repository.deleteExcursion(currentExcursion, this::finish);
    }
}