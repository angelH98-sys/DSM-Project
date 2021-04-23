package sv.edu.udb.dsm_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Carrito extends AppCompatActivity {

    private TextView prueba;
    FirebaseFirestore mFirestore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        mFirestore=FirebaseFirestore.getInstance();
        prueba=(TextView)findViewById(R.id.prueba);





        obtenerDatos();
    }

    private void obtenerDatos(){
        mFirestore.collection("tickets").document("9El7wXakxioCqYlPHZPY").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String estado = documentSnapshot.getString("estado");
                    String id_usuario = documentSnapshot.getString("id_usuario");

                    prueba.setText(estado+"    "+id_usuario);
                }
            }
        });
    }
}