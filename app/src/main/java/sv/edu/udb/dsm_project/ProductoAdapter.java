package sv.edu.udb.dsm_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import sv.edu.udb.dsm_project.Modelo.Producto;

public class ProductoAdapter extends FirestoreRecyclerAdapter<Producto,ProductoAdapter.ViewHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ProductoAdapter(@NonNull FirestoreRecyclerOptions<Producto> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Producto producto) {
        viewHolder.Nombre.setText(producto.getNomb());
        viewHolder.Precio.setText(producto.getPrec().toString());
        viewHolder.Descr.setText(producto.getDesc());
        viewHolder.Esta.setText((producto.isEsta())?"True":"False");

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contenido_activity, viewGroup,false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView  Nombre;
        TextView Precio;
        TextView Descr;
       CheckBox Esta;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Nombre = itemView.findViewById(R.id.lblNom2);
            Precio = itemView.findViewById(R.id.lblPrec2);
            Descr = itemView.findViewById(R.id.lblDesc2);
            Esta = itemView.findViewById(R.id.chkEsta2);

        }
    }
}
