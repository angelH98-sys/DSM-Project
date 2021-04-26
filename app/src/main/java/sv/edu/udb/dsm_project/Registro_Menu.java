package sv.edu.udb.dsm_project;

import android.content.Intent;
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

import sv.edu.udb.dsm_project.Modelo.Producto;

public class Registro_Menu extends AppCompatActivity {
    EditText Nomb,Descri,Precio;
    CheckBox Esta;
    Button btnGua, btnMos;
    FirebaseFirestore db;
    Producto pd;
    String TAG = "DocSnippets";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_activity);
        Iniciar();

        btnGua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setNomb(Nomb.getText().toString());
                pd.setPrec(Double.parseDouble(Precio.getText().toString()));
                pd.setDesc(Descri.getText().toString());
                pd.setEsta(Esta.isChecked());
                CrearDoc();
                Limpiar();
            }
        });
        btnMos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Registro_Menu.this, Contenido.class));
            }
        });
    }
    public void Limpiar()
    {
        Nomb.setText(null);
        Precio.setText(null);
        Descri.setText(null);
        Esta.setChecked(false);
    }
    public void Iniciar()
    {
        btnMos= (Button) findViewById(R.id.btnMost);
        btnGua = (Button) findViewById(R.id.btnGuar);
        Nomb= (EditText) findViewById(R.id.txtNombre);
        Descri=(EditText)findViewById(R.id.txtDescripcion);
        Precio=(EditText)findViewById(R.id.txtPrecio);
        Esta= (CheckBox) findViewById(R.id.chkEsta);
        pd = new Producto();
        db= FirebaseFirestore.getInstance();

    }

    public void CrearDoc()
    {
        Map<String, Object> prod = new HashMap<String, Object>();
        prod.put("Nombre", pd.getNomb());
        prod.put("Precio", pd.getPrec());
        prod.put("Descripcion", pd.getDesc());
        prod.put("Estado",pd.isEsta());

        // Add a new document with a generated ID
        db.collection("Producto")
                .add(prod)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Log.d(TAG, "onComplete: ");
                    }
                });
    }
}

