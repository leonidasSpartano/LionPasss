package UI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lionpass.R;

import java.util.List;

import Model.Security;

// Clase adaptador para usar con un RecyclerView que muestra una lista de objetos Security
public class SecurityAdapter extends RecyclerView.Adapter<SecurityAdapter.ViewHolder> {

    // Lista de objetos Security que se mostrarán en el RecyclerView
    private List<Security> securityList;

    // Listener para manejar eventos de clic en los elementos del RecyclerView
    private OnItemClickListener listener;

    // Constructor que recibe la lista de objetos Security a mostrar
    public SecurityAdapter(List<Security> securityList) {
        this.securityList = securityList;
    }

    // Interfaz para manejar eventos de clic en los elementos
    public interface OnItemClickListener {
        void onItemClick(Security security); // Método que se ejecutará cuando se haga clic en un elemento
    }

    // Método para establecer el listener para los clics en los elementos
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Crea una nueva vista para los elementos del RecyclerView
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el diseño del elemento (item_security_card) en el ViewGroup (RecyclerView)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_security_card, parent, false);
        return new ViewHolder(view); // Devuelve un ViewHolder que contiene la vista inflada
    }

    // Asocia los datos de un elemento de la lista (Security) a la vista (ViewHolder)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Security security = securityList.get(position); // Obtiene el objeto Security en la posición actual
        holder.bind(security, listener); // Vincula los datos del objeto a la vista y establece el listener
    }

    // Devuelve la cantidad de elementos en la lista
    @Override
    public int getItemCount() {
        return securityList.size();
    }

    // Clase ViewHolder que contiene las vistas de un elemento de la lista
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAppName, tvUserName; // Vistas para mostrar el nombre de la aplicación y el usuario

        // Constructor que recibe la vista de un elemento y busca las vistas específicas
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAppName = itemView.findViewById(R.id.tvAppName); // Asocia tvAppName al TextView correspondiente
            tvUserName = itemView.findViewById(R.id.tvUserName); // Asocia tvUserName al TextView correspondiente
        }

        // Método para vincular los datos de un objeto Security a las vistas y configurar el clic
        public void bind(Security security, OnItemClickListener listener) {
            // Establece el texto de las vistas con los datos del objeto Security
            tvAppName.setText(security.getAppName());
            tvUserName.setText(security.getUserName());

            // Configura un listener para el clic en todo el elemento
            itemView.setOnClickListener(v -> {
                if (listener != null) { // Verifica si hay un listener configurado
                    listener.onItemClick(security); // Llama al método onItemClick con el objeto Security
                }
            });
        }
    }
}
