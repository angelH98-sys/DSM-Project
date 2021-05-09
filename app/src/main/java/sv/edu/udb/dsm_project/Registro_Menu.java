package sv.edu.udb.dsm_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.DuplicateFormatFlagsException;
import java.util.HashMap;
import java.util.Map;

import sv.edu.udb.dsm_project.Modelo.Producto;

public class Registro_Menu extends AppCompatActivity {
    EditText Nomb,Descri,Precio,Url;
    CheckBox Esta;
    Button btnGua, btnMos;
    FirebaseFirestore db;
    Producto pd;
    String key="",nombre="",desc="",accion="",ur="",estado="";
    Double prec=0.0;
    Boolean estad=null;
    String TAG = "DocSnippets";
    Bundle datos;

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
                pd.setUrl(Url.getText().toString());
                CrearDoc();
                Limpiar();
            }
        });
        btnMos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), Contenido.class));
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
        Url = (EditText) findViewById(R.id.txtUrl);
        Nomb= (EditText) findViewById(R.id.txtNombre);
        Descri=(EditText)findViewById(R.id.txtDescripcion);
        Precio=(EditText)findViewById(R.id.txtPrecio);
        Esta= (CheckBox) findViewById(R.id.chkEsta);
        pd = new Producto();
        db= FirebaseFirestore.getInstance();
        datos = getIntent().getExtras();
        if(datos != null)
        {
            accion = datos.getString("accion");
             if(accion.equals("e"))
             {
                 editar();
             }
        }
       }


public void editar()
{
    key = datos.getString("key");
    nombre = datos.getString("nomb");
    prec = datos.getDouble("precio");
    desc = datos.getString("desc");
    estado =datos.getString("estado");
    estad = (estado.equals("Habilitado"))?true:false;
    ur=datos.getString("url");
    Esta.setChecked(estad);
    Nomb.setText(nombre);
    Descri.setText(desc);
    Precio.setText(prec.toString());
    Url.setText(ur);
}
    public void CrearDoc()
    {
        Map<String, Object> prod = new HashMap<String, Object>();
        prod.put("Nombre", pd.getNomb());
        prod.put("Precio", pd.getPrec());
        prod.put("Descripcion", pd.getDesc());
        prod.put("Estado",pd.isEsta());
        prod.put("Url",pd.getUrl());
            if(accion.equals("e"))
            {

                // Editando el registro
                db.collection("Producto")
                        .document(key)
                        .set(prod);
                Intent intent = new Intent(getBaseContext(), Contenido.class);
                startActivity(intent);
            }
            else
                {
                        // Add a new document with a generated ID
                        db.collection("Producto")
                              /*  .document("J2AjeN7pXdvpjC9ZHEtE")
                                .set(prod);*/
                               .add(prod)
                               .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        Log.d(TAG, "onComplete: ");
                                    }
                                });
                }

    }
}

