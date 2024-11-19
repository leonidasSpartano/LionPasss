package UI;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
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

        // Listener para abrir detalles con autenticación
        adapter.setOnItemClickListener(security -> authenticateAndOpenDetails(security));

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
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (userEmail == null) return;

        ref.orderByChild("userEmail").equalTo(userEmail)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        securityList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            try {
                                Security security = dataSnapshot.getValue(Security.class);
                                if (security != null) {
                                    securityList.add(security);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.err.println("Error al cargar datos: " + error.getMessage());
                    }
                });
    }

    private void authenticateAndOpenDetails(Security security) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            BiometricManager biometricManager = BiometricManager.from(this);
            if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK
                    | BiometricManager.Authenticators.DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS) {

                BiometricPrompt biometricPrompt = new BiometricPrompt(this,
                        ContextCompat.getMainExecutor(this),
                        new BiometricPrompt.AuthenticationCallback() {
                            @Override
                            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                                super.onAuthenticationSucceeded(result);
                                openDetails(security);
                            }

                            @Override
                            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                                super.onAuthenticationError(errorCode, errString);
                                Toast.makeText(Home.this, "Autenticación fallida: " + errString, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onAuthenticationFailed() {
                                super.onAuthenticationFailed();
                                Toast.makeText(Home.this, "Autenticación fallida. Intenta nuevamente.", Toast.LENGTH_SHORT).show();
                            }
                        });

                BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Autenticación requerida")
                        .setSubtitle("Usa tu huella digital o credenciales del dispositivo")
                        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK
                                | BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                        .build();

                biometricPrompt.authenticate(promptInfo);
            } else {
                // Si la autenticación biométrica no está disponible, usar KeyguardManager
                useKeyguard(security);
            }
        } else {
            Toast.makeText(this, "Tu dispositivo no es compatible con autenticación biométrica.", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void useKeyguard(Security security) {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (keyguardManager.isDeviceSecure()) {
            Intent intent = keyguardManager.createConfirmDeviceCredentialIntent("Autenticación requerida", "Verifica tu identidad para continuar.");
            startActivityForResult(intent, 1); // Código de solicitud
        } else {
            Toast.makeText(this, "Configura un bloqueo de pantalla para mayor seguridad.", Toast.LENGTH_SHORT).show();
        }
    }

    private void openDetails(Security security) {
        Intent intent = new Intent(Home.this, SecurityDetailsActivity.class);
        intent.putExtra("SECURITY_ID", security.getId());
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // Usuario autenticado correctamente
                Toast.makeText(this, "Autenticación exitosa.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Autenticación fallida.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
