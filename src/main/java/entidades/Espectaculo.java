package entidades;

import java.io.Serializable;
import java.time.LocalDate;

public class Espectaculo implements Serializable {
	

	private static final long serialVersionUID = 1L;
	private long id;
	private String nombre;
	private LocalDate fechaini;
	private LocalDate fechafin;
	private long idCoord;
	
	
	
	
	public Espectaculo() {
	}


	public Espectaculo(long id, String nombre, LocalDate fechaini, LocalDate fechafin, long idCoord) {
		this.id = id;
		this.nombre = nombre;
		this.fechaini = fechaini;
		this.fechafin = fechafin;
		this.idCoord = idCoord;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public LocalDate getFechaini() {
		return fechaini;
	}


	public void setFechaini(LocalDate fechaini) {
		this.fechaini = fechaini;
	}


	public LocalDate getFechafin() {
		return fechafin;
	}


	public void setFechafin(LocalDate fechafin) {
		this.fechafin = fechafin;
	}

	public long getIdCoord() {
		return idCoord;
	}


	public void setIdCoord(long idCoord) {
		this.idCoord = idCoord;
	}

	@Override
	public String toString() {
		return "Espectaculo [id=" + id + ", nombre=" + nombre + ", fechaini=" + fechaini + ", fechafin=" + fechafin
				+ "]";
	}
	
	public String toStringMostrarEspectáculos() {
		return "Espectaculo [id=" + id + ", nombre=" + nombre + ", fechaini=" + fechaini + ", fechafin=" + fechafin
				+ "]";
	}


	
	
	

}
