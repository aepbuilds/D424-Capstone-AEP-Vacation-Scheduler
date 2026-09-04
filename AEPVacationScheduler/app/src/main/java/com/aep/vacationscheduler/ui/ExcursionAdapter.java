package com.aep.vacationscheduler.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.aep.vacationscheduler.R;
import com.aep.vacationscheduler.data.Excursion;
import java.util.List;

public class ExcursionAdapter extends RecyclerView.Adapter<ExcursionAdapter.ExcursionViewHolder> {
    private final List<Excursion> excursions;
    private final ExcursionClickListener listener;

    public interface ExcursionClickListener {
        void onExcursionClick(Excursion excursion);
    }

    public ExcursionAdapter(List<Excursion> excursions, ExcursionClickListener listener) {
        this.excursions = excursions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExcursionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_excursion, parent, false);
        return new ExcursionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExcursionViewHolder holder, int position) {
        Excursion excursion = excursions.get(position);
        holder.tvTitle.setText(excursion.title);
        holder.tvDate.setText(excursion.date);

        holder.itemView.setOnClickListener(v -> listener.onExcursionClick(excursion));
    }

    @Override
    public int getItemCount() {
        return excursions.size();
    }

    static class ExcursionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate;
        ExcursionViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvExcursionTitle);
            tvDate = itemView.findViewById(R.id.tvExcursionDate);
        }
    }
}