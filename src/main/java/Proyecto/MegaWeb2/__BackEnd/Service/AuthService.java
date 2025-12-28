package Proyecto.MegaWeb2.__BackEnd.Service;

import org.springframework.stereotype.Service;

import Proyecto.MegaWeb2.__BackEnd.Model.User;
import Proyecto.MegaWeb2.__BackEnd.Security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class AuthService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    public String login(String username, String password) {
        User user = jdbcTemplate.query("CALL sp_login(?)", new Object[]{username}, rs -> {
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


        if (!new BCryptPasswordEncoder().matches(password, user.getPassword())) {
            return null;
        }

        return jwtUtil.generateToken(user);
    }
}
