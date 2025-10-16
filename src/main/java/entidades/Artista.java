package entidades;

import java.util.Set;

public class Artista extends Persona{
	
	private Long idArt;
	private String apodo=null;
	private Set<Especialidad>especialidades;
	
	//asd
	public Artista() {
		super(); 
	}


	public Artista(Long idArt, String apodo, Set<Especialidad> especialidades) {
		super();
		this.idArt = idArt;
		this.apodo = apodo;
		this.especialidades = especialidades;
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


	@Override
	public String toString() {
		return "Artista [idArt=" + idArt + ", apodo=" + apodo + ", especialidades=" + especialidades + "]";
	}
	
	 
	

	
	
	

}
