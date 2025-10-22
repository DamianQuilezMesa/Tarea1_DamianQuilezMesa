package entidades;

import java.util.ArrayList;

public class Numero {

	private Long id;
	private int orden;
	private String nombre;
	private Double duracion;
	private Espectaculo espectaculo;
	private ArrayList<Artista> artistas;

	public Numero() {
	}

	public Numero(Long id, int orden, String nombre, Double duracion, Espectaculo espectaculo, ArrayList<Artista> artistas) {
		super();
		this.id = id;
		this.orden = orden;
		this.nombre = nombre;
		this.duracion = duracion;
		this.espectaculo = espectaculo;
		this.artistas = artistas;
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

	public Espectaculo getEspectaculo() {
		return espectaculo;
	}

	public void setEspectaculo(Espectaculo espectaculo) {
		this.espectaculo = espectaculo;
	}

	@Override
	public String toString() {
		return "Numero [id=" + id + ", orden=" + orden + ", nombre=" + nombre
				+ ", duracion=" + duracion + ", espectaculo=" + espectaculo
				+ ", artistas=" + artistas + "]";
	}
	
	

	

}
	
