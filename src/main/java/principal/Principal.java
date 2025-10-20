package principal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.Scanner;

import entidades.Credenciales;
import entidades.Espectaculo;
import entidades.Perfiles;
import entidades.Sesion;

public class Principal {
	static Scanner sc = new Scanner(System.in);
	static Sesion sesion = new Sesion();
	static DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("dd/MM/yyyy");

	public static void main(String[] args) {
		
		System.out.println("Bienvenido al Circo!!");

		while (sesion.getPerfil()!=(null)) {
			selectorMenu(sesion);
		}

		System.out.println("Fin del programa");
	}

	public static void selectorMenu(Sesion sesion) {
		switch (sesion.getPerfil()) {
		case INVITADO:
			menuInvitado(sesion);
			break;
		case ADMIN:
			menuAdmin(sesion);
			break;
		case ARTISTA:
			menuArtista(sesion);
			break;
		case COORDINACION:
			menuCoordinacion(sesion);
			break;
		default:
			System.out.println("Sesion inválida");
			break;
		}
	}

	public static Sesion logIn(Sesion sesion) {
		String nomusuario;
		String contrasenia;
		boolean credencialesCorrectas = false;

		do {
			System.out.println(
					"Introduce tu nombre de usuario (o escribe 'salir' para cancelar):");
			nomusuario = sc.nextLine().trim();
			if ("salir".equalsIgnoreCase(nomusuario)) {
				System.out.println("Login cancelado.");
				break;
			}

			System.out.println("Introduce tu contraseña:");
			contrasenia = sc.nextLine();

			// Asignamos el resultado de las comprobaciones a la variable
			boolean esAdmin = verificarLoginAdmin(nomusuario, contrasenia,
					sesion);
			boolean esUsuario = verificarLoginPerfiles(nomusuario, contrasenia,
					sesion);
			credencialesCorrectas = esAdmin || esUsuario;

			if (!credencialesCorrectas) {
				System.out.println(
						"Credenciales incorrectas. Vuelve a intentarlo.");
			}
		} while (!credencialesCorrectas);
		return sesion;
	}

	public static boolean verificarLoginAdmin(String nomusuario,
			String contrasenia, Sesion sesion) {
		boolean credencialesCorrectas = false;

		// Cargamos el application.properties para extraer el usuario
		// y contraseña de admin
		Properties propiedades = new Properties();
		// Podria pedir manualmente la ruta si no carga correctamente
		try {
			propiedades.load(new FileInputStream(
					"src/main/resources/application.properties"));
		} catch (FileNotFoundException e) {
			System.out.println("No se encuentra el fichero");
			credencialesCorrectas = false;
		} catch (IOException e) {
			System.out.println("El formatin ye incorrectu");
			credencialesCorrectas = false;
		}

		String usuarioAdmin = propiedades.getProperty("usuarioAdmin");
		String contraseñaAdmin = propiedades.getProperty("passwordAdmin");

		if (nomusuario.equals(usuarioAdmin)
				&& contrasenia.equals(contraseñaAdmin)) {
			System.out.println("Bienvenido Administrador!");
			sesion.setPerfil(Perfiles.ADMIN);
			credencialesCorrectas = true;
		} else {
			credencialesCorrectas = false;
		}
		return credencialesCorrectas;
	}

	public static boolean verificarLoginPerfiles(String usuario,
			String contrasena, Sesion sesion) {
		boolean credencialesCorrectas = false;

		Credenciales credenciales = new Credenciales();

		File archivo = new File("ficheros/credenciales.txt");

		if (!archivo.exists()) {
			System.out.println("❌ El archivo credenciales.txt no existe.");
			credencialesCorrectas = false;
		}

		try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
			String linea;
			while ((linea = br.readLine()) != null) {
				String[] partes = linea.split("\\|");
				if (partes.length < 7)
					continue;

				Long id = Long.parseLong(partes[0].trim());
				String usuarioArchivo = partes[1].trim().toLowerCase();
				String contrasenaArchivo = partes[2].trim();
				String perfil = partes[6].trim().toLowerCase();

				if (usuarioArchivo.equals(usuario.toLowerCase().trim())
						&& contrasenaArchivo.equals(contrasena.trim())) {

					switch (perfil) {
					case "artista":
						System.out
								.println("✅ Usuario autenticado como ARTISTA");
						credenciales = new Credenciales(id, usuarioArchivo,
								contrasenaArchivo, Perfiles.ARTISTA);
						sesion.setCredenciales(credenciales);
						sesion.setPerfil(Perfiles.ARTISTA);
						credencialesCorrectas = true;
						break;
					case "coordinacion":
						System.out.println(
								"✅ Usuario autenticado como COORDINACIÓN");
						credenciales = new Credenciales(id, usuarioArchivo,
								contrasenaArchivo, Perfiles.COORDINACION);
						sesion.setCredenciales(credenciales);
						sesion.setPerfil(Perfiles.COORDINACION);
						credencialesCorrectas = true;
						break;
					default:
						System.out
								.println("⚠️ Perfil no reconocido: " + perfil);
						credencialesCorrectas = false;
					}

				} 

			}
		} catch (IOException e) {
			System.out.println("❌ Error al leer el archivo de credenciales.");
			e.printStackTrace();

		}

		return credencialesCorrectas;
	}

	public static void crearEspectaculo() {

		try {
			System.out.println("Introduce el identificador (número entero):");
			Long id = sc.nextLong();// Maximo 25 caracteres
			sc.nextLine(); // Limpia el buffer

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

	public static void menuAdmin(Sesion sesion) {
		String opcionMenu = "";
		
		System.out.println(
				"Hola " + Perfiles.ADMIN + ", bienvenido.");
		
		do {
			System.out.println("-----SELECCIONA UNA OPCION-----");
			System.out.println("1-Ver espectáculos");
			System.out.println("2-Gestionar espectáculos");
			System.out.println("3-Gestionar personas");
			System.out.println("4-Cerrar sesión");
			opcionMenu = sc.nextLine();

			switch (opcionMenu) {
			case "1":
				mostrarEspectaculos();
				break;
			case "2":
				System.out.println("Gestion de espectáculos");
				break;
			case "3":
				System.out.println("Gestion del personal");
				break;
			case "4":
				System.out.println("Cerrando sesion");
				sesion.setCredenciales(null);
				sesion.setPerfil(Perfiles.INVITADO);
				break;

			default:
				System.out.println("Opción no valida, vuelve a intentarlo");
				break;
			}
		} while (!opcionMenu.equals("4"));

	}

	public static void menuArtista(Sesion sesion) {
		String opcionMenu = "";
		do {
			System.out.println(
					"Hola " + Perfiles.ARTISTA + ", selecciona una opción");
			System.out.println("1-Ver espectáculos");
			System.out.println("2-Gestionar espectaculos");
			System.out.println("3-Cerrar sesión");
			opcionMenu = sc.nextLine();

			switch (opcionMenu) {
			case "1":
				mostrarEspectaculos();
				break;
			case "2":
				System.out.println("Gestion de espectáculos");
				break;
			case "3":
				System.out.println("Cerrando sesion");
				sesion.setCredenciales(null);
				sesion.setPerfil(Perfiles.INVITADO);
				break;

			default:
				System.out.println("Opción no valida, vuelve a intentarlo");
				break;
			}
		} while (!opcionMenu.equals("3"));

	}

	public static void menuCoordinacion(Sesion sesion) {
		String opcionMenu = "";
		do {
			System.out.println(
					"Hola " + Perfiles.COORDINACION + ", selecciona una opción");
			System.out.println("1-Ver espectáculos");
			System.out.println("2-Cerrar sesión");
			opcionMenu = sc.nextLine();

			switch (opcionMenu) {
			case "1":
				mostrarEspectaculos();
				break;
			case "2":
				System.out.println("Cerrando sesion");
				sesion.setCredenciales(null);
				sesion.setPerfil(Perfiles.INVITADO);
				break;

			default:
				System.out.println("Opción no valida, vuelve a intentarlo");
				break;
			}
		} while (!opcionMenu.equals("2"));

	}

	public static void menuInvitado(Sesion sesion) {
		String opcionMenu = "";

		do {
			System.out.println(
					"Hola " + Perfiles.INVITADO + ", selecciona una opción");
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
				System.out.println("Saliendo del programa");
				sesion.setPerfil(null);
				break;

			default:
				System.out.println("Opción no valida, vuelve a intentarlo");
				break;
			}
		} while (sesion.getPerfil()==(Perfiles.INVITADO));

	}
}
