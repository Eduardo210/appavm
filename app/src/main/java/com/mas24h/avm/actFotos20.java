package com.mas24h.avm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class actFotos20 extends AppCompatActivity {



    public final String CARPETA_RAIZ="Mas-Asistencia/";
    public final String RUTA_IMAGEN=CARPETA_RAIZ;

    final int COD_SELECCIONA=10;
    final int COD_FOTO=20;

    Bitmap bitmap02;
    TextView datControl;
    ImageButton botonTomar02;
    Button botonRegistro02;
    ImageView imgFoto02;
    ProgressDialog progressDialog;

    String path;
    StringRequest stringRequest;
    //RequestQueue request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fotos20);

        datControl = (TextView)findViewById(R.id.datControl);
        botonTomar02 = (ImageButton) findViewById(R.id.btnTomarImg02);
        //botonCargar = (ImageButton) findViewById(R.id.btnCargarImg);
        botonRegistro02= (Button) findViewById(R.id.btnRegistrar02);
        imgFoto02=(ImageView)findViewById(R.id.imgFoto02);
        recibirDatos();

        botonRegistro02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                cargarWebService();
            }
        });

        botonTomar02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tomarFotografia();
            }
        });

        // botonCargar.setOnClickListener(new View.OnClickListener() {
        //   @Override
        //   public void onClick(View v) {
        //      Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //      intent.setType("image/");
        //      startActivityForResult(intent.createChooser(intent,"Seleccione la Aplicación"),COD_SELECCIONA);
        //  }
        //});

    }

    private void tomarFotografia() {
        String control = datControl.getText().toString();
        //String iden = "identificacion";
        //File fileImagen = new File(Environment.getExternalStorageDirectory(), CARPETA_RAIZ+control+"/"+iden);
        File fileImagen = new File(Environment.getExternalStorageDirectory(), CARPETA_RAIZ+control);
        boolean isCreada=fileImagen.exists();
        String nombreImagen="";
        if(isCreada==false){
            isCreada=fileImagen.mkdirs();
        }
        if(isCreada==true){
            nombreImagen=(System.currentTimeMillis()/10000)+".jpg";
            //nombreImagen=datControl.getText().toString()+"_ident_"+(System.currentTimeMillis()/1000000)+".jpg";
            botonRegistro02.setEnabled(true);
        }
        //path=Environment.getExternalStorageDirectory()+File.separator+RUTA_IMAGEN+control+"/"+iden+File.separator+nombreImagen;
        path=Environment.getExternalStorageDirectory()+File.separator+RUTA_IMAGEN+control+File.separator+nombreImagen;
        fileImagen=new File(path);

        Intent intent=null;
        intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(fileImagen));
        ////
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N)
        {
            String authorities=getApplicationContext().getPackageName()+".provider";
            Uri imageUri= FileProvider.getUriForFile(this,authorities,fileImagen);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        }else
        {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImagen));
        }
        startActivityForResult(intent,COD_FOTO);
    }

    private void cargarWebService(){

        progressDialog = new ProgressDialog(actFotos20.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progres_dialog);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );

        String url = "https://mas-asistencia.com/operador/operavial50.php?";

        stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(),"prueba2 ",Toast.LENGTH_SHORT).show();
                if (response.trim().equalsIgnoreCase("registra")){
                    progressDialog.dismiss();
                    imgFoto02.setImageResource(0);
                    imgFoto02.setImageDrawable(getResources().getDrawable(R.drawable.ic_fotito));
                    botonRegistro02.setEnabled(false);
                    Toasty.info(getApplicationContext(), "Imagen guardada con exito", Toast.LENGTH_LONG, false).show();
                //  Toast.makeText(getApplicationContext(),"Imagen guardada con exito",Toast.LENGTH_SHORT).show();
                    Log.i("RESPUESTA: ",""+response);
                }else{
                    progressDialog.dismiss();
                    Toasty.info(getApplicationContext(), "No se ha registrado", Toast.LENGTH_LONG, false).show();
                    //Toast.makeText(getApplicationContext(),"No se ha registrado ",Toast.LENGTH_SHORT).show();
                    Log.i("RESPUESTA: ",""+response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toasty.info(getApplicationContext(), "No se ha podido conectar", Toast.LENGTH_LONG, false).show();
//                Toast.makeText(getApplicationContext(),"No se ha podido conectar",Toast.LENGTH_SHORT).show();
                //progreso.hide();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Toast.makeText(getApplicationContext(),"prueba2 ",Toast.LENGTH_SHORT).show();
                String control=datControl.getText().toString();
                String imagen=convertirImgString(bitmap02);

                Map<String,String> parametros=new HashMap<>();
                parametros.put("control", control);
                parametros.put("imagen",imagen);

                return parametros;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private String convertirImgString(Bitmap bitmap02) {

        ByteArrayOutputStream array=new ByteArrayOutputStream();
        bitmap02.compress(Bitmap.CompressFormat.JPEG,100,array);
        byte[] imagenByte=array.toByteArray();
        String imagenString= Base64.encodeToString(imagenByte,Base64.DEFAULT);

        return imagenString;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case COD_SELECCIONA:
                Uri miPath=data.getData();
                imgFoto02.setImageURI(miPath);

                try {
                    bitmap02=MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),miPath);
                    imgFoto02.setImageBitmap(bitmap02);
                    botonRegistro02.setEnabled(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case COD_FOTO:
                MediaScannerConnection.scanFile(getApplicationContext(), new String[]{path}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {
                                Log.i("Path",""+path);
                            }
                        });

                bitmap02= BitmapFactory.decodeFile(path);
                imgFoto02.setImageBitmap(bitmap02);

                break;
        }
        bitmap02=redimensionarImagen(bitmap02,800,600);
    }

    private Bitmap redimensionarImagen(Bitmap bitmap02, float anchoNuevo02, float altoNuevo02) {
        int ancho02=bitmap02.getWidth();
        int alto02=bitmap02.getHeight();
        if(ancho02>anchoNuevo02 || alto02>altoNuevo02){
            float escalaAncho=anchoNuevo02/ancho02;
            float escalaAlto= altoNuevo02/alto02;
            Matrix matrix=new Matrix();
            matrix.postScale(escalaAncho,escalaAlto);
            return Bitmap.createBitmap(bitmap02,0,0,ancho02,alto02,matrix,false);
        }else{
            return bitmap02;
        }
    }

    private void recibirDatos(){
        Bundle extras = getIntent().getExtras();
        String d1 = extras.getString("dato01");
        //    datControl = (TextView)findViewById(R.id.datControl);
        datControl.setText(d1);
    }

}
