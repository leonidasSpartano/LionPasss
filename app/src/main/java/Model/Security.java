package Model;

public class Security {
    private String id;           // Identificador único para la entrada
    private String email;        // Correo asociado al registro
    private String appName;      // Nombre de la aplicación
    private String userName;     // Nombre de usuario asociado
    private String notes;        // Notas adicionales
    private String password;     // Contraseña encriptada asociada
    private String userEmail;    // Correo del usuario dueño del registro

    // Constructor vacío requerido por Firebase
    public Security() {
    }

    // Constructor completo para inicializar todos los campos
    public Security(String id, String email, String appName, String userName, String notes, String password, String userEmail) {
        this.id = id;
        this.email = email;
        this.appName = appName;
        this.userName = userName;
        this.notes = notes;
        this.password = password;
        this.userEmail = userEmail;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
