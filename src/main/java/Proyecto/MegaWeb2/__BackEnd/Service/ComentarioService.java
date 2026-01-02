package Proyecto.MegaWeb2.__BackEnd.Service;

import Proyecto.MegaWeb2.__BackEnd.Dto.AnadirComentarioDTO;
import Proyecto.MegaWeb2.__BackEnd.Dto.ComentarioResponseDTO;
import Proyecto.MegaWeb2.__BackEnd.Repository.ComentarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComentarioService {

	@Autowired
	private ComentarioRepository comentarioRepository;

	// ComentarioService.java
	public List<ComentarioResponseDTO> listarComentarios() {
		return comentarioRepository.listarComentarios();
	}

	// Añadir comentario
	public int registrarComentario(AnadirComentarioDTO dto, int idUsuario) {
		return comentarioRepository.registrarComentario(dto, idUsuario);
	}

}
