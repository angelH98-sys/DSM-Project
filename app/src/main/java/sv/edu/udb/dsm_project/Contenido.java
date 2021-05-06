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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
                intent.putExtra("estado", productos.get(i).isEsta());
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
                i.putExtra("dui","");
                i.putExtra("peso","");
                i.putExtra("altura","");
                i.putExtra("fecha","");
                i.putExtra("genero","");
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
                                  obj = (JSONObject) parser.parse(gson.toJson(document.getData()));
                                   pd.setKey(actualDoc);
                                   pd.setNomb(obj.get("Nombre").toString());
                                   pd.setDesc(obj.get("Descripcion").toString());
                                   pd.setEsta(Boolean.parseBoolean(obj.get("Estado").toString()));
                                   pd.setPrec(Double.parseDouble( obj.get("Precio").toString()));
                                    productos.add(pd);
                                   //Agregar donde desea Nombre.setText(pd.getNomb());
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
}

