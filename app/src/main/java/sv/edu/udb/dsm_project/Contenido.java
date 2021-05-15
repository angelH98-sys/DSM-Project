package sv.edu.udb.dsm_project;

import android.R.layout;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sv.edu.udb.dsm_project.Modelo.Producto;

public class Contenido extends AppCompatActivity {
    FirebaseFirestore db;
    Producto pd ;
    String actualDoc;
    List<Producto> productos;
    ListView listaProductos;
    FloatingActionButton fab_agregar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mostrar_activity);
        db= FirebaseFirestore.getInstance();
        fab_agregar= findViewById(R.id.fab_agregar);
        inicializar();
        obtenerDatos();
      /*  fab_agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cuando el usuario quiere agregar un nuevo registro
                Intent i = new Intent(getBaseContext(), Registro_Menu.class);
                i.putExtra("accion","a"); // Agregar
                i.putExtra("key","");
                i.putExtra("nomb","");
                i.putExtra("desc","");
                i.putExtra("prec","");
                i.putExtra("esta","false");
                startActivity(i);
            }
        });*/
    }
    private void inicializar() {

        ticketExist();
        listaProductos = findViewById(R.id.ListaProductos);
        productos = new ArrayList<>();
        // Cuando el usuario haga clic en la lista (para editar registro)
        listaProductos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getBaseContext(), Registro_Menu.class);

                intent.putExtra("accion", "e"); // Editar
                intent.putExtra("key", productos.get(i).getKey());
                intent.putExtra("nomb", productos.get(i).getNomb());
                intent.putExtra("precio", productos.get(i).getPrec());
                intent.putExtra("desc", productos.get(i).getDesc());
                intent.putExtra("estado", productos.get(i).getEstado());
                intent.putExtra("url", productos.get(i).getUrl());
                startActivity(intent);
            }
        });

        // Cuando el usuario hace un LongClic (clic sin soltar elemento por mas de 2 segundos)
        // Es por que el usuario quiere eliminar el registro
        listaProductos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {

                // Preparando cuadro de dialogo para preguntar al usuario
                // Si esta seguro de eliminar o no el registro
                AlertDialog.Builder ad = new AlertDialog.Builder(Contenido.this);
                ad.setMessage("Está seguro de eliminar registro?")
                        .setTitle("Confirmación");

                ad.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        db.collection("Producto")
                                .document(productos.get(position).getKey())
                                .delete();
                        Toast.makeText(Contenido.this,
                                "Registro borrado!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getBaseContext(), Contenido.class);
                        startActivity(intent);
                    }
                });
                ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(Contenido.this,
                                "Operación de borrado cancelada!", Toast.LENGTH_SHORT).show();
                    }
                });

                ad.show();
                return true;
            }
        });


     fab_agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cuando el usuario quiere agregar un nuevo registro
                Intent i = new Intent(getBaseContext(), Registro_Menu.class);
                i.putExtra("accion","a"); // Agregar
                i.putExtra("key","");
                i.putExtra("nombre","");
                i.putExtra("desc","");
                i.putExtra("prec","");
                i.putExtra("esta","");
               // i.putExtra("url","");
                startActivity(i);
            }
        });



    }


    // Cambiarlo refProductos a consultaOrdenada para ordenar lista

   private void obtenerDatos () {
           db.collection("Producto")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            JSONParser parser = new JSONParser();
                            Gson gson = new Gson();
                            try {

                                JSONObject obj, aux;

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    pd=new Producto();
                                    actualDoc=document.getId();
                                 //Llenando el objeto con los datos obtenidos
                                  obj = (JSONObject) parser.parse(gson.toJson(document.getData()));
                                   pd.setKey(actualDoc);
                                   pd.setNomb(obj.get("Nombre").toString());
                                   pd.setDesc(obj.get("Descripcion").toString());
                                   pd.setEstado((Boolean.parseBoolean(obj.get("Estado").toString()))?"Habilitado":"Desabilitado");
                                 //  pd.setEsta(Boolean.parseBoolean(obj.get("Estado").toString()));
                                   pd.setPrec(Double.parseDouble( obj.get("Precio").toString()));
                                   pd.setUrl(obj.get("Url").toString());
                                    productos.add(pd);

                                }
                                ProductoAdapter adapter = new ProductoAdapter(Contenido.this,productos );
                                listaProductos.setAdapter(adapter);
                            } catch (Exception e) {
                                Log.d("exception", e.toString());
                            }

//                            Log.d("TAG", pd.toString());

                        }
                    }
                });

    }
    public void ticketExist(){
        //instancia a base de datos FireStore
        FirebaseFirestore mFirestore=FirebaseFirestore.getInstance();

        //instancia a autenticacion de Firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String loginUser= mAuth.getCurrentUser().getUid();
        Log.i("info","ha pasado por aca: "+loginUser);
        mFirestore.collection("tickets")
                .whereEqualTo("id_usuario",loginUser)
                .whereEqualTo("estado","Borrador")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful() && task.getResult().size()==0){
                            Log.i("info","ha pasado por aca, esta vacio: "+String.valueOf(task.getResult().size()));

                            Map<String, Object> data = new HashMap<>();
                            data.put("estado", "Borrador");
                            data.put("id_usuario", loginUser);
                            data.put("fechapago",null);
                            data.put("preciototal",0);
                            data.put("productos",0);

                            mFirestore.collection("tickets")
                                    .add(data)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Toast.makeText(getApplicationContext(),"Se ha creado el ticket, Puede Seguir comprando",Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(),"Hubo un pequeño problema",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
    }

}

