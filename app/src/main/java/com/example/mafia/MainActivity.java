package com.example.mafia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.InputType;
import android.transition.TransitionManager;
import android.util.Base64;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    private static final int VENGO_DE_LA_CAMARA = 1;
    private static final int VENGO_DE_LA_GALERIA = 3;
    private static final int PEDI_PERMISO_DE_ESCRITURA = 1;
    private static final int VENGO_DE_LA_CAMARA_CON_FICHERO = 2;
    private static final String PREFERENCIAS = "preferencias";
    private static final String NOMBRE_FICHERO = "fichero_foto";
    static final String ACCESO = "ACCESO";
    private static final String VALOR_CONTRASEÑA = "VALOR_CONTRASEÑA";


    EditText contraseña;
    ImageView foto;
    Button sacarFoto,buscarFoto,acceder;
    boolean primerAcceso;

    File fichero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCIAS, MODE_PRIVATE);






        contraseña = findViewById(R.id.introducirContraseña);
        foto = findViewById(R.id.fotoMafioso);
        sacarFoto = findViewById(R.id.botonSacarFoto);
        buscarFoto = findViewById(R.id.botonBuscarFoto);
        acceder = findViewById(R.id.botonIngresar);


        if(sharedPreferences.getBoolean(ACCESO,true)==false){

            Bitmap bp;

            bp = StringToBitMap(sharedPreferences.getString("BITMAP_FOTO",""));

            foto.setImageBitmap(bp);
            sacarFoto.setVisibility(View.INVISIBLE);
            buscarFoto.setVisibility(View.INVISIBLE);
            contraseña.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        }




        sacarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (intent.resolveActivity(getPackageManager())!=null) {
                    startActivityForResult(intent, VENGO_DE_LA_CAMARA);
                }else{
                    Toast.makeText(MainActivity.this, "Necesitas un programa que haga fotos.", Toast.LENGTH_SHORT).show();
                }


            }
        });

        buscarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,VENGO_DE_LA_GALERIA);

            }
        });

        acceder.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                Context context;
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.shake );
                acceder.startAnimation(animation);

                SharedPreferences misPreferencias = getSharedPreferences(PREFERENCIAS,MODE_PRIVATE);
                SharedPreferences.Editor editor = misPreferencias.edit();

                    if(misPreferencias.getBoolean(ACCESO,true)){

                        if(contraseña.getText().toString().isEmpty()){
                            Toast.makeText(MainActivity.this, R.string.toastContraseñaVacia, Toast.LENGTH_SHORT).show();
                        }else{

                            editor.putString(VALOR_CONTRASEÑA,contraseña.getText().toString());
                            editor.putBoolean(ACCESO,false);
                            editor.apply();
                            Intent intento = new Intent(MainActivity.this,ActividadPrincipal.class);
                            startActivity(intento);

                        }

                    }else{



                        if(contraseña.getText().toString().equals(misPreferencias.getString(VALOR_CONTRASEÑA,""))){

                            Intent intento = new Intent(MainActivity.this,ActividadPrincipal.class);
                            startActivity(intento);

                        }else{

                            Toast.makeText(MainActivity.this, R.string.contraseñaIncorrecta, Toast.LENGTH_SHORT).show();

                        }

                    }



            }
        });


    }

    private void pedirPermisoParafoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                PackageManager.PERMISSION_GRANTED){ //No tengo permiso
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){

            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PEDI_PERMISO_DE_ESCRITURA);
            }
        }else{ //Tengo permiso
            hacerFotoAltaResolucion();
        }
    }

    private File crearFicheroDeFoto() throws IOException {
        String fechaYHora = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nombreFichero = "misFotos_"+fechaYHora;
        File carpetaFotos = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imagenAltaResolucion = File.createTempFile(nombreFichero,".jpg", carpetaFotos);
        return imagenAltaResolucion;


    }



    private void hacerFotoAltaResolucion() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            fichero = crearFicheroDeFoto();
        } catch (IOException e) {
            e.printStackTrace();
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fichero));

        if (intent.resolveActivity(getPackageManager())!=null) {
            startActivityForResult(intent, VENGO_DE_LA_CAMARA_CON_FICHERO);
        }else{
            Toast.makeText(MainActivity.this, "Necesitas un programa que haga fotos.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==PEDI_PERMISO_DE_ESCRITURA){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                hacerFotoAltaResolucion();
            }else{
                Toast.makeText(this, "Sin permiso de escritura no puedo hacer foto a alta resolución.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VENGO_DE_LA_GALERIA && resultCode == RESULT_OK){
            Uri targetUri = data.getData();
            //textTargetUri.setText(targetUri.toString());
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                foto.setImageBitmap(bitmap);
                String aux = BitMapToString(bitmap);
                SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCIAS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("BITMAP_FOTO",aux );
                editor.apply();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }





        if (requestCode == VENGO_DE_LA_CAMARA && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            foto.setImageBitmap(bitmap);
            String aux = BitMapToString(bitmap);
            SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCIAS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("BITMAP_FOTO",aux );
            editor.apply();



        }else if (requestCode==VENGO_DE_LA_CAMARA_CON_FICHERO && resultCode==RESULT_OK){
            foto.setImageBitmap(BitmapFactory.decodeFile(fichero.getAbsolutePath()));
            SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCIAS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(NOMBRE_FICHERO,fichero.getAbsolutePath() );
            editor.apply();
        }else if (requestCode==VENGO_DE_LA_CAMARA_CON_FICHERO && resultCode==RESULT_CANCELED){
            fichero.delete();
        }
    }


    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte = Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }


}