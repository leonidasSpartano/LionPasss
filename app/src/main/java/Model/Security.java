package Model;

public class Security {
    private String id;
    private String email;
    private String appName;
    private String userName;
    private String notes;
    private String password;

    // Constructor vacío requerido por Firebase
    public Security() {
    }

    public Security(String id, String email, String appName, String userName, String notes, String password) {
        this.id = id;
        this.email = email;
        this.appName = appName;
        this.userName = userName;
        this.notes = notes;
        this.password = password;
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
}
