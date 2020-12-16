package com.example.mafia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static android.content.Context.LOCATION_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

public class ManejadorBD extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "datos.db";

    private static final String COL_ID = "ID";
    private static final String COL_FECHA= "FECHA";
    private static final String COL_BATERIA = "BATERIA";
    private static final String COL_POSICION = "POSICION";
    private static final String COL_DIRECCION = "DIRECCION";
    private static final String COL_MOTIVO = "MOTIVO";
    private static final String TABLE_NAME = "LOCALIZACION";



    public ManejadorBD(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public ManejadorBD(ActividadPrincipal actividadPrincipal){
        super(actividadPrincipal,DATABASE_NAME,null,1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_FECHA + " TEXT, " + COL_BATERIA + " TEXT, " + COL_POSICION + " TEXT, " + COL_DIRECCION + " TEXT, "+ COL_MOTIVO + ")");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public boolean insertar(String fecha, String bateria, String posicion, String direccion,String motivo){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_FECHA,fecha);
        contentValues.put(COL_BATERIA,bateria);
        contentValues.put(COL_POSICION,posicion);
        contentValues.put(COL_DIRECCION,direccion);
        contentValues.put(COL_MOTIVO,motivo);

        long resultado = db.insert(TABLE_NAME,null,contentValues);
        db.close();
        return(resultado != -1);


    }

    public boolean borrar(){

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
        db.close();
        return true;


    }

    Cursor listar(){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_NAME,null);
        return cursor;

    }

    public int contarFilas(){

        SQLiteDatabase db = this.getReadableDatabase();
        int contador = (int) DatabaseUtils.queryNumEntries(db,TABLE_NAME);
        db.close();
        return contador;

    }




}
