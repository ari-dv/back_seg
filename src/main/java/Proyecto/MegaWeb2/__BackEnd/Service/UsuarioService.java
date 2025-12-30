package Proyecto.MegaWeb2.__BackEnd.Service;

import java.awt.Image;

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

import com.spire.pdf.PdfDocument;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import com.spire.pdf.PdfDocument;
import com.spire.pdf.graphics.PdfImage;
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
            u.setEstado(rs.getInt("estado")); // 🔹 AGREGAR ESTO
            return u;
        }
        return null;
    });

    if (user == null) return null;

    if (!passwordEncoder.matches(password, user.getPassword())) {
        return null;
    }

    UsuarioDTO dto = new UsuarioDTO();
    dto.setId(user.getId());
    dto.setNombres(user.getNombres());
    dto.setApellidos(user.getApellidos());
    dto.setEmail(user.getEmail());
    dto.setRol(user.getIdRol());
    dto.setEstado(user.getEstado()); // 🔹 AGREGAR ESTO
    dto.setTwoFactorEnabled(false);

    return dto;
}



    
public int crearUsuario(UsuarioCreateRequestDTO dto) {
    // 1️⃣ Hashear la contraseña
    dto.setPassword(passwordEncoder.encode(dto.getPassword()));

    // 2️⃣ Guardar usuario en BD usando tu repositorio
    int idUsuario = usuarioRepository.crearUsuario(dto);

    try {
        // ==================== Cargar PDF ====================
        PdfDocument pdf = new PdfDocument();
        pdf.loadFromFile("plantilla_terminos.pdf");

        // ==================== Agregar firma visible ====================
        byte[] decodedImg = Base64.getDecoder().decode(dto.getFirmaBase64().split(",")[1]);
        ByteArrayInputStream bais = new ByteArrayInputStream(decodedImg);
        PdfImage pdfImg = PdfImage.fromStream(bais);

        pdf.getPages().get(0).getCanvas().drawImage(pdfImg, 50, 100, 200, 100);

        // ==================== Guardar PDF final ====================
        String outputPdf = "usuario_" + idUsuario + "_firmado.pdf";
        pdf.saveToFile(outputPdf);
        pdf.close();

    } catch (Exception e) {
        e.printStackTrace();
    }

    return idUsuario;
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
public void updateSecret2FA(String email, String secret2FA) {
    usuarioRepository.updateSecret2FA(email, secret2FA);
}
// Verifica si el código ingresado coincide con el secret_2fa
public boolean verify2FACode(String codigoIngresado) {
    String sql = "SELECT COUNT(*) FROM users WHERE secret_2fa = ?"; 
    Integer count = jdbcTemplate.queryForObject(sql, new Object[]{codigoIngresado}, Integer.class);
    return count != null && count > 0;
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
