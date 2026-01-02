package Proyecto.MegaWeb2.__BackEnd.Repository;

import Proyecto.MegaWeb2.__BackEnd.Dto.AnadirComentarioDTO;
import Proyecto.MegaWeb2.__BackEnd.Dto.ComentarioResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ComentarioRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Listar comentarios, opcionalmente filtrado por idConsulta
    public List<ComentarioResponseDTO> listarComentarios() {
        String sql = "CALL sp_listar_comentarios()";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ComentarioResponseDTO dto = new ComentarioResponseDTO();
            dto.setIdComentario(rs.getInt("idcomentario"));
            dto.setComentario(rs.getString("comentario"));
            dto.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
            dto.setNombres(rs.getString("nombres"));
            dto.setApellidos(rs.getString("apellidos"));
            dto.setImagenUsuario(rs.getString("imagen_usuario"));
            dto.setNombreEmpresa(rs.getString("nombre_empresa"));
            return dto;
        });
    }

    // Añadir comentario
    public int registrarComentario(AnadirComentarioDTO dto, int idUsuario) {
        try {
            jdbcTemplate.update("CALL sp_insertar_comentario(?, ?, ?)",
                dto.getComentario(), idUsuario, dto.getIdConsulta());

            // Si no lanza excepción, retorna 1 para indicar éxito
            return 1;
        } catch (Exception e) {
            // Puedes imprimir o registrar el mensaje de error del SQL SIGNAL
            System.err.println("Error al registrar comentario: " + e.getMessage());
            return 0;
        }
    }

}
