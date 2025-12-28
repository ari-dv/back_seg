package Proyecto.MegaWeb2.__BackEnd.Model;

import com.google.api.client.util.DateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class User {
	  @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String nombres;
	private String apellidos;
	private String password;
	private String email;
	private int idRol;
	private String imagen;
	private DateTime expirationToken;
	@Column(name = "two_factor_enabled")
    private boolean twoFactorEnabled = false;
	@Column(name = "secret_2fa")
    private String secret2FA;

	  // Getters y setters
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}


	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getIdRol() {
		return idRol;
	}

	public void setIdRol(int idRol) {
		this.idRol = idRol;
	}

	public String getImagen() {
		return imagen;
	}

	public void setImagen(String imagen) {
		this.imagen = imagen;
	}

	public DateTime getExpirationToken() {
		return expirationToken;
	}

	public void setExpirationToken(DateTime expirationToken) {
		this.expirationToken = expirationToken;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", nombres=" + nombres + ", apellidos=" + apellidos + ", password=" + password + ", email=" + email + ", idRol=" + idRol + ", imagen=" + imagen + "]";
	}

}
