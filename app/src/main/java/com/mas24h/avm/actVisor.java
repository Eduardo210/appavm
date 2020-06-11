package com.mas24h.avm;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import es.dmoral.toasty.Toasty;

public class actVisor extends AppCompatActivity {

    TextView datControl, datReporte, datTipoAsistencia, datCompania, datNomUsuario, datTelUser, datOperador, datEstado;
    TextView datMunicpio, datCalle, datEsquina, datColonia, datReferencias, datMarca, datTipoVeh, datColor, datPlacas, datSerie;
    RequestQueue requestQueue;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visor);

        datControl = findViewById(R.id.datControl);
        datReporte = findViewById(R.id.datReporte);
        datTipoAsistencia = findViewById(R.id.datTipoAsistencia);
        datCompania = findViewById(R.id.datCompania);
        datNomUsuario = findViewById(R.id.datNomUsuario);
        datTelUser = findViewById(R.id.datTelUser);
        datOperador = findViewById(R.id.datOperador);
        datEstado = findViewById(R.id.datEstado);
        datMunicpio = findViewById(R.id.datMunicpio);
        datCalle = findViewById(R.id.datCalle);
        datEsquina = findViewById(R.id.datEsquina);
        datColonia = findViewById(R.id.datColonia);
        datReferencias = findViewById(R.id.datReferencias);
        datMarca = findViewById(R.id.datMarca);
        datTipoVeh = findViewById(R.id.datTipoVeh);
        datColor = findViewById(R.id.datColor);
        datPlacas = findViewById(R.id.datPlacas);
        datSerie = findViewById(R.id.datSerie);

        recibirDatos();

        buscarReporte("http://www.mas-asistencia.com/operador/operavial45.php?control="+datControl.getText()+"");
    }

    private void recibirDatos() {
        Bundle extras = getIntent().getExtras();
        String d1 = extras.getString("dato01");
        datControl = (TextView) findViewById(R.id.datControl);
        datControl.setText(d1);
    }

    private void buscarReporte(String URL) {

        progressDialog = new ProgressDialog(actVisor.this);
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

                        datReporte.setText(jsonObject.getString("idReporte"));
                        datTipoAsistencia.setText(jsonObject.getString("tipoasistencia"));
                        datCompania.setText(jsonObject.getString("empresa"));
                        datNomUsuario.setText(jsonObject.getString("usuarioensitio"));
                        datTelUser.setText(jsonObject.getString("telusuario"));
                        datOperador.setText(jsonObject.getString("nombrecompleto"));
                        datEstado.setText(jsonObject.getString("estado"));
                        datMunicpio.setText(jsonObject.getString("mpo"));
                        datCalle.setText(jsonObject.getString("calle"));
                        datEsquina.setText(jsonObject.getString("entrecalles"));
                        datColonia.setText(jsonObject.getString("colonia"));
                        datReferencias.setText(jsonObject.getString("referencia"));
                        datMarca.setText(jsonObject.getString("marca"));
                        datTipoVeh.setText(jsonObject.getString("vehtipo"));
                        datColor.setText(jsonObject.getString("color"));
                        datPlacas.setText(jsonObject.getString("vehplacas"));
                        datSerie.setText(jsonObject.getString("vehserie"));

                    } catch (JSONException e) {
                        progressDialog.dismiss();
                        Toasty.info(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG, false).show();

                     //   Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.info(getApplicationContext(), "Error de conexion", Toast.LENGTH_LONG, false).show();
            //    Toast.makeText(getApplicationContext(), "Error de conexion", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }
        );
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }
}
