package entidades;

import java.time.LocalDate;

public class Coordinacion extends Persona {

	private Long idCoord;
	private Boolean senior = false;
	private LocalDate fechasenior = null;

	public Coordinacion() {
		super();
	}

	public Coordinacion(Long idCoord, Boolean senior, LocalDate fechasenior) {
		super();
		this.idCoord = idCoord;
		this.senior = senior;
		this.fechasenior = fechasenior;
	}

	public Long getIdCoord() {
		return idCoord;
	}

	public void setIdCoord(Long idCoord) {
		this.idCoord = idCoord;
	}

	public Boolean getSenior() {
		return senior;
	}

	public void setSenior(Boolean senior) {
		this.senior = senior;
	}

	public LocalDate getFechasenior() {
		return fechasenior;
	}

	public void setFechasenior(LocalDate fechasenior) {
		this.fechasenior = fechasenior;
	}

	@Override
	public String toString() {
		return "Coordinacion [idCoord=" + idCoord + ", senior=" + senior
				+ ", fechasenior=" + fechasenior + "]";
	}

}
