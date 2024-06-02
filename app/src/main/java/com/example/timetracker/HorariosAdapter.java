package com.example.timetracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HorariosAdapter extends RecyclerView.Adapter<HorariosAdapter.HorarioViewHolder> {

    private List<Horario> horarioList;

    public HorariosAdapter(List<Horario> horarioList) {
        this.horarioList = horarioList;
    }

    @NonNull
    @Override
    public HorarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_horario, parent, false);
        return new HorarioViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HorarioViewHolder holder, int position) {
        Horario horario = horarioList.get(position);
        holder.bind(horario);
    }

    @Override
    public int getItemCount() {
        return horarioList.size();
    }

    public static class HorarioViewHolder extends RecyclerView.ViewHolder {
        private TextView workerIdTextView;
        private TextView entradaTextView;
        private TextView salidaTextView;

        public HorarioViewHolder(@NonNull View itemView) {
            super(itemView);
            workerIdTextView = itemView.findViewById(R.id.workerIdTextView);
            entradaTextView = itemView.findViewById(R.id.entradaTextView);
            salidaTextView = itemView.findViewById(R.id.salidaTextView);
        }

        public void bind(Horario horario) {
            workerIdTextView.setText("Worker ID: " + horario.getWorkerId());
            entradaTextView.setText("Entrada: " + horario.getEntrada());
            salidaTextView.setText("Salida: " + horario.getSalida());
        }
    }
}
