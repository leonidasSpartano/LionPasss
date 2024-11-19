package UI;

import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lionpass.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class SecurityDetailsActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etAppName, etUserName, etNotes, etPassword;
    private MaterialButton btnEdit, btnDelete, btnCancel;
    private String securityId;
    private boolean isEditable = false;

    private final String secretKey = "1234567890123456"; // Clave de cifrado AES

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.security_detail_dialogf_ragment);

        // Inicializar vistas
        etEmail = findViewById(R.id.etEmail);
        etAppName = findViewById(R.id.etAppName);
        etUserName = findViewById(R.id.etUserName);
        etNotes = findViewById(R.id.etNotes);
        etPassword = findViewById(R.id.etPassword);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnCancel = findViewById(R.id.btnCancel);

        // Obtener el ID del registro desde el intent
        securityId = getIntent().getStringExtra("SECURITY_ID");
        if (securityId == null) {
            Toast.makeText(this, "ID inválido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Cargar los datos desde Firebase
        loadSecurityData();

        // Configurar botones
        btnEdit.setOnClickListener(v -> toggleEditMode());
        btnDelete.setOnClickListener(v -> deleteSecurity());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void loadSecurityData() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Security").child(securityId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    etEmail.setText(snapshot.child("email").getValue(String.class));
                    etAppName.setText(snapshot.child("appName").getValue(String.class));
                    etUserName.setText(snapshot.child("userName").getValue(String.class));
                    etNotes.setText(snapshot.child("notes").getValue(String.class));

                    // Desencriptar la contraseña antes de mostrarla
                    String encryptedPassword = snapshot.child("password").getValue(String.class);
                    try {
                        System.out.println("Encrypted Password: " + encryptedPassword);
                        String decryptedPassword = decrypt(encryptedPassword, secretKey);
                        etPassword.setText(decryptedPassword);
                    } catch (IllegalArgumentException e) {
                        Toast.makeText(SecurityDetailsActivity.this, "Error: Texto encriptado inválido", Toast.LENGTH_SHORT).show();
                    } catch (GeneralSecurityException e) {
                        Toast.makeText(SecurityDetailsActivity.this, "Error al desencriptar la contraseña", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SecurityDetailsActivity.this, "Registro no encontrado", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SecurityDetailsActivity.this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleEditMode() {
        isEditable = !isEditable;

        // Activar o desactivar los campos para edición
        etAppName.setEnabled(isEditable);
        etUserName.setEnabled(isEditable);
        etNotes.setEnabled(isEditable);
        etPassword.setEnabled(isEditable);

        if (isEditable) {
            btnEdit.setText("Guardar");
        } else {
            updateSecurity();
            btnEdit.setText("Editar");
        }
    }

    private void updateSecurity() {
        // Validar los datos antes de actualizar
        String email = etEmail.getText().toString().trim();
        String appName = etAppName.getText().toString().trim();
        String userName = etUserName.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || appName.isEmpty() || userName.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Encriptar la contraseña antes de actualizarla en Firebase
            String encryptedPassword = encrypt(password, secretKey);

            // Actualizar los datos en Firebase
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Security").child(securityId);
            ref.child("email").setValue(email);
            ref.child("appName").setValue(appName);
            ref.child("userName").setValue(userName);
            ref.child("notes").setValue(notes);
            ref.child("password").setValue(encryptedPassword)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Actualizado correctamente", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show());
        } catch (GeneralSecurityException e) {
            Toast.makeText(this, "Error al encriptar la contraseña", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteSecurity() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Security").child(securityId);
        ref.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Eliminado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show());
    }

    // Método para encriptar
    private String encrypt(String data, String secretKey) throws GeneralSecurityException {
        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
    }

    // Método para desencriptar
    private String decrypt(String encryptedData, String secretKey) throws GeneralSecurityException {
        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedBytes = Base64.decode(encryptedData, Base64.DEFAULT);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }
}
