package com.aep.vacationscheduler.ui;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.aep.vacationscheduler.R;
import com.aep.vacationscheduler.data.Vacation;
import java.util.List;

public class VacationAdapter extends RecyclerView.Adapter<VacationAdapter.VacationViewHolder> {
    private final List<Vacation> vacations;
    private final VacationClickListener listener;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vacation, parent, false);
        return new VacationViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull VacationViewHolder holder, int position) {
        Vacation vacation = vacations.get(position);
        holder.tvTitle.setText(vacation.title);
        holder.tvDates.setText(vacation.startDate + " - " + vacation.endDate);

        holder.itemView.setOnClickListener(v -> listener.onVacationClick(vacation));
    }

    @Override
    public int getItemCount() {
        return vacations.size();
    }

    static class VacationViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDates;
        VacationViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvVacationTitle);
            tvDates = itemView.findViewById(R.id.tvVacationDates);
        }
    }
}