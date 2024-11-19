package UI;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.lionpass.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import Model.Security;

public class AddPasswordDialogFragment extends DialogFragment {

    private EditText etEmail, etAppName, etUserName, etNotes, etPassword;
    private Button btnSave, btnCancel;

    private static final String SECRET_KEY = "1234567890123456"; // Clave AES (debe tener 16 caracteres)

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Inflar el diseño del diálogo
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_password, null);

        // Inicializar vistas
        etEmail = view.findViewById(R.id.etEmail);
        etAppName = view.findViewById(R.id.etAppName);
        etUserName = view.findViewById(R.id.etUserName);
        etNotes = view.findViewById(R.id.etNotes);
        etPassword = view.findViewById(R.id.etPassword);
        btnSave = view.findViewById(R.id.btnSave);
        btnCancel = view.findViewById(R.id.btnCancel);

        // Configurar listeners para los botones
        btnSave.setOnClickListener(v -> savePasswordToFirebase());
        btnCancel.setOnClickListener(v -> dismiss());

        // Crear y retornar el diálogo utilizando AlertDialog.Builder
        return new AlertDialog.Builder(requireContext())
                .setView(view)
                .create();
    }

    private void savePasswordToFirebase() {
        // Obtener los datos del formulario
        String mail = etEmail.getText().toString().trim();
        String nameApp = etAppName.getText().toString().trim();
        String nameUser = etUserName.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validar los campos obligatorios
        if (mail.isEmpty() || nameApp.isEmpty() || nameUser.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, completa todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener el correo del usuario logueado
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userEmail = auth.getCurrentUser() != null ? auth.getCurrentUser().getEmail() : null;

        if (userEmail == null) {
            Toast.makeText(getContext(), "No se pudo obtener el correo del usuario logueado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Encriptar la contraseña
        String encryptedPassword;
        try {
            encryptedPassword = encrypt(password, SECRET_KEY);
        } catch (GeneralSecurityException e) {
            Toast.makeText(getContext(), "Error al encriptar la contraseña", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return;
        }

        // Guardar en Firebase Realtime Database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Security");
        String id = ref.push().getKey();

        if (id != null) {
            // Crear el objeto Security con el correo del usuario logueado
            Security security = new Security(id, mail, nameApp, nameUser, notes, encryptedPassword,userEmail);
            security.setUserEmail(userEmail); // Asignar el correo del usuario logueado

            ref.child(id).setValue(security)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Contraseña guardada con éxito", Toast.LENGTH_SHORT).show();
                            dismiss(); // Cerrar el diálogo
                        } else {
                            Toast.makeText(getContext(), "Error al guardar en Firebase", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Error al generar el ID", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Método para encriptar un texto usando AES
     *
     * @param data      Texto a encriptar
     * @param secretKey Clave secreta de 16 caracteres
     * @return Texto encriptado en Base64
     * @throws GeneralSecurityException Si ocurre un error de cifrado
     */
    private String encrypt(String data, String secretKey) throws GeneralSecurityException {
        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
    }
}
