package com.aep.vacationscheduler.ui;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.aep.vacationscheduler.R;
import com.aep.vacationscheduler.data.AppRepository;
import com.aep.vacationscheduler.data.Vacation;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class ReportActivity extends AppCompatActivity {
    private AppRepository repository;
    private TableLayout table;

    private static class ReportRow {
        String title, hotel, startDate, endDate;
        int excursionCount;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        repository = new AppRepository(getApplication());
        table = findViewById(R.id.reportTable);

        TextView tvTimestamp = findViewById(R.id.tvReportTimestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);
        tvTimestamp.setText("Generated: " + sdf.format(new Date()));

        loadReport();
    }

    private void loadReport() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Vacation> vacations = repository.getAllVacations();
            List<ReportRow> rows = new ArrayList<>();
            for (Vacation v : vacations) {
                ReportRow row = new ReportRow();
                row.title = v.title;
                row.hotel = v.hotel;
                row.startDate = v.startDate;
                row.endDate = v.endDate;
                row.excursionCount = repository.getExcursionCountForVacation(v.id);
                rows.add(row);
            }
            runOnUiThread(() -> populateTable(rows));
        });
    }

    private void populateTable(List<ReportRow> rows) {
        table.removeAllViews();
        table.addView(buildRow(new String[]{"Title", "Hotel", "Start Date", "End Date", "# Excursions"}, true));

        for (ReportRow r : rows) {
            table.addView(buildRow(new String[]{
                    r.title, r.hotel, r.startDate, r.endDate, String.valueOf(r.excursionCount)
            }, false));
        }
    }

    private TableRow buildRow(String[] values, boolean isHeader) {
        TableRow row = new TableRow(this);
        for (String value : values) {
            TextView tv = new TextView(this);
            tv.setText(value);
            tv.setPadding(16, 12, 16, 12);
            tv.setGravity(Gravity.START);
            if (isHeader) {
                tv.setTypeface(null, android.graphics.Typeface.BOLD);
            }
            row.addView(tv);
        }
        return row;
    }
}