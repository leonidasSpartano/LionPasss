package UI;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lionpass.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Model.Security;

public class Home extends AppCompatActivity {

    private Button btnAddPassword;
    private Button btnLogout;
    private RecyclerView recyclerPasswords;
    private SecurityAdapter adapter;
    private List<Security> securityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        // Inicializar vistas
        btnAddPassword = findViewById(R.id.btnAddPassword);
        btnLogout = findViewById(R.id.btnLogout);
        recyclerPasswords = findViewById(R.id.recyclerPasswords);

        // Configurar RecyclerView
        recyclerPasswords.setLayoutManager(new LinearLayoutManager(this));
        securityList = new ArrayList<>();
        adapter = new SecurityAdapter(securityList);
        recyclerPasswords.setAdapter(adapter);

        // Configurar listeners para botones
        btnAddPassword.setOnClickListener(v -> openAddPasswordDialog());
        btnLogout.setOnClickListener(v -> logoutUser());

        // Cargar datos desde Firebase
        loadPasswordsFromFirebase();
    }

    private void openAddPasswordDialog() {
        AddPasswordDialogFragment dialog = new AddPasswordDialogFragment();
        dialog.show(getSupportFragmentManager(), "AddPasswordDialog");
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(Home.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void loadPasswordsFromFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Security");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                securityList.clear(); // Limpiar lista para evitar duplicados
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Security security = dataSnapshot.getValue(Security.class);
                    if (security != null) {
                        securityList.add(security);
                    }
                }
                adapter.notifyDataSetChanged(); // Notificar al adaptador que los datos cambiaron
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar errores
            }
        });
    }
}
