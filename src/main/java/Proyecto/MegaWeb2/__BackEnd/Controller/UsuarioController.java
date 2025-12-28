package Proyecto.MegaWeb2.__BackEnd.Controller;

import Proyecto.MegaWeb2.__BackEnd.Dto.UsuarioCreateRequestDTO;
import Proyecto.MegaWeb2.__BackEnd.Dto.EditarUsuarioDTO;
import Proyecto.MegaWeb2.__BackEnd.Dto.ListarUsuarioDTO;
import Proyecto.MegaWeb2.__BackEnd.Service.UsuarioService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/usuarios")
public class UsuarioController {

	private final UsuarioService usuarioService;

	@Autowired
	public UsuarioController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	/**
	 * Crea un nuevo usuario a partir del JSON recibido en el cuerpo y
	 * devuelve un mapa con el id generado.
	 */
@PostMapping
public ResponseEntity<Map<String, Integer>> crearUsuario(
        @Valid @RequestBody UsuarioCreateRequestDTO dto
) {
    int idUsuario = usuarioService.crearUsuario(dto);
    if (idUsuario > 0) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("idUsuario", idUsuario));
    } else {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", -1));
    }
}

	/**
	 * Lista todos los usuarios.
	 */
	@GetMapping
	public ResponseEntity<List<ListarUsuarioDTO>> listarUsuarios() {
		List<ListarUsuarioDTO> lista = usuarioService.listarUsuarios(null);
		return ResponseEntity.ok(lista);
	}

	/**
	 * Obtiene un usuario por su ID.
	 */
	@GetMapping("/{id}")
	public ResponseEntity<ListarUsuarioDTO> obtenerUsuario(
			@PathVariable Integer id
	) {
		List<ListarUsuarioDTO> lista = usuarioService.listarUsuarios(id);
		if (lista.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(lista.get(0));
	}

	/**
	 * Actualiza un usuario existente.
	 */
	@PutMapping
	public ResponseEntity<Void> editarUsuario(
			@RequestBody EditarUsuarioDTO dto
	) {
		usuarioService.editarUsuario(dto);
		return ResponseEntity.noContent().build();
	}

	/**
	 * Elimina un usuario por su ID.
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminarUsuario(
			@PathVariable Integer id
	) {
		usuarioService.eliminarUsuario(id);
		return ResponseEntity.noContent().build();
	}
}
