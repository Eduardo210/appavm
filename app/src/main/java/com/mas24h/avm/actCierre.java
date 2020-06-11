package com.mas24h.avm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class actCierre extends AppCompatActivity {

    TextView datControl, tv_reporte, datUser;
    EditText et_ComentariosCierre;
    Button btnCerrar;
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cierre);

        recibirDatos();

        datControl =(TextView)findViewById(R.id.datControl);
        tv_reporte =(TextView)findViewById(R.id.tv_reporte);
        et_ComentariosCierre = (EditText)findViewById(R.id.et_ComentariosCierre);
        btnCerrar = (Button) findViewById(R.id.btnCerrar);

        buscarReporte("http://www.mas-asistencia.com/operador/operavial52.php?control="+datControl.getText()+"");

        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_ComentariosCierre.getText().toString().trim().length() == 0) {
                    //Toast.makeText(getBaseContext(), "Es necesario escribir los comentarios del cierre ", Toast.LENGTH_LONG).show();//EditText Vacio!
                    Toasty.info(getBaseContext(), "Es necesario escribir los comentarios del cierre", Toast.LENGTH_LONG, false).show();
                }else{
                    cargarWebService();
                }
            }
        });
    }

    private void recibirDatos(){
        Bundle extras = getIntent().getExtras();
        String d1 = extras.getString("dato01");
        String d2 = extras.getString("dato02");
        datControl = (TextView)findViewById(R.id.datControl);
        datUser = (TextView)findViewById(R.id.datUser);
        datControl.setText(d1);
        datUser.setText(d2);
    }

    private void buscarReporte(String URL) {

        progressDialog = new ProgressDialog(actCierre.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progres_dialog);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
    JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {
            JSONObject jsonObject = null;
            for (int i = 0; i < response.length(); i++) {
                try {
                    jsonObject = response.getJSONObject(i);

                    tv_reporte.setText(jsonObject.getString("idReporte"));

                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            progressDialog.dismiss();
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(), "Error de conexion", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
    }
    );
    requestQueue= Volley.newRequestQueue(this);
    requestQueue.add(jsonArrayRequest);
}

    private void cargarWebService(){

        progressDialog = new ProgressDialog(actCierre.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progres_dialog);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );

        String url = "https://mas-asistencia.com/operador/operavial53.php?";

        stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(),"prueba2 ",Toast.LENGTH_SHORT).show();
                if (response.trim().equalsIgnoreCase("registra")){

                    File dir = new File(Environment.getExternalStorageDirectory()+"nombre_folder");
                    //comprueba si es directorio.
                    if (dir.isDirectory())
                    {
                        //obtiene un listado de los archivos contenidos en el directorio.
                        String[] hijos = dir.list();
                        //Elimina los archivos contenidos.
                        for (int i = 0; i < hijos.length; i++)
                        {
                            new File(dir, hijos[i]).delete();
                        }
                    }

                    progressDialog.dismiss();
                    //Toast.makeText(getApplicationContext(),"Reporte cerrado con exito",Toast.LENGTH_LONG).show();
                    Toasty.info(getApplicationContext(), "Reporte cerrado con exito.", Toast.LENGTH_LONG, false).show();
                    Log.i("RESPUESTA: ",""+response);
                    Intent intent = new Intent(getApplicationContext(), actMain.class);
                    intent.putExtra("dato01", datUser.getText().toString());
                    startActivity(intent);
                    finish();
                }else{
                    progressDialog.dismiss();
                    Toasty.info(getApplicationContext(), "No se registro cierre, intente nuevamente", Toast.LENGTH_LONG, false).show();
                    Log.i("RESPUESTA: ",""+response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toasty.info(getApplicationContext(), "Error de conexion al servidor", Toast.LENGTH_LONG, false).show();
            //   Toast.makeText(getApplicationContext(),"Error de conexion al servidor",Toast.LENGTH_SHORT).show();
                //progreso.hide();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Toast.makeText(getApplicationContext(),"prueba2 ",Toast.LENGTH_SHORT).show();
                String control=datControl.getText().toString();
                String notas=et_ComentariosCierre.getText().toString();

                Map<String,String> parametros=new HashMap<>();
                parametros.put("control", control);
                parametros.put("notas",notas);

                return parametros;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
