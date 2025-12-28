package Proyecto.MegaWeb2.__BackEnd.Service;

import Proyecto.MegaWeb2.__BackEnd.Model.User;
import Proyecto.MegaWeb2.__BackEnd.Dto.UsuarioCreateRequestDTO;
import Proyecto.MegaWeb2.__BackEnd.Dto.EditarUsuarioDTO;
import Proyecto.MegaWeb2.__BackEnd.Dto.ListarUsuarioDTO;
import Proyecto.MegaWeb2.__BackEnd.Dto.UsuarioDTO;
import Proyecto.MegaWeb2.__BackEnd.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private JdbcTemplate jdbcTemplate;  // 🔹 JdbcTemplate para consultas directas

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ================= LOGIN POR EMAIL =================
    public UsuarioDTO authenticateByEmail(String email, String password) {
        User user = jdbcTemplate.query("CALL sp_login(?)", new Object[]{email}, rs -> {
            if (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setNombres(rs.getString("nombres"));
                u.setApellidos(rs.getString("apellidos"));
                u.setPassword(rs.getString("password"));
                u.setEmail(rs.getString("email"));
                u.setIdRol(rs.getInt("idRol"));
                return u;
            }
            return null;
        });

        if (user == null) return null;

        // Verificar contraseña
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return null;
        }

        // Mapear a DTO
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(user.getId());
        dto.setNombres(user.getNombres());
        dto.setApellidos(user.getApellidos());
        dto.setEmail(user.getEmail());
        dto.setRol(user.getIdRol());
        dto.setTwoFactorEnabled(false); // Ajustar según lógica 2FA
        return dto;
    }

    // ================= CREAR USUARIO =================
    public int crearUsuario(UsuarioCreateRequestDTO dto) {
        // Hashear password
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        return usuarioRepository.crearUsuario(dto);
    }

    // ================= LISTAR USUARIOS =================
    public List<ListarUsuarioDTO> listarUsuarios(Integer id) {
        return usuarioRepository.listarUsuarios(id);
    }

    // ================= EDITAR USUARIO =================
    public void editarUsuario(EditarUsuarioDTO dto) {
        usuarioRepository.editarUsuario(dto);
    }

    // ================= ELIMINAR USUARIO =================
    public void eliminarUsuario(Integer id) {
        usuarioRepository.eliminarUsuario(id);
    }

    // ================= ACTUALIZAR CONTRASEÑA =================
    public void updatePassword(String email, String nuevaPassword) {
        String hashedPassword = passwordEncoder.encode(nuevaPassword);
        usuarioRepository.actualizarPasswordPorEmail(email, hashedPassword);
    }

    // ================= BUSQUEDAS =================
    public UsuarioDTO findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    // ================= 2FA =================
    public void updateSecret2FA(String email, String secret) {
        usuarioRepository.updateSecret2FA(email, secret);
    }

    public void updateTwoFactorEnabled(String email, boolean enabled) {
        usuarioRepository.updateTwoFactorEnabled(email, enabled);
    }

    // ================= TOKEN =================
    public void updateExpirationToken(String email, Date expirationToken) {
        usuarioRepository.actualizarExpirationToken(email, expirationToken);
    }

    public void removeExpirationToken(String email) {
        usuarioRepository.removeExpirationToken(email);
    }

    public void updateExpirationTokenWithUserId(Integer userId, Date expirationToken) {
        usuarioRepository.actualizarExpirationTokenWithUserId(userId, expirationToken);
    }
}
