package entidades;

import java.util.ArrayList;
import java.util.Set;

public class Artista extends Persona {

	private Long idArt;
	private String apodo = null;
	private Set<Especialidad> especialidades;
	private ArrayList<Numero> numeros;

	public Artista() {
		super();
	}

	public Artista(Long idArt, String apodo, Set<Especialidad> especialidades,
			ArrayList<Numero> numeros) {
		super();
		this.idArt = idArt;
		this.apodo = apodo;
		this.especialidades = especialidades;
		this.setNumeros(numeros);
	}

	public Long getIdArt() {
		return idArt;
	}

	public void setIdArt(Long idArt) {
		this.idArt = idArt;
	}

	public String getApodo() {
		return apodo;
	}

	public void setApodo(String apodo) {
		this.apodo = apodo;
	}

	public Set<Especialidad> getEspecialidades() {
		return especialidades;
	}

	public void setEspecialidades(Set<Especialidad> especialidades) {
		this.especialidades = especialidades;
	}

	public ArrayList<Numero> getNumeros() {
		return numeros;
	}

	public void setNumeros(ArrayList<Numero> numeros) {
		this.numeros = numeros;
	}

	@Override
	public String toString() {
		return "Artista [idArt=" + idArt + ", apodo=" + apodo
				+ ", especialidades=" + especialidades + "]";
	}

}
