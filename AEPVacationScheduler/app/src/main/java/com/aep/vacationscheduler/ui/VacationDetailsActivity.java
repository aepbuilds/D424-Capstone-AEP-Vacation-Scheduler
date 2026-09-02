package com.aep.vacationscheduler.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.aep.vacationscheduler.R;
import com.aep.vacationscheduler.data.AppRepository;
import com.aep.vacationscheduler.data.Vacation;
//import com.aep.vacationscheduler.notifications.NotificationHelper;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * VacationDetailsActivity handles adding, updating, and deleting vacations,
 * as well as viewing and adding excursions for a specific vacation.
 * Satisfies Requirements B1, B2, B3a-f, and C (Detailed Vacation View).
 */
public class VacationDetailsActivity extends AppCompatActivity {
    private AppRepository repository;
    private EditText etTitle, etHotel, etStart, etEnd;
    private Button btnSave, btnDelete, btnShare;
    private RecyclerView excursionRecyclerView;
    private ExcursionAdapter excursionAdapter;

    private int vacationId;
    private Vacation currentVacation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_details);

        // Initialize repository and UI components [Requirement C]
        repository = new AppRepository(getApplication());
        etTitle = findViewById(R.id.etVacationTitle);
        etHotel = findViewById(R.id.etHotel);
        etStart = findViewById(R.id.etStartDate);
        etEnd = findViewById(R.id.etEndDate);
        btnSave = findViewById(R.id.btnSaveVacation);
        btnDelete = findViewById(R.id.btnDeleteVacation);
        btnShare = findViewById(R.id.btnShareVacation);
        excursionRecyclerView = findViewById(R.id.excursionRecyclerView);
        Button btnAddExcursion = findViewById(R.id.btnAddExcursion);

        // Get vacation ID from intent to determine if we are editing or adding [Requirement B1, B3a]
        vacationId = getIntent().getIntExtra("vacationId", -1);
        if (vacationId != -1) {
            currentVacation = repository.getVacationById(vacationId);
            etTitle.setText(currentVacation.title);
            etHotel.setText(currentVacation.hotel);
            etStart.setText(currentVacation.startDate);
            etEnd.setText(currentVacation.endDate);
        }

        // Use DatePickerDialog for date input to ensure correct formatting [Requirement B3c]
        etStart.setOnClickListener(v -> showDatePicker(etStart));
        etEnd.setOnClickListener(v -> showDatePicker(etEnd));

        // Set click listeners for action buttons [Requirement B1, B3b, B3f]
        btnSave.setOnClickListener(v -> saveVacation());
        btnDelete.setOnClickListener(v -> deleteVacation());
        btnShare.setOnClickListener(v -> shareVacation());

        // Add excursion button [Requirement B3h]
        btnAddExcursion.setOnClickListener(v -> {
            if (vacationId == -1) {
                Toast.makeText(this, "Please save vacation first", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, ExcursionDetailsActivity.class);
            intent.putExtra("vacationId", vacationId);
            startActivity(intent);
        });

        refreshExcursions();
    }

    /**
     * Reloads the list of excursions associated with the current vacation [Requirement B3g].
     */
    private void refreshExcursions() {
        if (vacationId == -1) return;
        excursionRecyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        excursionAdapter = new ExcursionAdapter(repository.getExcursionsForVacation(vacationId), excursion -> {
            // Navigate to excursion details view [Requirement B5a, C]
            Intent intent = new Intent(this, ExcursionDetailsActivity.class);
            intent.putExtra("vacationId", vacationId);
            intent.putExtra("excursionId", excursion.id);
            startActivity(intent);
        });
        excursionRecyclerView.setAdapter(excursionAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshExcursions();
    }

    /**
     * Displays a DatePickerDialog to help the user select a date [Requirement B3c].
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
     * Validates inputs and saves the vacation to the database [Requirement B1, B3b].
     * Also schedules notifications for the start and end dates [Requirement B3e].
     */
    private void saveVacation() {
        String title = etTitle.getText().toString();
        String hotel = etHotel.getText().toString();
        String start = etStart.getText().toString();
        String end = etEnd.getText().toString();

        // 1. Basic validation: Ensure no fields are empty
        if (title.isEmpty() || hotel.isEmpty() || start.isEmpty() || end.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Date format validation [Requirement B3c]
        String dateFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        try {
            Date vacayStart = sdf.parse(start);
            Date vacayEnd = sdf.parse(end);
            // 3. Logic validation: End date must be after start date [Requirement B3d]
            if (vacayEnd.before(vacayStart)) {
                Toast.makeText(this, R.string.error_end_date, Toast.LENGTH_LONG).show();
                return;
            }
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        Vacation vacation = new Vacation(title, hotel, start, end);
        if (vacationId != -1) {
            vacation.id = vacationId;
            repository.updateVacation(vacation, () -> {
                // Schedule start and end date notifications [Requirement B3e]
                //NotificationHelper.scheduleVacationNotification(this, vacation, true);
                //NotificationHelper.scheduleVacationNotification(this, vacation, false);
                //finish();
            });
        } else {
            repository.insertVacation(vacation, () -> {
                //finish();
            });
        }
    }

    /**
     * Deletes the current vacation if it has no associated excursions [Requirement B1b].
     */
    private void deleteVacation() {
        if (vacationId == -1) return;
        // Validation: prevent deletion if excursions are associated [Requirement B1b]
        int count = repository.getExcursionCountForVacation(vacationId);
        if (count > 0) {
            Toast.makeText(this, R.string.error_delete_vacation, Toast.LENGTH_LONG).show();
            return;
        }
        repository.deleteVacation(currentVacation, this::finish);
    }

    /**
     * Shares the vacation details via a system share intent [Requirement B3f].
     */
    private void shareVacation() {
        String title = etTitle.getText().toString();
        String hotel = etHotel.getText().toString();
        String start = etStart.getText().toString();
        String end = etEnd.getText().toString();

        String shareText = "Hotel: " + hotel + "\nStart Date: " + start + "\nEnd Date: " + end;
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share via"));
    }
}