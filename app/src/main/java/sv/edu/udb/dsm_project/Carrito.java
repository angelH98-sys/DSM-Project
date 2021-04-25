package sv.edu.udb.dsm_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

public class Carrito extends AppCompatActivity {

    private TextView prueba;
    FirebaseFirestore mFirestore;
    private String loginUser;
    private List<productosTicket> productos = new ArrayList<productosTicket>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);
        //quemado
        loginUser="usuarioprueba123";
        mFirestore=FirebaseFirestore.getInstance();
        prueba=(TextView)findViewById(R.id.prueba);

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

}

