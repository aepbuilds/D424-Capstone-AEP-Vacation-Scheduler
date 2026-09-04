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
import com.aep.vacationscheduler.data.Excursion;
import com.aep.vacationscheduler.data.Vacation;
import com.aep.vacationscheduler.notifications.NotificationHelper;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class VacationDetailsActivity extends AppCompatActivity {
    private AppRepository repository;
    private EditText etTitle, etHotel, etStart, etEnd;
    private RecyclerView excursionRecyclerView;
    private ExcursionAdapter excursionAdapter;

    private int vacationId;
    private Vacation currentVacation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_details);

        repository = new AppRepository(getApplication());
        etTitle = findViewById(R.id.etVacationTitle);
        etHotel = findViewById(R.id.etHotel);
        etStart = findViewById(R.id.etStartDate);
        etEnd = findViewById(R.id.etEndDate);
        Button btnSave = findViewById(R.id.btnSaveVacation);
        Button btnDelete = findViewById(R.id.btnDeleteVacation);
        Button btnShare = findViewById(R.id.btnShareVacation);
        excursionRecyclerView = findViewById(R.id.excursionRecyclerView);
        Button btnAddExcursion = findViewById(R.id.btnAddExcursion);

        vacationId = getIntent().getIntExtra("vacationId", -1);
        if (vacationId != -1) {
            Executors.newSingleThreadExecutor().execute(() -> {
                currentVacation = repository.getVacationById(vacationId);
                runOnUiThread(() -> {
                    etTitle.setText(currentVacation.title);
                    etHotel.setText(currentVacation.hotel);
                    etStart.setText(currentVacation.startDate);
                    etEnd.setText(currentVacation.endDate);
                });
            });
        }

        etStart.setOnClickListener(v -> showDatePicker(etStart));
        etEnd.setOnClickListener(v -> showDatePicker(etEnd));

        btnSave.setOnClickListener(v -> saveVacation());
        btnDelete.setOnClickListener(v -> deleteVacation());
        btnShare.setOnClickListener(v -> shareVacation());

        btnAddExcursion.setOnClickListener(v -> {
            if (vacationId == -1) {
                Toast.makeText(this, "Please save Vacation first", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, ExcursionDetailsActivity.class);
            intent.putExtra("vacationId", vacationId);
            startActivity(intent);
        });

        refreshExcursions();
    }

    private void refreshExcursions() {
        if (vacationId == -1) return;
        excursionRecyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Excursion> excursions = repository.getExcursionsForVacation(vacationId);
            runOnUiThread(() -> {
                excursionAdapter = new ExcursionAdapter(excursions, excursion -> {
                    Intent intent = new Intent(this, ExcursionDetailsActivity.class);
                    intent.putExtra("vacationId", vacationId);
                    intent.putExtra("excursionId", excursion.id);
                    startActivity(intent);
                });
                excursionRecyclerView.setAdapter(excursionAdapter);
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshExcursions();
    }

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

    private void saveVacation() {
        String title = etTitle.getText().toString();
        String hotel = etHotel.getText().toString();
        String start = etStart.getText().toString();
        String end = etEnd.getText().toString();

        if (title.isEmpty() || hotel.isEmpty() || start.isEmpty() || end.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String dateFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        try {
            Date vacayStart = sdf.parse(start);
            Date vacayEnd = sdf.parse(end);
            assert vacayEnd != null;
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
                NotificationHelper.scheduleVacationNotification(this, vacation, true);
                NotificationHelper.scheduleVacationNotification(this, vacation, false);
                finish();
            });
        } else {
            repository.insertVacation(vacation, this::finish);
        }
    }

    private void deleteVacation() {
        if (vacationId == -1) return;
        Executors.newSingleThreadExecutor().execute(() -> {
            int count = repository.getExcursionCountForVacation(vacationId);
            if (count > 0) {
                runOnUiThread(() -> Toast.makeText(this, R.string.error_delete_vacation, Toast.LENGTH_LONG).show());
                return;
            }
            repository.deleteVacation(currentVacation, this::finish);
        });
    }

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