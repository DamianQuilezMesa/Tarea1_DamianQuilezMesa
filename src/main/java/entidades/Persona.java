package entidades;

public class Persona {

	private Long id;
	private String email;
	private String nombre;
	private String nacionalidad;

	public Persona() {
	}

	public Persona(Long id, String email, String nombre, String nacionalidad) {
		this.id = id;
		this.email = email;
		this.nombre = nombre;
		this.nacionalidad = nacionalidad;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getNacionalidad() {
		return nacionalidad;
	}

	public void setNacionalidad(String nacionalidad) {
		this.nacionalidad = nacionalidad;
	}

	@Override
	public String toString() {
		return "Persona [id=" + id + ", email=" + email + ", nombre=" + nombre
				+ ", nacionalidad=" + nacionalidad + "]";
	}

}
