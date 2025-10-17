package entidades;

import java.io.Serializable;
import java.time.LocalDate;

public class Espectaculo implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long id;
	private String nombre;
	private LocalDate fechaini;
	private LocalDate fechafin;
	private Long idCoord;

	public Espectaculo() {
	}


	public Espectaculo(Long id, String nombre, LocalDate fechaini,
			LocalDate fechafin, Long idCoord) {
		super();
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

	
	public Long getIdCoord() {
		return idCoord;
	}


	public void setIdCoord(Long idCoord) {
		this.idCoord = idCoord;
	}


	@Override
	public String toString() {
		return "Espectaculo [id=" + id + ", nombre=" + nombre + ", fechaini="
				+ fechaini + ", fechafin=" + fechafin + "]";
	}

	public String espectaculoParaInvitados() {
		return "Espectaculo [id=" + id + ", nombre=" + nombre + ", fechaini="
				+ fechaini + ", fechafin=" + fechafin + "]";
	}

}
