package sv.edu.udb.dsm_project;

import android.R.layout;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sv.edu.udb.dsm_project.Modelo.Producto;

public class Contenido extends AppCompatActivity {
   RecyclerView recyclerViewProd;
   ProductoAdapter PAdapter;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mostrar_activity);
        recyclerViewProd=findViewById(R.id.recyclerProductos);
        recyclerViewProd.setLayoutManager(new LinearLayoutManager(this));
        db= FirebaseFirestore.getInstance();
        Query query= db.collection("Producto");
        FirestoreRecyclerOptions<Producto> lst = new FirestoreRecyclerOptions.Builder<Producto>().setQuery(query,Producto.class).build();
        PAdapter = new ProductoAdapter(lst);
        PAdapter.notifyDataSetChanged();
        recyclerViewProd.setAdapter(PAdapter);
     }

    @Override
    protected void onStart() {
        super.onStart();
        PAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        PAdapter.stopListening();
    }
}

