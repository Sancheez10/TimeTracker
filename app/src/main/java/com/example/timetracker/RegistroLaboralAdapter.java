package com.example.timetracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RegistroLaboralAdapter extends RecyclerView.Adapter<RegistroLaboralAdapter.ViewHolder> {

    private List<Fichaje> fichajeList;

    public RegistroLaboralAdapter(List<Fichaje> fichajeList) {
        this.fichajeList = fichajeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fichaje, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Fichaje fichaje = fichajeList.get(position);
        holder.tvDate.setText("Fecha: " + fichaje.getDate());
        holder.tvLocation.setText("Ubicación: " + fichaje.getLocation());
        holder.tvCheckIn.setText("Entrada: " + fichaje.getCheckInTime());
        holder.tvCheckOut.setText("Salida: " + fichaje.getCheckOutTime());
        if (fichaje.getUserId() != null) {
            holder.tvUserId.setText("ID Usuario: " + fichaje.getUserId());
            holder.tvUserId.setVisibility(View.VISIBLE);
        } else {
            holder.tvUserId.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return fichajeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDate, tvLocation, tvCheckIn, tvCheckOut, tvUserId;

        public ViewHolder(View view) {
            super(view);
            tvDate = view.findViewById(R.id.tvDate);
            tvLocation = view.findViewById(R.id.tvLocation);
            tvCheckIn = view.findViewById(R.id.tvCheckIn);
            tvCheckOut = view.findViewById(R.id.tvCheckOut);
            tvUserId = view.findViewById(R.id.tvUserId);
        }
    }
}
