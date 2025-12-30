package Proyecto.MegaWeb2.__BackEnd.Repository;

import Proyecto.MegaWeb2.__BackEnd.Dto.EditarUsuarioDTO;
import Proyecto.MegaWeb2.__BackEnd.Dto.ListarUsuarioDTO;
import Proyecto.MegaWeb2.__BackEnd.Dto.UsuarioCreateRequestDTO;
import Proyecto.MegaWeb2.__BackEnd.Dto.UsuarioDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;
@Repository
public class UsuarioRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

    // 🔹 BUSCAR USUARIO POR SECRET 2FA
    public UsuarioDTO findBySecret2FA(String secret2FA) {
        String sql = """
            SELECT id, nombres, apellidos, email, idRol, estado
            FROM users
            WHERE secret_2fa = ?
        """;

        return jdbcTemplate.query(sql, new Object[]{secret2FA}, rs -> {
            if (rs.next()) {
                UsuarioDTO dto = new UsuarioDTO();
                dto.setId(rs.getInt("id"));
                dto.setNombres(rs.getString("nombres"));
                dto.setApellidos(rs.getString("apellidos"));
                dto.setEmail(rs.getString("email"));
                dto.setRol(rs.getInt("idRol"));
                dto.setEstado(rs.getInt("estado"));
                return dto;
            }
            return null;
        });
    }

    // 🔹 LIMPIAR SECRET 2FA (recomendado)
    public void updateSecret2FA(String email, String secret2FA) {
        jdbcTemplate.update(
            "UPDATE users SET secret_2fa = ? WHERE email = ?",
            secret2FA, email
        );
    }

public int crearUsuario(UsuarioCreateRequestDTO dto) {
    // Solo 6 parámetros según el procedimiento almacenado
    String sql = "CALL sp_crear_usuario(?, ?, ?, ?, ?, ?,?)";
    Object[] params = new Object[]{
        dto.getNombres(),    // p_nombres
        dto.getApellidos(),  // p_apellidos
        dto.getPassword(),   // p_password
        dto.getEmail(),      // p_email
        dto.getDni(),        // p_dni
        dto.getIdRol(),      // p_idRol
        dto.getFirmaBase64() // p_firmaBase64
    };

    // Usamos update si no devuelve id, o query si tu procedimiento devuelve algo
    return jdbcTemplate.update(sql, params);
}

	public List<ListarUsuarioDTO> listarUsuarios(Integer id) {
		return jdbcTemplate.query(
				"CALL sp_listar_usuarios(?)",
				new Object[]{id},
				(rs, rowNum) -> {
					ListarUsuarioDTO u = new ListarUsuarioDTO();
					u.setId(rs.getInt("id"));
					u.setNombres(rs.getString("nombres"));
					u.setApellidos(rs.getString("apellidos"));
					u.setEmail(rs.getString("email"));
                    u.setDni(rs.getString("dni"));
					u.setSuscripcion(rs.getInt("suscripcion"));
					u.setTema(rs.getString("tema"));
					u.setJtable(rs.getString("jtable"));
					u.setImagen(rs.getString("imagen"));
					u.setIdRol(rs.getInt("idRol"));
					u.setCreatedAt(rs.getString("created_at"));
					u.setUpdatedAt(rs.getString("updated_at"));
					return u;
				}
		);
	}

	public List<ListarUsuarioDTO> listarUsuarioss() {
		return jdbcTemplate.query(
				"SELECT * FROM users",
				new Object[]{},
				(rs, rowNum) -> {
					ListarUsuarioDTO u = new ListarUsuarioDTO();
					u.setId(rs.getInt("id"));
					u.setNombres(rs.getString("nombres"));
					u.setApellidos(rs.getString("apellidos"));
					u.setEmail(rs.getString("email"));
					u.setSuscripcion(rs.getInt("suscripcion"));
					u.setTema(rs.getString("tema"));
                    u.setDni(rs.getString("dni"));

					u.setJtable(rs.getString("jtable"));
					u.setImagen(rs.getString("imagen"));
					u.setIdRol(rs.getInt("idRol"));
					u.setCreatedAt(rs.getString("created_at"));
					u.setUpdatedAt(rs.getString("updated_at"));
					return u;
				}
		);
	}

	public void editarUsuario(EditarUsuarioDTO dto) {
		String sql = "CALL sp_editar_usuario(?, ?, ?, ?)";
		jdbcTemplate.update(sql, dto.getId(), dto.getNombres(), dto.getApellidos(), dto.getEmail());
	}

	public void eliminarUsuario(Integer id) {
		jdbcTemplate.update("CALL sp_eliminar_usuario(?)", id);
	}

	/**
	 * Busca un usuario por su username
	 */
public UsuarioDTO findByUsername(String hashedUsername) {
    final String sql = """
        SELECT id, nombres, email, idRol, suscripcion, 
               tema, jtable, imagen, password AS passwordHash, expirationToken, two_factor_enabled, secret_2fa
        FROM users 
        WHERE username = ?
    """;

    try {
        return jdbcTemplate.queryForObject(sql, new Object[]{hashedUsername}, (rs, rowNum) -> {
            UsuarioDTO usuario = new UsuarioDTO();
            usuario.setId(rs.getInt("id"));
            usuario.setNombres(rs.getString("nombres"));
            usuario.setEmail(rs.getString("email"));
            usuario.setRol(rs.getInt("idRol"));
            usuario.setSuscripcion(rs.getInt("suscripcion"));
            usuario.setTema(rs.getString("tema"));
            usuario.setJTable(rs.getString("jtable"));
            usuario.setImagen(rs.getString("imagen"));
            usuario.setPasswordHash(rs.getString("passwordHash"));
            usuario.setExpirationToken(rs.getTimestamp("expirationToken"));
            usuario.setTwoFactorEnabled(rs.getBoolean("two_factor_enabled"));
            usuario.setSecret2FA(rs.getString("secret_2fa"));
            return usuario;
        });
    } catch (EmptyResultDataAccessException e) {
        return null;
    } catch (Exception e) {
        throw new RuntimeException("Error al buscar usuario por username", e);
    }
}



	public UsuarioDTO findByEmail(String email) {
    final String sql = """
        SELECT id, nombres, email, idRol, suscripcion, 
               tema, jtable, imagen, password AS passwordHash, expirationToken
        FROM users 
        WHERE email = ?
    """;

    try {
        return jdbcTemplate.queryForObject(sql, new Object[]{email}, (rs, rowNum) -> {
            UsuarioDTO usuario = new UsuarioDTO();
            usuario.setId(rs.getInt("id"));
            usuario.setNombres(rs.getString("nombres"));
            usuario.setEmail(rs.getString("email"));
            usuario.setRol(rs.getInt("idRol"));
            usuario.setSuscripcion(rs.getInt("suscripcion"));
            usuario.setTema(rs.getString("tema"));
            usuario.setJTable(rs.getString("jtable"));
            usuario.setImagen(rs.getString("imagen"));
            usuario.setPasswordHash(rs.getString("passwordHash"));
            Date timestamp = rs.getTimestamp("expirationToken");
            usuario.setExpirationToken(timestamp);
            return usuario;
        });
    } catch (EmptyResultDataAccessException e) {
        // No existe usuario con ese correo
        return null;
    } catch (Exception e) {
        throw new RuntimeException("Error al buscar usuario por email: " + email, e);
    }
}

	public void actualizarExpirationToken(String username, Date expirationToken) {
		String sql = "UPDATE users SET expirationToken = ? WHERE username = ?";

		jdbcTemplate.update(sql, expirationToken, username);
	}

public void actualizarPasswordPorEmail(String email, String password) {
    String sql = "UPDATE users SET password = ? WHERE email = ?";
    jdbcTemplate.update(sql, password, email);
}

	public void removeExpirationToken(String username) {
		String sql = "UPDATE users SET expirationToken = ? WHERE username = ?";

		jdbcTemplate.update(sql, null, username);
	}

	public void actualizarExpirationTokenWithUserId(Integer userId, Date expirationToken) {
		String sql = "UPDATE users SET expirationToken = ? WHERE id = ?";
		jdbcTemplate.update(sql, expirationToken, userId);
	}



@Transactional
public void updateTwoFactorEnabled(String username, boolean enabled) {
    String sql = "UPDATE users SET two_factor_enabled = ? WHERE username = ?";
    jdbcTemplate.update(sql, enabled, username);
}

}