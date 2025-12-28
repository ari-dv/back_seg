package Proyecto.MegaWeb2.__BackEnd.Dto;

import java.time.LocalDateTime;
import java.util.Date;

public class UsuarioDTO {
    private Integer id;
    private String nombres;
    private String username;
    private String apellidos;
    private String email;
    private int idRol;
    private Integer suscripcion;
    private String tema;
    private String jtable;
    private String imagen;
    private String passwordHash;
    private String passwordEncode;
    private Date expirationToken;
    private boolean twoFactorEnabled;
    private String secret2FA;
    private String dni;
    

    
    // 🔹 Agregamos el campo estado
    private int estado; // 0 = desactivado, 1 = activo

    // Getters y setters existentes...
    public boolean isTwoFactorEnabled() {
        return twoFactorEnabled;
    }
    public void setTwoFactorEnabled(boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }

    public String getSecret2FA() {
        return secret2FA;
    }
    public void setSecret2FA(String secret2FA) {
        this.secret2FA = secret2FA;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getRol() { return idRol; }
    public void setRol(int rol) { this.idRol = rol; }

    public int getSuscripcion() { return suscripcion; }
    public void setSuscripcion(Integer suscripcion) { this.suscripcion = suscripcion; }

    public String getTema() { return tema; }
    public void setTema(String tema) { this.tema = tema; }

    public String getJTable() { return jtable; }
    public void setJTable(String jtable) { this.jtable = jtable; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getPasswordEncode() { return passwordEncode; }
    public void setPasswordEncode(String passwordEncode) { this.passwordEncode = passwordEncode; }

    public Date getExpirationToken() { return expirationToken; }
    public void setExpirationToken(Date expirationToken) { this.expirationToken = expirationToken; }

    // 🔹 Getter y setter para estado
    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }
}
