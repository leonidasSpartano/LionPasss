package UI;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lionpass.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    private EditText fullNameInput, registerEmailInput, registerPasswordInput, confirmPasswordInput;
    private Button registerButton, backToLoginButton;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("User");

        // Definir los campos de texto y botones
        fullNameInput = findViewById(R.id.fullNameInput); // Campo para capturar el nombre
        registerEmailInput = findViewById(R.id.registerEmailInput);
        registerPasswordInput = findViewById(R.id.registerPasswordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        registerButton = findViewById(R.id.registerButton);
        backToLoginButton = findViewById(R.id.backToLoginButton);

        // Botón para regresar a Login
        backToLoginButton.setOnClickListener(view -> {
            Intent intent = new Intent(Register.this, Login.class);
            startActivity(intent);
        });

        // Botón para registrarse
        registerButton.setOnClickListener(view -> {
            String name = fullNameInput.getText().toString().trim();
            String email = registerEmailInput.getText().toString().trim();
            String password = registerPasswordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            // Validar campos vacíos
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(Register.this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar formato de correo electrónico
            if (!isValidEmail(email)) {
                Toast.makeText(Register.this, "Por favor ingresa un correo electrónico válido", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar que las contraseñas coincidan
            if (!password.equals(confirmPassword)) {
                Toast.makeText(Register.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crear el usuario en Firebase Authentication
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();

                            // Crear un mapa con los datos del usuario
                            HashMap<String, String> userData = new HashMap<>();
                            userData.put("name", name); // Agregar el nombre del usuario
                            userData.put("email", email); // Agregar el correo del usuario

                            // Guardar datos en Firebase Realtime Database
                            databaseReference.child(userId).setValue(userData)
                                    .addOnCompleteListener(dbTask -> {
                                        if (dbTask.isSuccessful()) {
                                            Toast.makeText(Register.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(Register.this, Login.class);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(Register.this, "Error al guardar en la base de datos", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(Register.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    // Método para validar el correo electrónico con regex
    private boolean isValidEmail(String email) {
        // Regex para validar correos electrónicos con cualquier dominio y extensión estándar
        String emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
