package Proyecto.MegaWeb2.__BackEnd.Dto;

public class AnadirComentarioDTO {

	private String comentario;
	private int idConsulta;

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public int getIdConsulta() {
		return idConsulta;
	}

	public void setIdConsulta(int idConsulta) {
		this.idConsulta = idConsulta;
	}

	@Override
	public String toString() {
		return "AnadirComentarioDTO [comentario=" + comentario + ", idConsulta=" + idConsulta + "]";
	}

}
