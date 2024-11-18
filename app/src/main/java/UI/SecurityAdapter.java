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

public class SecurityAdapter extends RecyclerView.Adapter<SecurityAdapter.SecurityViewHolder> {

    private List<Security> securityList;

    public SecurityAdapter(List<Security> securityList) {
        this.securityList = securityList;
    }

    @NonNull
    @Override
    public SecurityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_security, parent, false);
        return new SecurityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SecurityViewHolder holder, int position) {
        Security security = securityList.get(position);
        holder.cardNameApp.setText(security.getAppName());
        holder.cardNameUser.setText(security.getUserName());
    }

    @Override
    public int getItemCount() {
        return securityList.size();
    }

    public static class SecurityViewHolder extends RecyclerView.ViewHolder {
        TextView cardNameApp, cardNameUser;

        public SecurityViewHolder(@NonNull View itemView) {
            super(itemView);
            cardNameApp = itemView.findViewById(R.id.cardNameApp);
            cardNameUser = itemView.findViewById(R.id.cardNameUser);
        }
    }
}
