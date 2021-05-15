package sv.edu.udb.dsm_project;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
//import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Carrito extends AppCompatActivity {
    private LinearLayout padre;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    private String loginUser;
    DecimalFormat df= new DecimalFormat("#.00");
    private List<productosTicket> productos = new ArrayList<productosTicket>();
    private TextView totapPagoTV;
    private String actualDoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);
        padre=(LinearLayout)findViewById(R.id.padre);
        totapPagoTV = findViewById(R.id.totalPagar);
        mFirestore=FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        loginUser= mAuth.getCurrentUser().getUid();//Con esto acceden al UID de la tabla de authenticaci&oacute;n de firebase

        Log.i("Usuario",loginUser);

        mFirestore.collection("tickets")
            .whereEqualTo("idusuario",loginUser)
            .whereEqualTo("estado","Borrador")
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshot,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w("TAG", "Listen failed.", e);
                        return;
                    }
//                    for (QueryDocumentSnapshot doc : snapshot)
                    if(snapshot.isEmpty())
//                        creaTicket("Se ha creado el ticket, Puede Seguir comprando");
                        Log.i("Usuario","no hay ticket");
                    else
                        prepararDatos(snapshot.getDocuments().get(0));
//                        Log.w("TAG", "Data => ." + doc.toString());

                }
            });

    }

    private void creaTicket(String msg) {
        Map<String, Object> data = new HashMap<>();
        data.put("estado", "Borrador");
        data.put("id_usuario", loginUser);
        data.put("fechapago",null);
        data.put("preciototal",0);
        data.put("productos",null);

        mFirestore.collection("tickets")
            .add(data)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"Hubo un pequeño problema",Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void prepararDatos(DocumentSnapshot document){
//        mFirestore.collection("tickets")
//            .whereEqualTo("id_usuario",loginUser)
//            .whereEqualTo("estado","Borrador")
//            .get()
//            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {

                    try{
                        productos.clear();
                        JSONParser parser = new JSONParser();
                        Gson gson = new Gson();
                        JSONObject obj, aux;
                        JSONArray array;

                        int cantidad;
                        String id_producto, nombre;
                        Double precio, subtotal;
//                        for (QueryDocumentSnapshot document : task.getResult()) {

                            actualDoc = document.getId();

                            obj = (JSONObject) parser.parse(gson.toJson(document.getData()));
                            array = (JSONArray) obj.get("productos");
                            Log.w("TAG", "Data => " + array);
                            for (int i = 0; i < array.size(); i++){
                                aux = (JSONObject) array.get(i);

                                cantidad = Integer.parseInt(aux.get("cantidad").toString());
                                id_producto = aux.get("id_producto").toString();
                                nombre = aux.get("nombre").toString();
                                precio = Double.parseDouble(aux.get("precio").toString());
                                subtotal = Double.parseDouble(aux.get("subtotal").toString());

                                productos.add(new productosTicket(cantidad, id_producto, nombre, precio, subtotal));
                            }
//                        }

                    }catch (Exception e){
                        Log.d("exception", e.toString());
                    }
//                    Log.d("TAG", productos.toString());
                    Render(padre);
//                }
//            }
//        });
    }

    public void comprar(View view) {
        mFirestore.collection("tickets")
            .document(actualDoc).update(
                "estado","Cancelado",
                "fechapago", String.valueOf(new SimpleDateFormat("hh: mm: ss a dd-MMM-yyyy").format(new Date()))
        );
        creaTicket("Compra realizada con exito");

    }

    public class productosTicket{
        int cantidad;
        String id_producto;
        String nombre;
        Double precio;
        Double subtotal;

        public productosTicket(){}

        public productosTicket(int cantidad, String id_producto, String nombre, Double precio, Double subtotal) {
            this.cantidad=cantidad;
            this.id_producto=id_producto;
            this.nombre=nombre;
            this.precio=precio;
            this.subtotal=subtotal;
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
            return subtotal;
        }
        public int getCantidad(){
            return cantidad;
        }

    }


    public void Render(LinearLayout padre){
        padre.removeAllViews();

        double totalTicket =0;

//        LinearLayout.LayoutParams layParams = new LinearLayout.LayoutParams(width, height,weigth);

        for (int i=0;i<productos.size();i++){

            Log.i("prods",productos.get(i).toString());
            //layouts para organizar la info
            LinearLayout hijo = new LinearLayout(padre.getContext());
            hijo.setPadding(20,5,5,5);
            hijo.setOrientation(LinearLayout.HORIZONTAL);
            hijo.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            //detalle de la info
            LinearLayout detalles = new LinearLayout(padre.getContext());
            detalles.setLayoutParams(new LinearLayout.LayoutParams(890, LinearLayout.LayoutParams.MATCH_PARENT));
            detalles.setOrientation(LinearLayout.VERTICAL);

            //agregar o quitar cantidad
            LinearLayout acciones = new LinearLayout(padre.getContext());
            acciones.setLayoutParams(new LinearLayout.LayoutParams(110,LinearLayout.LayoutParams.MATCH_PARENT));
            acciones.setOrientation(LinearLayout.VERTICAL);

            //textView que contiene la info
            TextView nombre= new TextView(detalles.getContext());
            TextView precio= new TextView(detalles.getContext());
            TextView subprecio= new TextView(detalles.getContext());
            nombre.setText("Nombre: "+productos.get(i).nombre);
            precio.setText("Precio $"+productos.get(i).precio.toString());
            subprecio.setText("Sub total $"+df.format(productos.get(i).precio*productos.get(i).cantidad));
            totalTicket += productos.get(i).precio*productos.get(i).cantidad;
            detalles.addView(nombre);
            detalles.addView(precio);
            detalles.addView(subprecio);
            int pos = i;
            MoreLessButtons(acciones,(int)productos.get(i).cantidad,productos.get(i).id_producto, pos);
//            MoreLessButtons(acciones,(int)productos.get(i).cantidad,(Double)productos.get(i).precio,(Double) productos.get(i).subtotal,subprecio, totalTicket,totapPagoTV);
            hijo.addView(detalles);
            hijo.addView(acciones);

            Button eliminar = new Button(padre.getContext());
            eliminar.setText("X");
            eliminar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
            eliminar.setPadding(0,4,16,4);
            eliminar.setLeft(0);
            eliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eliminarItem(pos);
                }
            });
            hijo.addView(eliminar);
            padre.addView(hijo);

        }
        totapPagoTV.setText("Total de la compra = $"+df.format(totalTicket));
    }

//    private void MoreLessButtons(LinearLayout v,int cant, Double precio, Double subtotal,TextView subtotalText,Double total,TextView totalText){
    private void MoreLessButtons(LinearLayout v,int cant,String id, int pos){
        TextView cantidad = new TextView(v.getContext());
        cantidad.setText(String.valueOf(cant));
        Button add = new Button(v.getContext());
        add.setCompoundDrawablesWithIntrinsicBounds(R.drawable.more_one,0,0,0);
        add.setLayoutParams(new LinearLayout.LayoutParams(100,100));
        add.setPadding(0,4,16,4);
        add.setLeft(0);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                masProducto(id);
            }
        });
        v.addView(add);

        v.addView(cantidad);
        Button less = new Button(v.getContext());
        less.setCompoundDrawablesWithIntrinsicBounds(R.drawable.less_one,0,0,0);
        less.setLayoutParams(new LinearLayout.LayoutParams(100,100));
        less.setPadding(0,4,16,4);
        less.setLeft(0);
        less.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menosProducto(id,pos);
            }
        });
        v.addView(less);
        

    }

    //acciones
      public void masProducto(String id){
        for(productosTicket a:productos)
          if(a.id_producto == id){
              a.cantidad+=1;
              a.subtotal = a.cantidad*a.precio;
              mFirestore.collection("tickets")
                  .document(actualDoc)
                  .update("productos", productos);
          }
    }


    public void menosProducto(String id, int pos){
        for(productosTicket a:productos)
            if(a.id_producto == id){
                a.cantidad-=1;
                if(a.cantidad>0){
                    a.subtotal = a.cantidad*a.precio;
                    mFirestore.collection("tickets")
                            .document(actualDoc)
                            .update("productos", productos);
                }
                else eliminarItem(pos);
            }
    }

    public void eliminarItem(int pos){
        new AlertDialog.Builder(padre.getContext())
            .setTitle("Eliminar?"+String.valueOf(productos.get(pos)))
            .setMessage("Seguro que desea eliminar")
            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mFirestore.collection("tickets")
                        .document(actualDoc)
                        .update("productos",FieldValue.arrayRemove(productos.get(pos)))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Se elimino la compra", Toast.LENGTH_LONG).show();
                            }
                        });
                }
            })
            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).create().show();

    }

}

