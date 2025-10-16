package entidades;

public class Sesion {
	public Credenciales credenciales=null;
	public Perfiles perfil=Perfiles.INVITADO;
	
	public Sesion() {
	}


	public Sesion(Credenciales credenciales, Perfiles perfil) {
		this.credenciales = credenciales;
		this.perfil = perfil;
	}

	public Credenciales getCredenciales() {
		return credenciales;
	}

	public void setCredenciales(Credenciales credenciales) {
		this.credenciales = credenciales;
	}

	public Perfiles getPerfil() {
		return perfil;
	}

	public void setPerfil(Perfiles perfil) {
		this.perfil = perfil;
	}

	@Override
	public String toString() {
		return "Sesion [credenciales=" + credenciales + ", perfil=" + perfil + "]";
	}
	
	
}
