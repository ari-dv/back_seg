package Proyecto.MegaWeb2.__BackEnd.Controller;

import Proyecto.MegaWeb2.__BackEnd.Dto.AnadirComentarioDTO;
import Proyecto.MegaWeb2.__BackEnd.Dto.ComentarioResponseDTO;
import Proyecto.MegaWeb2.__BackEnd.Security.UsuarioSesionUtil;
import Proyecto.MegaWeb2.__BackEnd.Service.ComentarioService;
import Proyecto.MegaWeb2.__BackEnd.Service.PermisoRolModuloService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comentarios")
@Tag(name = "Comentarios", description = "Gestión de comentarios")
public class ComentarioController {

    @Autowired
    private ComentarioService comentarioService;

    @Autowired
    private PermisoRolModuloService permisoRolModuloService;

    private static final int ID_MODULO_COMENTARIOS = 11;

    @Operation(
        summary = "Listar comentarios publicados",
        description = "Lista todos los comentarios o solo los de una consulta específica si se proporciona idConsulta",
        parameters = @Parameter(name = "idConsulta", description = "ID de la consulta a filtrar (opcional)")
    )
    @GetMapping
    public ResponseEntity<List<ComentarioResponseDTO>> listar() {
        List<ComentarioResponseDTO> comentarios = comentarioService.listarComentarios();
        return ResponseEntity.ok(comentarios);
    }


    @Operation(summary = "Publicar un comentario (requiere autenticación y permiso de creación)")
    @PostMapping
    public ResponseEntity<?> registrarComentario(@RequestBody AnadirComentarioDTO dto) {
        Integer idUsuario = UsuarioSesionUtil.getIdUsuarioActual();
        Integer idRol = UsuarioSesionUtil.getIdRolActual();

        if (idUsuario == null || idRol == null) {
            return ResponseEntity.status(401).body("{\"error\": \"No autenticado\"}");
        }

        final int ID_MODULO_COMENTARIOS = 11;
        if (!permisoRolModuloService.tienePermiso(idRol, ID_MODULO_COMENTARIOS, "pCreate")) {
            return ResponseEntity.status(403).body("{\"error\": \"No tienes permiso para comentar\"}");
        }

        int result = comentarioService.registrarComentario(dto, idUsuario);
        if (result > 0) {
            return ResponseEntity.ok("{\"status\": \"Comentario registrado\"}");
        } else {
            return ResponseEntity.status(400).body("{\"error\": \"Usuario o consulta no válida\"}");
        }
    }

}
