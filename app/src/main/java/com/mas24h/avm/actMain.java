package com.mas24h.avm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.mas24h.avm.Controlador.Adapter;
import com.mas24h.avm.Controlador.Folios;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;


public class actMain extends AppCompatActivity {

    //Las variables definidas
    TextView tvUser, tv_output;
    List<Folios> reportesList= null;
    RecyclerView recyclerView;
    ProgressDialog progressDialog;

    private SwipeRefreshLayout swipeRefreshLayout;
    public static final String TAG = "MyTag";
    private TextView mOutputText;
    StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Enlazamos a los controladores del XML con la clase Java
        tvUser = (TextView)findViewById(R.id.tvUser);
        mOutputText=findViewById(R.id.tv_output);
        // Invocamos el metodo para recir los datos
        recibirDatos();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reportesList = new ArrayList<>();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.blancoToast);
        // Invocamos a metodo para leer los folios
        loadFolios();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
           public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                refreshFolios();
            }
        });
        // Invocamos al metodo cargar Token
        cargarToken();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mnu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.itm_srh) {
            Intent buscar= new Intent(this, actBuscar.class );
            startActivity(buscar);
        } else if (id == R.id.itm_act){
            refreshFolios();
            //Toast.makeText(this, "Opcion actualizar", Toast.LENGTH_LONG).show();

        } else if (id == R.id.itm_disp) {
            Intent disponibilidad= new Intent(this, actDisponibilidad.class );
            disponibilidad.putExtra("dato05", tvUser.getText().toString());
            startActivity(disponibilidad);

        } else if (id == R.id.itm_solicitud) {
            Intent solicitud = new Intent(this, actSolicitud.class );
            solicitud.putExtra("dato06", tvUser.getText().toString());
            startActivity(solicitud);

        } else if (id == R.id.itm_endSession) {
            logOut();
        }
        return super.onOptionsItemSelected(item);
    }

    private void recibirDatos(){
        Bundle extras = getIntent().getExtras();
        //Recibe el nombre del usuario
        String d1 = extras.getString("dato01");
        //Recibe el token de autentication
        String d2 = extras.getString("dato02");
        tvUser.setText(d1);
        mOutputText.setText(d2);
    }

    private void loadFolios() {

        progressDialog = new ProgressDialog(actMain.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progres_dialog);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );

        final String URL_folios ="https://mas-asistencia.com/operador/operavial40.php?user="+tvUser.getText()+"";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_folios,new Response.Listener<String>() {
            @Override

            public void onResponse(String response) {

                try {
                    JSONArray array = new JSONArray(response);

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject folio = array.getJSONObject(i);

                        reportesList.add(new Folios(

                                folio.getInt("idReporte"),
                                folio.getString("estatus"),
                                folio.getString("calle"),
                                folio.getString("colonia")
                        ));
                    }

                    Adapter adapter = new Adapter(actMain.this, reportesList);
                    recyclerView.setAdapter(adapter);
                    progressDialog.dismiss();

                    adapter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent= new Intent(actMain.this, actDetalle.class);
                            //Intent intent= new Intent(actMain.this, actMain.class);
                            //dato = idReporte
                            int dato = reportesList.get(recyclerView.getChildAdapterPosition(view)).getidReporte();
                            String dato2 = reportesList.get(recyclerView.getChildAdapterPosition(view)).getEstatus();
                            intent.putExtra("dato01", dato);
                            intent.putExtra("dato02", dato2);
                            intent.putExtra("dato03", tvUser.getText().toString());

                            startActivity(intent);
                            finish();
                            //finish();

                        }
                    });

                } catch (JSONException e) {
                    Toasty.info(getApplication(),"error 8", Toast.LENGTH_LONG, false).show();
                    //  Toast.makeText(getApplication(), "error 8", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.info(getApplication(),error.toString(), Toast.LENGTH_LONG, false).show();
                //Toast.makeText(getApplication(), error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        Volley.newRequestQueue(this).add(stringRequest);

    }

    private void refreshFolios() {
        reportesList.clear();

       final String URL_folios ="https://mas-asistencia.com/operador/operavial40.php?user="+tvUser.getText()+"";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_folios,new Response.Listener<String>() {
            @Override

            public void onResponse(String response) {

                try {
                    JSONArray array = new JSONArray(response);

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject folio = array.getJSONObject(i);

                        reportesList.add(new Folios(

                                folio.getInt("idReporte"),
                                folio.getString("estatus"),
                                folio.getString("calle"),
                                folio.getString("colonia")
                        ));
                    }

                    Adapter adapter = new Adapter(actMain.this, reportesList);
                    recyclerView.setAdapter(adapter);
                    progressDialog.dismiss();

                    adapter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //Intent intent= new Intent(actMain.this, actDetalle.class);
                            Intent intent= new Intent(actMain.this, actDetalle.class);
                            int dato = reportesList.get(recyclerView.getChildAdapterPosition(view)).getidReporte();
                            String dato2 = reportesList.get(recyclerView.getChildAdapterPosition(view)).getEstatus();
                            intent.putExtra("dato01", dato);
                            intent.putExtra("dato02", dato2);
                            intent.putExtra("dato03", tvUser.getText().toString());

                            startActivity(intent);



                        }
                    });

                } catch (JSONException e) {
                    Toasty.info(getApplication(),"error 8", Toast.LENGTH_LONG, false).show();
                    //  Toast.makeText(getApplication(), "error 8", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.info(getApplication(),error.toString(), Toast.LENGTH_LONG, false).show();
                //Toast.makeText(getApplication(), error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        Volley.newRequestQueue(this).add(stringRequest);

    }

    public void logOut(){
        SharedPreferences preferences=getSharedPreferences("preferenciaslogin", Context.MODE_PRIVATE);
        preferences.edit().clear().commit();
        Intent intent=new Intent(getApplicationContext(),actLogin.class);
        startActivity(intent);
        finish();
    }

    private void cargarToken() {
        String token = mOutputText.getText().toString();
        if (!(token == "0") && !token.isEmpty()) {
            String url = "https://mas-asistencia.com/operador/operavial54.php?";
            stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    if (response.trim().equalsIgnoreCase("registra")) {
                       Toasty.info(getApplicationContext(), "Token registrado con exito.", Toast.LENGTH_LONG, false).show();
                        Log.i("RESPUESTA: ", "" + response);
                    } else {
                        Toasty.info(getApplicationContext(), "Token valido", Toast.LENGTH_LONG, false).show();
                        Log.i("RESPUESTA: ", "" + response);

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toasty.info(getApplicationContext(), "Error de conexion al servidor", Toast.LENGTH_LONG, false).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    String usr = tvUser.getText().toString();
                    String token = mOutputText.getText().toString();

                    Map<String, String> parametros = new HashMap<>();
                    parametros.put("usr", usr);
                    parametros.put("token", token);

                    return parametros;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

        } else {
            //Toasty.info(getApplicationContext(), "No se activo token, es necesario reiniciar app.", Toast.LENGTH_LONG, false).show();
        }

    }

}
