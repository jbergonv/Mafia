package com.example.mafia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContextWrapper;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class activityMostrarBD extends AppCompatActivity {

    ListView lista;
    ActividadPrincipal actividadPrincipal = ActividadPrincipal.ap2;
    ManejadorBD manejadorBD = new ManejadorBD(actividadPrincipal);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_b_d);
        lista = findViewById(R.id.lista);

        mostrar();



    }

    public void mostrar(){

        Cursor cursor = manejadorBD.listar();
        ArrayAdapter<String> adapter;
        List<String> listaB = new ArrayList<>();

        if ((cursor != null) && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String fila = "";
                fila += cursor.getString(0);
                fila += " " + cursor.getString(1);
                fila += " " + cursor.getString(2);
                fila += " \n" + cursor.getString(3);
                fila += " " + cursor.getString(4);
                fila += " " + cursor.getString(5);
                listaB.add(fila);
            }
            adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item,listaB);



            lista.setAdapter(adapter);

        } else {
            Toast.makeText(activityMostrarBD.this, "Vacío", Toast.LENGTH_SHORT).show();
        }


    }

}