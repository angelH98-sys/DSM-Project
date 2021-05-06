package sv.edu.udb.dsm_project;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import sv.edu.udb.dsm_project.Modelo.Producto;

public class ProductoAdapter extends ArrayAdapter<Producto> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    List<Producto> productos;
    private Activity context;

    public ProductoAdapter(@NonNull Activity context, @NonNull List<Producto> productos) {
        super(context, R.layout.producto_layout, productos);
        this.context = context;
        this.productos = productos;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        // Método invocado tantas veces como elementos tenga la coleccion personas
        // para formar a cada item que se visualizara en la lista personalizada
        LayoutInflater layoutInflater = context.getLayoutInflater();
        View rowview=null;
        // optimizando las diversas llamadas que se realizan a este método
        // pues a partir de la segunda llamada el objeto view ya viene formado
        // y no sera necesario hacer el proceso de "inflado" que conlleva tiempo y
        // desgaste de bateria del dispositivo
        if (view == null)
            rowview = layoutInflater.inflate(R.layout.producto_layout,null);
        else rowview = view;

        TextView Nomb= rowview.findViewById(R.id.lblNomb);
        TextView Desc = rowview.findViewById(R.id.lblDes);
        TextView Prec = rowview.findViewById(R.id.lblPre);
        TextView Esta = rowview.findViewById(R.id.lblEsta);

        Nomb.setText("Nombre : "+productos.get(position).getNomb());
        Desc.setText("Descripcion : " + productos.get(position).getDesc());
        Prec.setText("Precio : "+productos.get(position).getPrec());
        Esta.setText("Estado : " + productos.get(position).isEsta());


        return rowview;
    }
}
