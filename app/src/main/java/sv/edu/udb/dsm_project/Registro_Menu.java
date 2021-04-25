package sv.edu.udb.dsm_project;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Registro_Menu extends AppCompatActivity {

    EditText Nomb,Descri,Precio;
    CheckBox Esta;
    Button btnGua;
    FirebaseFirestore db;
    private static final String TAG = "DocSnippets";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Iniciar();

    }
    public void Iniciar()
    {
        btnGua = (Button) findViewById(R.id.btnSend);
        Nomb= findViewById(R.id.txtNombre);
        Descri=findViewById(R.id.txtDescripcion);
        Precio=findViewById(R.id.txtPrecio);
        Esta=findViewById(R.id.chkEsta);

        db= FirebaseFirestore.getInstance();
        CrearDoc();
    }

    public void CrearDoc()
    {
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<String, Object>();
        user.put("first", "Ada");
        user.put("last", "Lovelace");
        user.put("born", 1815);

        // Add a new document with a generated ID
        db.collection("Prueba")
                .add(user)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Log.d(TAG, "onComplete: ");
                    }
                });
    }
}

