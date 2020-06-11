package com.mas24h.avm;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Se carga el activity para mandar el Splash
        setContentView(R.layout.activity_splash);

        //Validamos los permisos
        if(validaPermisos()){
            //Handler posiciona el activity para su posterior uso
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Cambiamos el activity por medio de la clase Intent
                    Intent intent=new Intent(SplashActivity.this, actLogin.class);
                    //Iniciamos la clase con el metodo startActivity
                    startActivity(intent);
                    //Finaizamos la activity para que no se ponga en cola
                    finish();
                    //El tiempo de ejecucion es de 2 segundos que se mide en milisegundos
                }
            },2000);
        }else{
            Toast.makeText(getApplicationContext(),"No se cargaron los datos",Toast.LENGTH_LONG).show();
        }
    }

    private boolean validaPermisos() { //Este metodo valida los permisos a la camara y fotos
        // Accede a la version del android
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }
        // Pide permisos para acceder a la camara
        if((checkSelfPermission(CAMERA)== PackageManager.PERMISSION_GRANTED)&&
                (checkSelfPermission(WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)){
            return true;
        }
        //Pide permisos para el almacenamiento
        if((shouldShowRequestPermissionRationale(CAMERA)) ||
                (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))){
            cargarDialogoRecomendacion();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==100){
            if(grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED
                    && grantResults[1]==PackageManager.PERMISSION_GRANTED){

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent=new Intent(SplashActivity.this, actLogin.class);
                       startActivity(intent);
                       finish();
                    }
                },2000);
            }else{
                solicitarPermisosManual();
            }
        }

    }
    //Solicita al usuario hacer los permisos de manera manual para obtener los permisos de la aplicacion
    private void solicitarPermisosManual() {
        final CharSequence[] opciones={"si","no"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(SplashActivity.this);
        alertOpciones.setTitle("¿Desea configurar los permisos de forma manual?");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("si")){
                    Intent intent=new Intent();
                    //Lleva a la ruta de la aplicacion para asignar los permisos
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri=Uri.fromParts("package",getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);
                }else{
                    Toasty.info(getApplicationContext(),"Los permisos no fueron aceptados", Toast.LENGTH_LONG, false).show();
                    // Toast.makeText(getApplicationContext(),"Los permisos no fueron aceptados",Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                    finish();
                }
            }
        });
        alertOpciones.show();
    }

    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo=new AlertDialog.Builder(SplashActivity.this);
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
            }
        });
        dialogo.show();
    }

}
