package com.example.timetracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class AnnotationsAdapter extends ArrayAdapter<Anotacion> {

    private Context mContext;
    private int mResource;
    private List<Anotacion> mAnotaciones;
    private OnDeleteClickListener mListener;

    public AnnotationsAdapter(Context context, int resource, List<Anotacion> anotaciones, OnDeleteClickListener listener) {
        super(context, resource, anotaciones);
        this.mContext = context;
        this.mResource = resource;
        this.mAnotaciones = anotaciones;
        this.mListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Obtener la anotación actual
        Anotacion anotacion = getItem(position);

        // Crear la vista si es necesario
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mResource, parent, false);
        }

        // Obtener las vistas del layout
        TextView tvTexto = convertView.findViewById(R.id.tvTexto);
        TextView tvCreatedBy = convertView.findViewById(R.id.tvCreatedBy);
        TextView tvFechaHora = convertView.findViewById(R.id.tvFechaHora);
        Button btnDelete = convertView.findViewById(R.id.btnDelete);

        // Configurar las vistas con los datos de la anotación
        tvTexto.setText(anotacion.getTexto());
        tvCreatedBy.setText(anotacion.getCreatedBy());
        tvFechaHora.setText(anotacion.getFechaHora().toString());

        // Configurar el botón de eliminar
        btnDelete.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onDeleteClick(anotacion);
            }
        });

        return convertView;
    }

    // Interfaz para manejar el click en el botón de eliminar
    public interface OnDeleteClickListener {
        void onDeleteClick(Anotacion anotacion);
    }
}
