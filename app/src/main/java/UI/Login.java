package UI;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.lionpass.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.Executor;

public class Login extends AppCompatActivity {

    private EditText emailInputLayout, passwordInput;
    private Button loginButton, registerButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Definir los EditText y botones
        emailInputLayout = findViewById(R.id.emailInputLayout);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        // Botón para registrar
        registerButton.setOnClickListener(view -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });

        // Botón para iniciar sesión
        loginButton.setOnClickListener(view -> {
            String email = emailInputLayout.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            // Validar campos vacíos
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(Login.this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verificar credenciales con Firebase Authentication
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Usuario autenticado correctamente
                            authenticateUser();
                        } else {
                            Toast.makeText(Login.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void authenticateUser() {
        BiometricManager biometricManager = BiometricManager.from(this);

        int canAuthenticate = biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL
        );

        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            promptBiometricAuthentication();
        } else {
            promptKeyguardAuthentication();
        }
    }

    private void promptBiometricAuthentication() {
        Executor executor = ContextCompat.getMainExecutor(this);

        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(Login.this, "Autenticación exitosa", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Login.this,Home.class);
                startActivity(intent);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(Login.this, "Autenticación fallida", Toast.LENGTH_SHORT).show();
            }
        });

        // Crear el PromptInfo correctamente
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Autenticación biométrica")
                .setSubtitle("Por favor, autentícate para continuar")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void promptKeyguardAuthentication() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

        if (keyguardManager != null && keyguardManager.isKeyguardSecure()) {
            Intent intent = keyguardManager.createConfirmDeviceCredentialIntent("Autenticación requerida", "Por favor, verifica tu identidad.");
            startActivityForResult(intent, 123);
        } else {
            Toast.makeText(this, "Configuración de seguridad no disponible", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Autenticación exitosa", Toast.LENGTH_SHORT).show();
                navigateToHome();
            } else {
                Toast.makeText(this, "Autenticación fallida", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void navigateToHome() {
        Intent intent = new Intent(Login.this, Home.class); // Redirige al Home después de autenticarse
        startActivity(intent);
        finish();
    }
}
