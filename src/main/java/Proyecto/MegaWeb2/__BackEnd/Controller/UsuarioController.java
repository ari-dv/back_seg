package Proyecto.MegaWeb2.__BackEnd.Controller;

import Proyecto.MegaWeb2.__BackEnd.Dto.UsuarioCreateRequestDTO;
import Proyecto.MegaWeb2.__BackEnd.Dto.EditarUsuarioDTO;
import Proyecto.MegaWeb2.__BackEnd.Dto.ListarUsuarioDTO;
import Proyecto.MegaWeb2.__BackEnd.Service.UsuarioService;
import Proyecto.MegaWeb2.__BackEnd.Service.AuditService;
import Proyecto.MegaWeb2.__BackEnd.Util.URLEncryptionUtil;
import Proyecto.MegaWeb2.__BackEnd.Security.UsuarioSesionUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/usuarios")
public class UsuarioController {

	private final UsuarioService usuarioService;

	@Autowired
	private AuditService auditService;

	@Autowired
	public UsuarioController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	/**
	 * Obtiene la IP del cliente
	 */
	private String obtenerIPCliente(HttpServletRequest request) {
		String xForwardedFor = request.getHeader("X-Forwarded-For");
		if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
			return xForwardedFor.split(",")[0].trim();
		}
		return request.getRemoteAddr();
	}

	/**
	 * Crea un nuevo usuario a partir del JSON recibido en el cuerpo y
	 * devuelve un mapa con el id generado ENCRIPTADO.
	 */
	@PostMapping
	public ResponseEntity<Map<String, String>> crearUsuario(
			@RequestBody UsuarioCreateRequestDTO dto,
			HttpServletRequest request
	) {
		String ipCliente = obtenerIPCliente(request);
		
		int idUsuario = usuarioService.crearUsuario(dto);
		if (idUsuario > 0) {
			// ✅ ENCRIPTAR EL ID ANTES DE DEVOLVERLO
			String idEncriptado = URLEncryptionUtil.encriptarId(idUsuario);
			
			auditService.registrarEvento("SISTEMA", "CREAR_USUARIO", 
				"Usuario creado: " + dto.getEmail(), ipCliente, "/api/auth/usuarios", "POST");
			
			return ResponseEntity
					.status(HttpStatus.CREATED)
					.body(Map.of("idUsuario", idEncriptado));
		} else {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "-1"));
		}
	}

	/**
	 * Lista todos los usuarios.
	 */
	@GetMapping
	public ResponseEntity<List<ListarUsuarioDTO>> listarUsuarios(HttpServletRequest request) {
		String ipCliente = obtenerIPCliente(request);
		auditService.registrarAccesoRecurso("USUARIO", "/api/auth/usuarios", ipCliente);
		
		List<ListarUsuarioDTO> lista = usuarioService.listarUsuarios(null);
		return ResponseEntity.ok(lista);
	}

	/**
	 * Obtiene un usuario por su ID ENCRIPTADO.
	 * 
	 * CAMBIO: 
	 * Antes: GET /api/auth/usuarios/5
	 * Ahora: GET /api/auth/usuarios/Xy9-zRq2
	 */
	@GetMapping("/{idEncriptado}")
	public ResponseEntity<?> obtenerUsuario(
			@PathVariable String idEncriptado,
			HttpServletRequest request
	) {
		String ipCliente = obtenerIPCliente(request);
		
		try {
			// ✅ DESENCRIPTAR EL ID
			Integer id = URLEncryptionUtil.desencriptarId(idEncriptado);
			if (id == null) {
				return ResponseEntity.status(400)
					.body("{\"error\": \"ID inválido\"}");
			}

			List<ListarUsuarioDTO> lista = usuarioService.listarUsuarios(id);
			if (lista.isEmpty()) {
				return ResponseEntity.notFound().build();
			}

			auditService.registrarAccesoRecurso("USUARIO", "/api/auth/usuarios/" + id, ipCliente);
			return ResponseEntity.ok(lista.get(0));
		} catch (Exception e) {
			return ResponseEntity.status(400)
				.body("{\"error\": \"Error al procesar la solicitud\"}");
		}
	}

	/**
	 * Actualiza un usuario existente.
	 * El ID va encriptado en el DTO
	 */
	@PutMapping
	public ResponseEntity<Map<String, String>> editarUsuario(
			@RequestBody EditarUsuarioDTO dto,
			HttpServletRequest request
	) {
		String ipCliente = obtenerIPCliente(request);
		String usuario = (String) request.getAttribute("username");
		
		try {
			usuarioService.editarUsuario(dto);
			
			auditService.registrarEvento(usuario, "ACTUALIZAR_USUARIO", 
				"Usuario actualizado ID: " + dto.getId(), ipCliente, 
				"/api/auth/usuarios", "PUT");
			
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.status(400)
				.body(Map.of("error", "Error al actualizar usuario"));
		}
	}

	/**
	 * Elimina un usuario por su ID ENCRIPTADO.
	 * 
	 * CAMBIO:
	 * Antes: DELETE /api/auth/usuarios/5
	 * Ahora: DELETE /api/auth/usuarios/Xy9-zRq2
	 */
	@DeleteMapping("/{idEncriptado}")
	public ResponseEntity<?> eliminarUsuario(
			@PathVariable String idEncriptado,
			HttpServletRequest request
	) {
		String ipCliente = obtenerIPCliente(request);
		String usuario = (String) request.getAttribute("username");
		
		try {
			// ✅ DESENCRIPTAR EL ID
			Integer id = URLEncryptionUtil.desencriptarId(idEncriptado);
			if (id == null) {
				return ResponseEntity.status(400)
					.body("{\"error\": \"ID inválido\"}");
			}

			usuarioService.eliminarUsuario(id);
			
			auditService.registrarEliminacion(usuario, id, "USUARIO", ipCliente);
			
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.status(400)
				.body("{\"error\": \"Error al eliminar usuario\"}");
		}
	}
}