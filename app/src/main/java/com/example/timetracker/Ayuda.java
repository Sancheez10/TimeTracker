package com.example.timetracker;

import android.app.Dialog;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class Ayuda extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Ayuda")
                .setMessage("TimeTracker es un servicio para agilizar el sistema de fichar de los empleados a través de una aplicacion compatible con el ordenador, el ")
                .setPositiveButton("Aceptar", (dialog, id) -> dismiss());
        return builder.create();
    }
}