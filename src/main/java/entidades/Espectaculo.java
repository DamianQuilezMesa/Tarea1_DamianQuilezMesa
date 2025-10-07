package entidades;

import java.time.LocalDate;

public class Espectaculo {
	
	private Long id;
	private String nombre;
	private LocalDate fechaini;
	private LocalDate fechafin;
	
	
	public Espectaculo() {
		super();
	}


	public Espectaculo(Long id, String nombre, LocalDate fechaini, LocalDate fechafin) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.fechaini = fechaini;
		this.fechafin = fechafin;
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


	@Override
	public String toString() {
		return "Espectaculo [id=" + id + ", nombre=" + nombre + ", fechaini=" + fechaini + ", fechafin=" + fechafin
				+ "]";
	}
	
	
	

}
