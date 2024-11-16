package com.example.appclinicabiovida;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.appclinicabiovida.databinding.ActivityGestionarcitasAdministadorBinding;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
public class gestionarcitas_administador extends AppCompatActivity  implements OnItemClickListener{
    ActivityGestionarcitasAdministadorBinding binding;
    public static ArrayList<Cita> listaUsuarios;

    ImageView b2;
    private RequestQueue rq;
    private RecyclerView lst1;
    private AdaptadorUsuario adaptadorUsuario;

    String codigoCita,dniPaciente,sintomas,estadoCita,motivocancelacionCita,idHorario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestionarcitas_administador);
        binding = ActivityGestionarcitasAdministadorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        b2=findViewById(R.id.bt2);

        lst1=findViewById(R.id.lst1);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i = new Intent(getApplicationContext(), horario_administrador.class);
                startActivity(i);
                finish();
            }
        });


        /*boton_imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(gestionarcitas_administador.this, "Registrar Datos", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), editarcitas_administrador.class);
                startActivity(i);
                finish();
            }
        });*/




        listaUsuarios=new ArrayList<>();
        rq= Volley.newRequestQueue(this);

        cargarPersona();

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        lst1.setLayoutManager(linearLayoutManager);
        adaptadorUsuario=new AdaptadorUsuario();
        lst1.setAdapter(adaptadorUsuario);


    }

    private void cargarPersona() {
        String url="http://192.168.0.199:80/cita/mostrar_.php";
        JsonObjectRequest requerimiento=new JsonObjectRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String valor=response.get("datos").toString();
                            JSONArray arreglo=new JSONArray(valor);
                            JSONArray jsonArray =response.getJSONArray("datos");

                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject objeto = new JSONObject(arreglo.get(i).toString());
                                codigoCita= objeto.getString("codigoCita");
                                dniPaciente = objeto.getString("dniPaciente");
                                sintomas = objeto.getString("sintomas");
                                estadoCita= objeto.getString("estadoCita");
                                motivocancelacionCita=objeto.getString("motivocancelacionCita");
                                idHorario=objeto.getString("idHorario");




                                Cita usuario=new Cita(codigoCita,dniPaciente,sintomas,estadoCita ,idHorario,motivocancelacionCita);
                                listaUsuarios.add(usuario);
                                adaptadorUsuario.notifyItemRangeInserted(listaUsuarios.size(), i+1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        rq.add(requerimiento);
    }

    @Override
    public void onItemClick(Cita cita) {
        Toast.makeText(this, "HOla",Toast.LENGTH_LONG).show();
    }


    private class AdaptadorUsuario extends RecyclerView.Adapter<AdaptadorUsuario.AdaptadorUsuarioHolder>{
        private OnItemClickListener listener;
        @NonNull
        @Override
        public AdaptadorUsuarioHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new AdaptadorUsuarioHolder(getLayoutInflater().inflate(R.layout.list_item,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull AdaptadorUsuarioHolder holder, int position) {
            holder.imprimir(position);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(),editarcitas_administrador.class).putExtra("position",position));
                }
            });

        }

        @Override
        public int getItemCount() {
            return listaUsuarios.size();
        }


        class AdaptadorUsuarioHolder extends RecyclerView.ViewHolder {
            TextView tvNombre, tvPaciente, tvEstado,tvfecha;
            ImageView tvlistar;

            public CardView cardView;
            public AdaptadorUsuarioHolder(@NonNull View itemView) {
                super(itemView);
                tvPaciente =itemView.findViewById(R.id.dniPaciente1);
                tvNombre=itemView.findViewById(R.id.paciente);
                tvEstado =itemView.findViewById(R.id.estadocita1);
                tvlistar =itemView.findViewById(R.id.editar);
                cardView=itemView.findViewById(R.id.main_container);
            }

            public void imprimir(int position) {
                tvNombre.setText(""+listaUsuarios.get(position).getCodigoCita());
                tvPaciente.setText(""+listaUsuarios.get(position).getDniPaciente());
                tvEstado.setText("Estado\n"+listaUsuarios.get(position).getEstadoCita());


                //  listener.onItemClick(listaUsuarios.get(position));
            }



        }
    }
}