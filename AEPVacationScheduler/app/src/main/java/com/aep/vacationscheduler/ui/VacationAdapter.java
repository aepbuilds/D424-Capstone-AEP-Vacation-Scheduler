package com.aep.vacationscheduler.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.aep.vacationscheduler.R;
import com.aep.vacationscheduler.data.Vacation;
import java.util.List;

/**
 * VacationAdapter is a RecyclerView adapter that manages the display of a list of vacations [Requirement C].
 */
public class VacationAdapter extends RecyclerView.Adapter<VacationAdapter.VacationViewHolder> {
    private final List<Vacation> vacations;
    private final VacationClickListener listener;

    /**
     * Interface to handle click events on vacation items.
     */
    public interface VacationClickListener {
        void onVacationClick(Vacation vacation);
    }

    public VacationAdapter(List<Vacation> vacations, VacationClickListener listener) {
        this.vacations = vacations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VacationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for a single vacation [Requirement C]
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vacation, parent, false);
        return new VacationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VacationViewHolder holder, int position) {
        Vacation vacation = vacations.get(position);
        // Bind vacation data to the TextViews [Requirement B2]
        holder.tvTitle.setText(vacation.title);
        holder.tvDates.setText(vacation.startDate + " - " + vacation.endDate);

        // Set a click listener to navigate to details view [Requirement B3a, C]
        holder.itemView.setOnClickListener(v -> listener.onVacationClick(vacation));
    }

    @Override
    public int getItemCount() {
        return vacations.size();
    }

    /**
     * ViewHolder class to hold references to the UI elements of each vacation item.
     */
    static class VacationViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDates;
        VacationViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvVacationTitle);
            tvDates = itemView.findViewById(R.id.tvVacationDates);
        }
    }
}