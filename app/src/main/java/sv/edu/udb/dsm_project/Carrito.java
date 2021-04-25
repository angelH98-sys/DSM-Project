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

import java.util.Date;
import java.util.List;

public class Carrito extends AppCompatActivity {

    private TextView prueba;
    FirebaseFirestore mFirestore;
    private String loginUser;



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
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String estado = document.getData().get("estado").toString();
                        Date fechapago = (Date)document.getData().get("fechapago");
                        String id_usuario = loginUser;
                        double preciototal = (Double) document.getData().get("preciototal");


                        /*Object[] productos = (Object[]) document.getData().get("productos");
                        List<productosTicket>  productosEnTicket;
                        for (Object o:productos){

                        }*/
                        String valor=document.getData().get("productos").toString();

                        Log.d("return", document.getData().get("productos").toString());
                        prueba.setText(estado + "    " + fechapago + "     " + id_usuario + "    " + preciototal+"\n"+document.getData().get("productos").toString()+"\n\n"+valor.substring(2));

                    }
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

        public void main(int cantidad,String id_producto,String nombre,Double precio,Double subtotal){
            this.cantidad=cantidad;
            this.id_producto=id_producto;
            this.nombre=nombre;
            this.precio=precio;
            this.subtotal=subtotal;
        }




    }

}

