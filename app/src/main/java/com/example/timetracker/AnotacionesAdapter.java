package com.example.timetracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AnotacionesAdapter extends ArrayAdapter<Anotacion> {

    private Context context;
    private List<Anotacion> anotaciones;
    private String currentUserEmail; // Email del usuario actual, obtenido de SharedPreferences

    public AnotacionesAdapter(Context context, List<Anotacion> anotaciones) {
        super(context, R.layout.item_anotacion, anotaciones);
        this.context = context;
        this.anotaciones = anotaciones;

        // Obtener el correo del usuario desde SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("workers_pref", Context.MODE_PRIVATE);
        this.currentUserEmail = sharedPreferences.getString("email", "Usuario"); // Asignar el email del usuario
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_anotacion, parent, false);
        }

        Anotacion anotacion = anotaciones.get(position);

        TextView tvTexto = convertView.findViewById(R.id.tvTextoAnotacion);
        TextView tvFechaHora = convertView.findViewById(R.id.tvFechaHoraAnotacion);
        TextView tvCreatedBy = convertView.findViewById(R.id.tvCreatedBy);
        Button btnEliminar = convertView.findViewById(R.id.btnEliminar);

        tvTexto.setText(anotacion.getTexto());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String fechaHoraStr = sdf.format(anotacion.getFechaHora());
        tvFechaHora.setText(fechaHoraStr);

        tvCreatedBy.setText("Creado por: " + anotacion.getCreatedBy());

        // Solo permitir eliminar la anotación si el correo del usuario actual coincide con el creador
        if (anotacion.getCreatedBy().equals(currentUserEmail)) {
            btnEliminar.setVisibility(View.VISIBLE); // Mostrar botón de eliminar
            btnEliminar.setOnClickListener(v -> eliminarAnotacion(anotacion));
        } else {
            btnEliminar.setVisibility(View.GONE); // Ocultar botón de eliminar si no es el creador
        }

        return convertView;
    }

    private void eliminarAnotacion(Anotacion anotacion) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = currentUserEmail; // Usar el email del usuario actual en lugar de userId

        db.collection("anotaciones_users")
                .document(userId)
                .collection("anotaciones")
                .document(anotacion.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    anotaciones.remove(anotacion); // Eliminar de la lista local
                    notifyDataSetChanged(); // Actualizar la vista
                    Toast.makeText(context, "Anotación eliminada", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al eliminar la anotación", Toast.LENGTH_SHORT).show();
                });
    }
}
