package com.example.timetracker;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RegistroLaboralAdapter extends RecyclerView.Adapter<RegistroLaboralAdapter.ViewHolder> {
    private List<String> fichajeList;

    public RegistroLaboralAdapter(List<String> fichajeList) {
        this.fichajeList = fichajeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String totalHours = fichajeList.get(position);
        Log.d("Adapter", "Asignando valor al ViewHolder: " + totalHours);
        holder.textView.setText(totalHours);
    }


    @Override
    public int getItemCount() {
        return fichajeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}

