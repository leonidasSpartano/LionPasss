package UI;

import android.app.Dialog;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Model.Security;

public class AddPasswordDialogFragment extends DialogFragment {

    private EditText etEmail, etAppName, etUserName, etNotes, etPassword;
    private Button btnSave, btnCancel;

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

        // Guardar en Firebase Realtime Database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Security");
        String id = ref.push().getKey();

        if (id != null) {
            Security security = new Security(id, mail, nameApp, nameUser, notes, password);

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
}
