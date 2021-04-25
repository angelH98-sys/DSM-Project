package sv.edu.udb.dsm_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.DecimalFormat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

public class Carrito extends AppCompatActivity {
    private LinearLayout padre;
    FirebaseFirestore mFirestore;
    private String loginUser;
    DecimalFormat df= new DecimalFormat("#.00");
    private List<productosTicket> productos = new ArrayList<productosTicket>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);
        //quemado
        loginUser="usuarioprueba123";
        mFirestore=FirebaseFirestore.getInstance();
        padre=(LinearLayout)findViewById(R.id.padre);

        obtenerDatos();
    }

    private void obtenerDatos(){
        mFirestore.collection("tickets").whereEqualTo("id_usuario",loginUser).whereEqualTo("estado","Borrador").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                    Log.d("TAG", "onComplete: ");
                    datos(padre);
                }
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
        for (int i=0;i<productos.size();i++){
            TextView nombre= new TextView(getApplicationContext());
            TextView cantidad= new TextView(getApplicationContext());
            TextView precio= new TextView(getApplicationContext());
            TextView subprecio= new TextView(getApplicationContext());
            TextView producto= new TextView(getApplicationContext());
            producto.setText("Producto "+(i+1));
            nombre.setText("Nombre: "+productos.get(i).nombre);
            cantidad.setText("Cantidad: "+(int)productos.get(i).cantidad);
            precio.setText("Precio $"+productos.get(i).precio.toString());
            subprecio.setText("Sub total $"+df.format(productos.get(i).precio*productos.get(i).cantidad)+"\n\n");
            total=total+productos.get(i).precio*productos.get(i).cantidad;
            padre.addView(producto);
            padre.addView(nombre);
            padre.addView(cantidad);
            padre.addView(precio);
            padre.addView(subprecio);

        }
        TextView totalcompra= new TextView(getApplicationContext());
        totalcompra.setText("Total de la compra = $"+df.format(total));
        Button realizarCompra = new Button(getApplicationContext());
        realizarCompra.setText("Realizar compra");
        padre.addView(totalcompra);
        padre.addView(realizarCompra);
    }




}

