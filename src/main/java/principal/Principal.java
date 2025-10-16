package principal;

import java.util.Properties;
import java.util.Scanner;
import entidades.Sesion;
import java.io.*;

public class Principal {
	static Scanner sc = new Scanner(System.in);
	static Sesion sesion = new Sesion();

	public static void main(String[] args) {

		menuInvitado();

		System.out.println("Fin del programa");

	}

	public static void menuInvitado() {
		String opcionMenu = "";

		do {
			System.out.println("Selecciona una opción");
			System.out.println("1-Ver espectáculos");
			System.out.println("2-Log in");
			System.out.println("3-Salir del programa");
			opcionMenu = sc.nextLine();

			switch (opcionMenu) {
			case "1":
				verEspectaculos();
				break;
			case "2":
				logIn(sesion);
				break;
			case "3":
				System.out.println("Saliendo del prorama");
				break;

			default:
				System.out.println("Opción no valida, vuelve a intentarlo");
				break;
			}
		} while (!opcionMenu.equals("3"));

	}

	public static void verEspectaculos() {
		System.out.println("Viendo espectáculos...");

	}

	public static void logIn(Sesion sesion) {
		Properties propiedades = new Properties();
		try {
			propiedades.load(new FileInputStream("src/main/resources/application.properties"));
		} catch (FileNotFoundException e) {
			System.out.println("No se encuentra el fichero");
		} catch (IOException e) {
			System.out.println("El formatin ye incorrectu");
		}

		String usuarioAdmin = propiedades.getProperty("usuarioadmin");
		String contraseñaAdmin = propiedades.getProperty("passwordAdmin");

		String nomusuario;
		String contrasenia;

		System.out.println("Introduce tu nombre de usuario");
		nomusuario = sc.nextLine();
		System.out.println("Introduce tu contraseña");
		contrasenia = sc.nextLine();

		if (nomusuario.equals(usuarioAdmin)
				|| contrasenia.equals(contraseñaAdmin)) {
			System.out.println("Admin detectado");
			
		} else {
			System.out.println("Atrás satanás");
		}
	}

}
