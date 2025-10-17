package principal;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.Scanner;

import entidades.Espectaculo;
import entidades.Perfiles;
import entidades.Sesion;

public class Principal {
	static Scanner sc = new Scanner(System.in);
	static Sesion sesion = new Sesion();
	static DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("dd/MM/yyyy");

	public static void main(String[] args) {
		
		menuInvitado(sesion);
		
		System.out.println("Fin del programa");
	}

	public static void menuInvitado(Sesion sesion) {
		String opcionMenu = "";

		do {
			System.out.println("Selecciona una opción");
			System.out.println("1-Ver espectáculos");
			System.out.println("2-Log in");
			System.out.println("3-Salir del programa");
			opcionMenu = sc.nextLine();

			switch (opcionMenu) {
			case "1":
				mostrarEspectaculos();
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

	public static void logIn(Sesion sesion) {
		Properties propiedades = new Properties();
		try {
			propiedades.load(new FileInputStream(
					"src/main/resources/application.properties"));
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

		if (nomusuario.equals(usuarioAdmin) && contrasenia.equals(contraseñaAdmin)) {
            System.out.println("Bienvenido Administrador!");
            sesion.setPerfil(Perfiles.ADMIN);
            return;
        }

	}

	public static void crearEspectaculo() {

		try {
			System.out.println("Introduce el identificador (número entero):");
			Long id = sc.nextLong();// Maximo 25 caracteres
			sc.nextLine(); // limpia el buffer

			System.out.println("Introduce el nombre:");
			String nombre = sc.nextLine();

			System.out.println("Introduce la fecha de inicio (dd/mm/aaaa):");
			String fechaIniStr = sc.nextLine();
			LocalDate fechaini = LocalDate.parse(fechaIniStr, formatter);

			// Maximo 1 año mas que la fecha de inicio
			System.out.println("Introduce la fecha de fin (dd/mm/aaaa):");
			String fechaFinStr = sc.nextLine();
			LocalDate fechafin = LocalDate.parse(fechaFinStr, formatter);

			System.out.println("Introduce el identificador del coordinador:");
			Long idCoord = sc.nextLong();

			Espectaculo esp1 = new Espectaculo(id, nombre, fechaini, fechafin,
					idCoord);
			System.out.println("Espectáculo creado: " + esp1);

		} catch (Exception e) {
			System.out.println(
					"Error al introducir los datos: " + e.getMessage());
		} finally {

			System.out.println("Objeto creado ");
		}

	}

	// Repetido
	public static void mostrarEspectaculos() {
		System.out.println("ESPECTÁCULOS:");
		try (ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream("espectaculos.dat"))) {
			Espectaculo espectaculo = (Espectaculo) ois.readObject();
			System.out.println(espectaculo.espectaculoParaInvitados());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
