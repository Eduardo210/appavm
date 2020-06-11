package com.mas24h.avm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class actLogin extends AppCompatActivity {

    //Se declaran las variables con las que se vinculan a el XML
    EditText edtUsuario, edtPassword;
    Button btnLogin;
    String usuario, password;
    ProgressDialog progressDialog;
    private TextView mOutputText;
    //Variable para obtener el Tag
    public static final String TAG = "MyToken";

    //Metodo principal de la clase
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Se utiliza la clase findViewById para enlazar la clase con Java con el XML
        edtUsuario=findViewById(R.id.datUsuario);
        edtPassword=findViewById(R.id.edtPassword);
        btnLogin=findViewById(R.id.btnLogin);
        mOutputText=findViewById(R.id.tv_output);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {

                        if(task.isSuccessful()){
                            String token=task.getResult().getToken();
                            Log.d(TAG, "onComplete: Token: "+token);
                            mOutputText.setText(token);
                            Toasty.info(actLogin.this, "Token valido", Toast.LENGTH_SHORT, false).show();

                        }else{
                            mOutputText.setText("0");
                            Toasty.info(actLogin.this, "No se genero el token", Toast.LENGTH_LONG, false).show();
                        }

                    }
                });

        recuperarPreferencias();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usuario = edtUsuario.getText().toString();
                password = edtPassword.getText().toString();
                if (!usuario.isEmpty() && !password.isEmpty()) {
                    validarUsuario("https://www.mas-asistencia.com/security/secu03.php");
                } else {
                    Toasty.info(actLogin.this, "No se permiten campos vacios", Toast.LENGTH_LONG, false).show();

                }
            }
        });
    }
    private void validarUsuario(String URL){
        progressDialog = new ProgressDialog(actLogin.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progres_dialog);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );

        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.isEmpty()) {
                    guardarPreferencias();
                    progressDialog.dismiss();
                   Intent intent = new Intent(getApplicationContext(), actMain.class);
                   intent.putExtra("dato01", edtUsuario.getText().toString());
                   intent.putExtra("dato02", mOutputText.getText().toString());
                   startActivity(intent);
                   finish();
                } else {
                    progressDialog.dismiss();

                    Toasty.info(actLogin.this, "Usuario o contrasena incorrecto.", Toast.LENGTH_LONG, false).show();
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toasty.info(actLogin.this, error.toString(), Toast.LENGTH_LONG, false).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> parametros=new HashMap<String, String>();
                parametros.put("usuario", usuario);
                parametros.put("password", password);
                return parametros;
            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void guardarPreferencias(){
        SharedPreferences preferences=getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("usuario", usuario);
        editor.putString("password", password);
        editor.putBoolean("sesion", true);
        editor.commit();
    }

    private void recuperarPreferencias(){
        SharedPreferences preferences=getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        edtUsuario.setText(preferences.getString("usuario",""));
        edtPassword.setText(preferences.getString("password", ""));

    }
}
