package sv.edu.udb.dsm_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Text;

public class Carrito extends AppCompatActivity {
    private LinearLayout padre;
    FirebaseFirestore mFirestore;
    private String loginUser;
    DecimalFormat df= new DecimalFormat("#.00");
    private List<productosTicket> productos = new ArrayList<productosTicket>();
    private TextView totapPagoTV;
    private String actualDoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);
        //quemado
        loginUser="usuarioprueba123";
        mFirestore=FirebaseFirestore.getInstance();
        padre=(LinearLayout)findViewById(R.id.padre);
        totapPagoTV = findViewById(R.id.totalPagar);

        obtenerDatos();
    }

    private void obtenerDatos(){
        mFirestore.collection("tickets")
            .whereEqualTo("id_usuario",loginUser)
            .whereEqualTo("estado","Borrador")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    JSONParser parser = new JSONParser();
                    Gson gson = new Gson();
                    try{
                        JSONObject obj, aux;
                        JSONArray array;

                        int cantidad;
                        String id_producto, nombre;
                        Double precio, subtotal;
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            actualDoc = document.getId();

                            obj = (JSONObject) parser.parse(gson.toJson(document.getData()));
                            array = (JSONArray) obj.get("productos");
                            for (int i = 0; i <= array.size(); i++){
                                aux = (JSONObject) array.get(i);

                                cantidad = Integer.parseInt(aux.get("cantidad").toString());
                                id_producto = aux.get("id_producto").toString();
                                nombre = aux.get("nombre").toString();
                                precio = Double.parseDouble(aux.get("precio").toString());
                                subtotal = Double.parseDouble(aux.get("subtotal").toString());

                                productos.add(new productosTicket(cantidad, id_producto, nombre, precio, subtotal));
                            }
                        }

                    }catch (Exception e){
                        Log.d("exception", e.toString());
                    }
                    Log.d("TAG", productos.toString());
                    datos(padre);
                }
            }
        });
    }

    public void comprar(View view) {
        mFirestore.collection("tickets")
            .document(actualDoc).update(
                "estado","Cancelado",
                "fechapago", String.valueOf(new SimpleDateFormat("hh: mm: ss a dd-MMM-yyyy").format(new Date()))
        );

        Map<String, Object> data = new HashMap<>();
        data.put("estado", "Borrador");
        data.put("idusuario", loginUser);
        data.put("fechapago",null);
        data.put("preciototal",0);

        mFirestore.collection("tickets")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.i("info",documentReference.getId());
                        Toast.makeText(getApplicationContext(),"Compra realizada con exito",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Hubo un pequeño problema",Toast.LENGTH_SHORT).show();
                        Log.i("info",e.toString());
                    }
                });

    }

    public class productosTicket{
        int cantidad;
        String id_producto;
        String nombre;
        Double precio;
        Double subtotal;

        public productosTicket(int cantidad, String id_producto, String nombre, Double precio, Double subtotal) {
            this.cantidad=cantidad;
            this.id_producto=id_producto;
            this.nombre=nombre;
            this.precio=precio;
            this.subtotal=subtotal;
        }

    }


    public void datos(LinearLayout padre){
        double total=0;

//        LinearLayout.LayoutParams layParams = new LinearLayout.LayoutParams(width, height,weigth);

        for (int i=0;i<productos.size();i++){
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
            total+= productos.get(i).precio*productos.get(i).cantidad;
            detalles.addView(nombre);
            detalles.addView(precio);
            detalles.addView(subprecio);
            MoreLessButtons(acciones,(int)productos.get(i).cantidad,(Double)productos.get(i).precio,(Double) productos.get(i).subtotal,subprecio,total,totapPagoTV);
            hijo.addView(detalles);
            hijo.addView(acciones);
            padre.addView(hijo);

        }
        totapPagoTV.setText("Total de la compra = $"+df.format(total));
    }

    private void MoreLessButtons(LinearLayout v,int cant, Double precio, Double subtotal,TextView subtotalText,Double total,TextView totalText){
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
                masProducto(cant,precio,subtotal,cantidad,subtotalText,total,totalText);
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
                menosProducto(cant,precio,subtotal,cantidad,subtotalText,total,totalText);
            }
        });
        v.addView(less);

        Button eliminar = new Button(v.getContext());
        eliminar.setText("X");
        eliminar.setLayoutParams(new LinearLayout.LayoutParams(100,100));
        eliminar.setPadding(0,4,16,4);
        eliminar.setLeft(0);
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminar();
            }
        });
        v.addView(eliminar);


    }

    //acciones

    public void masProducto(int cantidad, Double precio, Double subtotal, TextView cantidadText, TextView subtotalTexto,Double total, TextView totalText){
        String texto= totalText.getText().toString();
        texto=texto.replace("Total de la compra = $","");
        total=Double.parseDouble(texto);
        total=total+precio;
        cantidad=Integer.parseInt(cantidadText.getText().toString())+1;
        subtotal=cantidad*precio;
        subtotalTexto.setText("Sub total $"+df.format(subtotal));
        cantidadText.setText(""+cantidad);
        totalText.setText("Total de la compra = $"+df.format(total));
    }

    public void menosProducto(int cantidad, Double precio, Double subtotal, TextView cantidadText, TextView subtotalTexto,Double total,TextView totalText){
        String texto= totalText.getText().toString();
        texto=texto.replace("Total de la compra = $","");
        total=Double.parseDouble(texto);
        total=total-precio;
        cantidad=Integer.parseInt(cantidadText.getText().toString())-1;
        if(cantidad>=0){
            subtotal=cantidad*precio;
            subtotalTexto.setText("Sub total $"+df.format(subtotal));
            cantidadText.setText(""+cantidad);
            totalText.setText("Total de la compra = $"+df.format(total));
        }

    }

    public void eliminar(){
        Toast.makeText(getApplicationContext(), "Se elimino la compra", Toast.LENGTH_LONG).show();
    }

}

