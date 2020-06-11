package com.mas24h.avm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mas24h.avm.Controlador.ServiceHandler;
import com.mas24h.avm.Controlador.solicitudes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class actSolicitud extends Activity implements AdapterView.OnItemSelectedListener {

    Button btn_solicitud;
    private Spinner spinnersolicitudes;
    private ArrayList<solicitudes> solicitudesList;
    ProgressDialog pDialog;
    ProgressDialog progressDialog;
    StringRequest stringRequest;

    EditText cnotas;
    TextView tv_user;


    private String URL_LISTA_SOLICITUDES = "http://www.mas-asistencia.com/operador/operavial60.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitud);
        spinnersolicitudes = (Spinner) findViewById(R.id.spinsolicitud);
        solicitudesList = new ArrayList<solicitudes>();
        spinnersolicitudes.setOnItemSelectedListener(this);
        cnotas = (EditText)findViewById(R.id.et_notas);
        tv_user = (TextView) findViewById(R.id.tv_user);
        btn_solicitud = (Button) findViewById(R.id.btn_solicitud);

        recibirDatos();

        new Getsolicitudes().execute();

        btn_solicitud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cnotas.getText().toString().trim().length() == 0) {
                    Toasty.info(getBaseContext(), "Es necesario escribir los comentarios de la solicitud", Toast.LENGTH_LONG, false).show();
                }else{
                    cargarWebService();
                }
            }
        });

    }
    private void populateSpinner() {
        List<String> lables = new ArrayList<String>();
        for (int i = 0; i < solicitudesList.size(); i++) {
            lables.add(solicitudesList.get(i).getsolicitudTipo());
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, lables);
        spinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnersolicitudes.setAdapter(spinnerAdapter);
    }
    private class Getsolicitudes extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(actSolicitud.this);
            pDialog.setMessage("Obtencion de las solicitudes..");
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            ServiceHandler jsonParser = new ServiceHandler();
            String json = jsonParser.makeServiceCall(URL_LISTA_SOLICITUDES, ServiceHandler.GET);
            Log.e("Response: ", "> " + json);
            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if (jsonObj != null) {
                        JSONArray solicitudes = jsonObj
                                .getJSONArray("solicitudes");
                        for (int i = 0; i < solicitudes.length(); i++) {
                            JSONObject catObj = (JSONObject) solicitudes.get(i);
                            solicitudes cat = new solicitudes(catObj.getInt("id"),
                                    catObj.getString("solicitudTipo"));
                            solicitudesList.add(cat);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("JSON Data", "¿No ha recibido ningún dato desde el servidor!");
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            populateSpinner();
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        //Toast.makeText(getApplicationContext(),parent.getItemAtPosition(position).toString() + " Seleccionado" ,Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    private void recibirDatos() {
        Bundle extras = getIntent().getExtras();
        String d1 = extras.getString("dato06");
        tv_user.setText(d1);
    }

    private void cargarWebService(){

        progressDialog = new ProgressDialog(actSolicitud.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progres_dialog);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );

        String url = "https://mas-asistencia.com/operador/operavial61.php?";

        stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(),"prueba2 ",Toast.LENGTH_SHORT).show();
                if (response.trim().equalsIgnoreCase("registra")){

                    File dir = new File(Environment.getExternalStorageDirectory()+"nombre_folder");
                    //comprueba si es directorio.
                    if (dir.isDirectory()) {
                        //obtiene un listado de los archivos contenidos en el directorio.
                        //String[] hijos = dir.list();
                        //Elimina los archivos contenidos.
                        //for (int i = 0; i < hijos.length; i++)
                        //{
                            //new File(dir, hijos[i]).delete();
                        //}
                    }

                    progressDialog.dismiss();
                    //Toast.makeText(getApplicationContext(),"Reporte cerrado con exito",Toast.LENGTH_LONG).show();
                    Toasty.info(getApplicationContext(), "Solicitud agregada con exito.", Toast.LENGTH_LONG, false).show();
                    Log.i("RESPUESTA: ",""+response);
                    Intent intent = new Intent(getApplicationContext(), actMain.class);
                    intent.putExtra("dato01", tv_user.getText().toString());
                    startActivity(intent);
                    finish();
                }else{
                    progressDialog.dismiss();
                    Toasty.info(getApplicationContext(), "No se registro la solicitud, intente nuevamente", Toast.LENGTH_LONG, false).show();
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

                String tipoSolicitud = spinnersolicitudes.getItemAtPosition(spinnersolicitudes.getSelectedItemPosition()).toString();
                String notas=cnotas.getText().toString();
                String user=tv_user.getText().toString();

                Map<String,String> parametros=new HashMap<>();
                parametros.put("tipoSolicitud", tipoSolicitud);
                parametros.put("notas",notas);
                parametros.put("user", user);
                return parametros;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


}
