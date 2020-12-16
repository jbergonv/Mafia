package com.example.mafia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
    String[] arrayPosicion = new String[100];
    int cont=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_b_d);
        lista = findViewById(R.id.lista);

        mostrar();

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String aux = arrayPosicion[position];
                String[] partes = aux.split(",");
                String localizacion = partes[0]+","+partes[1];


                Intent intento = new Intent(Intent.ACTION_VIEW);
                intento.setData(Uri.parse("geo:"+localizacion));
                startActivity(intento);

            }
        });



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
                fila += " " + cursor.getString(3);
                arrayPosicion[cont]=cursor.getString(3);
                cont++;
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