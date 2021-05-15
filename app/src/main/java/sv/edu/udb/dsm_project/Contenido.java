package sv.edu.udb.dsm_project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCanceledListener;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
    FirebaseAuth mAuth;

    boolean userIsAdmin;
    String ticketId;
    List<products> productosEnTicket;
    Double precioTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mostrar_activity);

        inicializar();
        obtenerDatos();
    }
    private void inicializar() {

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        fab_agregar = findViewById(R.id.fab_agregar);
        listaProductos = findViewById(R.id.ListaProductos);
        productos = new ArrayList<>();
        productosEnTicket = new ArrayList<>();

        checkUserType();
        if(userIsAdmin){
            setAdministratorActions();
        }else{
            setCustomerActions();
        }


    }

    private void checkUserType(){

        db.collection("usuarios")
                .whereEqualTo("uid", mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){

                            for (QueryDocumentSnapshot document: task.getResult()){
                                if(document.get("tipo").toString() == "Admin"){
                                    userIsAdmin = true;
                                }else{
                                    userIsAdmin = false;
                                }
                            }
                        }else{
                            userIsAdmin = false;
                        }
                    }
                });

        db.collection("tickets")
                .whereEqualTo("idusuario", mAuth.getCurrentUser().getUid().toString())
                .whereEqualTo("estado", "Borrador").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot document: task.getResult()){

                            ticketId = document.getId().toString();
                        }
                    }
                });
    }

    private void setAdministratorActions(){

        fab_agregar.setImageResource(R.drawable.add);

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

    private void setCustomerActions(){
        fab_agregar.setImageResource(R.drawable.cart);

        listaProductos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                products p = new products();
                p.cantidad = 1;
                p.id_producto = productos.get(i).getKey();
                p.nombre = productos.get(i).getNomb();
                p.precio = productos.get(i).getPrec();

                updateTicketInfo(p);

                try{
                    db.collection("tickets")
                            .document(ticketId)
                            .update("productos",  productosEnTicket,
                                    "preciototal", precioTotal)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(Contenido.this, "Producto añadido", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }catch(Exception e){
                    Log.d("return", e.getMessage());
                }


            }
        });

        fab_agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getBaseContext(), Carrito.class));
            }
        });
    }

    private void updateTicketInfo(products p){

        products aux;
        Double subtotal;
        int cantidad;

        if(productosEnTicket.isEmpty()){
            p.subTotal = p.cantidad * p.precio;
            productosEnTicket.add(p);
            precioTotal += p.subTotal;
            return;
        }

        for(int i = 0; i < productosEnTicket.size(); i++){
            aux = productosEnTicket.get(i);
            if(aux.id_producto.equals(p.id_producto)){

                cantidad = aux.cantidad += 1;
                subtotal = cantidad * p.precio;

                aux.cantidad = cantidad;
                aux.subTotal = subtotal;

                productosEnTicket.set(i, aux);
                precioTotal += p.precio;
                return;
            }
        }

        p.subTotal = p.cantidad * p.precio;
        productosEnTicket.add(p);
        precioTotal += p.subTotal;
        return;
    }


    // Cambiarlo refProductos a consultaOrdenada para ordenar lista

   private void obtenerDatos () {
       JSONParser parser = new JSONParser();
       Gson gson = new Gson();
           db.collection("Producto")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            try {

                                JSONObject obj;
                                Boolean status;
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    obj = (JSONObject) parser.parse(gson.toJson(document.getData()));
                                    status = (Boolean) obj.get("Estado");

                                    pd=new Producto();
                                    actualDoc=document.getId();
                                    //Llenando el objeto con los datos obtenidos
                                    pd.setKey(actualDoc);
                                    pd.setNomb(obj.get("Nombre").toString());
                                    pd.setDesc(obj.get("Descripcion").toString());
                                    pd.setEstado((Boolean.parseBoolean(obj.get("Estado").toString()))?"Habilitado":"Desabilitado");
                                    pd.setPrec(Double.parseDouble( obj.get("Precio").toString()));
                                    pd.setUrl(obj.get("Url").toString());

                                    if(status && !userIsAdmin){

                                        productos.add(pd);
                                    }else if(userIsAdmin){

                                        productos.add(pd);
                                    }

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

           db.collection("tickets")
                   .whereEqualTo("idusuario", mAuth.getCurrentUser().getUid().toString())
                   .whereEqualTo("estado", "Borrador").get()
                   .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                       @Override
                       public void onComplete(@NonNull Task<QuerySnapshot> task) {
                           JSONObject obj, aux;
                           JSONArray array;
                           products p;

                           for(QueryDocumentSnapshot document: task.getResult()){
                               try {
                                   obj = (JSONObject) parser.parse(gson.toJson(document.getData()));
                                   if(obj.get("preciototal").toString() == "0"){
                                       precioTotal = 0.0;
                                       return;
                                   }
                                   array = (JSONArray) obj.get("productos");
                                   precioTotal = (Double) obj.get("preciototal");

                                   for(int i = 0; i < array.size(); i++){
                                       aux = (JSONObject) array.get(i);
                                       p = new products();

                                       p.cantidad = Integer.parseInt(aux.get("cantidad").toString());
                                       p.id_producto = aux.get("id_producto").toString();
                                       p.nombre = aux.get("nombre").toString();
                                       p.precio = Double.parseDouble(aux.get("precio").toString());
                                       p.subTotal = Double.parseDouble(aux.get("subtotal").toString());
                                       productosEnTicket.add(p);
                                   }

                               } catch (ParseException e) {
                                   e.printStackTrace();
                               }
                           }
                       }
                   });

    }

    public class products{

        int cantidad;
        String id_producto;
        String nombre;
        Double precio;
        Double subTotal;

        public products(){
            this.cantidad = 0;
            this.id_producto = "";
            this.nombre = "";
            this.precio = 0.0;
            this.subTotal = 0.0;
        }

        public String getId_producto(){
            return id_producto;
        }
        public String getNombre(){
            return nombre;
        }
        public Double getPrecio(){
            return precio;
        }
        public Double getSubtotal(){
            return subTotal;
        }
        public int getCantidad(){
            return cantidad;
        }
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



