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
import com.aep.vacationscheduler.notifications.NotificationHelper;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class ExcursionDetailsActivity extends AppCompatActivity {
    private AppRepository repository;
    private EditText etTitle, etDate;
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
        Button btnSave = findViewById(R.id.btnSaveExcursion);
        Button btnDelete = findViewById(R.id.btnDeleteExcursion);

        // Get IDs from intent to determine if we are editing or adding [Requirement B5b]
        vacationId = getIntent().getIntExtra("vacationId", -1);
        excursionId = getIntent().getIntExtra("excursionId", -1);

        if (excursionId != -1) {
            Executors.newSingleThreadExecutor().execute(() -> {
                currentExcursion = repository.getExcursionById(excursionId);
                runOnUiThread(() -> {
                    etTitle.setText(currentExcursion.title);
                    etDate.setText(currentExcursion.date);
                });
            });
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
    private void saveExcursion() {
        String title = etTitle.getText().toString();
        String date = etDate.getText().toString();

        if (title.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String dateFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        Date excursionDate;
        try {
            excursionDate = sdf.parse(date);
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        Date finalExcursionDate = excursionDate;
        Executors.newSingleThreadExecutor().execute(() -> {
            Vacation vacation = repository.getVacationById(vacationId);
            try {
                Date vacStart = sdf.parse(vacation.startDate);
                Date vacEnd = sdf.parse(vacation.endDate);

                assert finalExcursionDate != null;
                if (finalExcursionDate.before(vacStart) || finalExcursionDate.after(vacEnd)) {
                    runOnUiThread(() -> Toast.makeText(this, R.string.error_excursion_date, Toast.LENGTH_LONG).show());
                    return;
                }
            } catch (ParseException e) {
                runOnUiThread(() -> Toast.makeText(this, "Invalid vacation date format", Toast.LENGTH_SHORT).show());
                return;
            }

            Excursion excursion = new Excursion(vacationId, title, date);
            if (excursionId != -1) {
                excursion.id = excursionId;
                repository.updateExcursion(excursion, () -> {
                    NotificationHelper.scheduleExcursionNotification(this, excursion);
                    finish();
                });
            } else {
                repository.insertExcursion(excursion, () -> {
                    NotificationHelper.scheduleExcursionNotification(this, excursion);
                    finish();
                });
            }
        });
    }

    /**
     * Deletes the current excursion from the database [Requirement B5b].
     */
    private void deleteExcursion() {
        if (currentExcursion == null) return;
        repository.deleteExcursion(currentExcursion, this::finish);
    }
}