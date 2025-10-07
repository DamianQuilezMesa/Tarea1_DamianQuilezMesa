package entidades;

import java.time.LocalDate;

public class Numero {
	
	private Long id;
	private int orden;
	private String nombre;
	private Double duracion;
	
	
	public Numero() {
		super();
	}


	public Numero(Long id, int orden, String nombre, Double duracion) {
		super();
		this.id = id;
		this.orden = orden;
		this.nombre = nombre;
		this.duracion = duracion;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public int getOrden() {
		return orden;
	}


	public void setOrden(int orden) {
		this.orden = orden;
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public Double getDuracion() {
		return duracion;
	}


	public void setDuracion(Double duracion) {
		this.duracion = duracion;
	}


	@Override
	public String toString() {
		return "Numero [id=" + id + ", orden=" + orden + ", nombre=" + nombre + ", duracion=" + duracion + "]";
	}
	
	

}
