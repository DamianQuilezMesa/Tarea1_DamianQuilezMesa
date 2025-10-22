package principal;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
	static File ficheroEspectaculos = new File("espectaculos.dat");
	static File ficheroCredenciales = new File("ficheros/credenciales.txt");

	public static void main(String[] args) {

		System.out.println("Bienvenido al Circo!!");

		while (sesion.getPerfil() != (null)) {
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
			// Introducimos el nombre de usuario y le damos opcion a cancelar el
			// login introduciendo
			// la palabra salir en minúscula
			System.out.println(
					"Introduce tu nombre de usuario (o escribe 'salir' para cancelar):");
			nomusuario = sc.nextLine().trim();
			if ("salir".equalsIgnoreCase(nomusuario)) {
				System.out.println("Login cancelado.");
				break;
			}

			// Introducimos la contraseña y le damos opcion a cancelar el login
			// introduciendo
			// la palabra salir en minúscula
			System.out.println(
					"Introduce tu contraseña (o escribe 'salir' para cancelar):");
			contrasenia = sc.nextLine();
			if ("salir".equalsIgnoreCase(contrasenia)) {
				System.out.println("Login cancelado.");
				break;
			}

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

		/*
		 * Almacenamos en un string el string asociado a usuarioAdmin y a
		 * passwordAdmin que en este caso es admin para ambos
		 */
		String usuarioAdmin = propiedades.getProperty("usuarioAdmin");
		String contraseñaAdmin = propiedades.getProperty("passwordAdmin");

		/*
		 * Comprobamos que las credenciales introducidas por teclado coincidan
		 * con las de admin, si es asi le establecemos el tipo de perfil a ADMIN
		 * y devolvemos true, si no false
		 */
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

	public static void menuAdmin(Sesion sesion) {
		String opcionMenu = "";

		System.out.println("Hola " + Perfiles.ADMIN + ", bienvenido.");

		do {
			System.out.println("-----SELECCIONA UNA OPCION-----");
			System.out.println("1 - Gestionar espectáculos");
			System.out.println("2 - Gestionar personas");
			System.out.println("3 - Cerrar sesión");
			opcionMenu = sc.nextLine();

			switch (opcionMenu) {
			case "1":
				gestionarEspectaculos();
				break;
			case "2":
				gestionarPersonas();
				break;
			case "3":
				logOut();
				break;

			default:
				System.out.println("Opción no valida, vuelve a intentarlo");
				break;
			}
		} while (!opcionMenu.equals("3"));

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
				logOut();
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
			System.out.println("Hola " + Perfiles.COORDINACION
					+ ", selecciona una opción");
			System.out.println("1-Gestionar espectáculos");
			System.out.println("2-Cerrar sesión");
			opcionMenu = sc.nextLine();

			switch (opcionMenu) {
			case "1":
				gestionarEspectaculos();
				break;
			case "2":
				logOut();
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
		} while (sesion.getPerfil() == (Perfiles.INVITADO));

	}

	public static void gestionarEspectaculos() {
		String opcionMenu = "";

		do {
			System.out.println(
					"Hola " + sesion.getPerfil() + ", selecciona una opción");
			System.out.println("1 - Ver Espectaculos");
			System.out.println("2 - Crear un nuevo Espectaculo");
			System.out.println("3 - Salir de la Gestion de Espectaculos");
			opcionMenu = sc.nextLine();

			switch (opcionMenu) {
			case "1":
				mostrarEspectaculos();
				break;
			case "2":
				crearEspectaculo();
				break;
			case "3":
				System.out.println("Saliendo de la gestion de espectaculos");
				break;

			default:
				System.out.println("Opción no valida, vuelve a intentarlo");
				break;
			}
		} while (!opcionMenu.contentEquals("3"));
	}

	public static void crearEspectaculo() {
	    try {
	        Long id = generarNuevoIdEspectaculo();

	        System.out.println("Introduce el nombre (máx. 25 caracteres):");
	        String nombre = sc.nextLine().trim();
	        if (nombre.length() > 25) {
	            System.out.println("❌ El nombre no puede superar los 25 caracteres.");
	            return;
	        }
	        if (existeNombreEspectaculo(nombre)) {
	            System.out.println("❌ Ya existe un espectáculo con ese nombre.");
	            return;
	        }

	        System.out.println("Introduce la fecha de inicio (dd/MM/yyyy):");
	        LocalDate fechaini = LocalDate.parse(sc.nextLine(), formatter);

	        System.out.println("Introduce la fecha de fin (dd/MM/yyyy):");
	        LocalDate fechafin = LocalDate.parse(sc.nextLine(), formatter);

	        if (!fechafin.isAfter(fechaini)) {
	            System.out.println("❌ La fecha de fin debe ser posterior a la de inicio.");
	            return;
	        }
	        if (fechaini.plusYears(1).isBefore(fechafin)) {
	            System.out.println("❌ El periodo no puede superar 1 año.");
	            return;
	        }

	        Long idCoord = null;
	        if (sesion.getPerfil() == Perfiles.COORDINACION) {
	            idCoord = sesion.credenciales.getId(); // cuidado: ¿es idPersona o idCoordinador?
	        } else if (sesion.getPerfil() == Perfiles.ADMIN) {
	            System.out.println("Introduce el identificador del coordinador:");
	            Long candidato = Long.parseLong(sc.nextLine());
	            if (!esCoordinadorValido(candidato)) {
	                System.out.println("❌ El id introducido no corresponde a un coordinador válido.");
	                return;
	            }
	            idCoord = candidato;
	        } else {
	            System.out.println("❌ No tienes permisos para crear espectáculos.");
	            return;
	        }

	        Espectaculo nuevoEspectaculo = new Espectaculo(id, nombre, fechaini, fechafin, idCoord);
	        guardarEspectaculo(nuevoEspectaculo);

	        System.out.println("✅ Espectáculo creado: " + nuevoEspectaculo);

	    } catch (Exception e) {
	        System.out.println("Error al introducir los datos: " + e.getMessage());
	    }
	}
	
	public static boolean esCoordinadorValido(Long idCoord) {
        if (!ficheroCredenciales.exists() || ficheroCredenciales.length() == 0) 
        	return false;

        try (BufferedReader br = new BufferedReader(new FileReader(ficheroCredenciales))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split("\\|");
                if (partes.length >= 7) {
                    long idPersona = Long.parseLong(partes[0].trim());
                    String perfil = partes[6].trim().toLowerCase();

                    if (idPersona == idCoord && perfil.equals("coordinacion")) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("⚠️ Error leyendo credenciales.txt: " + e.getMessage());
        }
        return false;
    }

		
	public static boolean existeNombreEspectaculo(String nombreBuscado) {
		if (!ficheroEspectaculos.exists() || ficheroEspectaculos.length() == 0)
			return false;

		try (ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream(ficheroEspectaculos))) {
			List<Espectaculo> espectaculos = (List<Espectaculo>) ois
					.readObject();
			for (Espectaculo e : espectaculos) {
				if (e.getNombre().equalsIgnoreCase(nombreBuscado)) {
					return true;
				}
			}
		} catch (Exception e) {
			System.out.println(
					"⚠️ Error leyendo espectáculos: " + e.getMessage());
		}
		return false;
	}

	public static void guardarEspectaculo(Espectaculo nuevo) {
		List<Espectaculo> espectaculos = new ArrayList<>();

		// 1. Leer los espectáculos existentes (si el fichero ya existe)
		if (ficheroEspectaculos.exists() && ficheroEspectaculos.length() > 0) {
			try (ObjectInputStream ois = new ObjectInputStream(
					new FileInputStream(ficheroEspectaculos))) {
				espectaculos = (List<Espectaculo>) ois.readObject();
			} catch (Exception e) {
				System.out.println(
						"No se pudieron leer los espectáculos existentes: "
								+ e.getMessage());
			}
		}

		// 2. Añadir el nuevo espectáculo
		espectaculos.add(nuevo);

		// 3. Guardar la lista completa en el fichero
		try (ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(ficheroEspectaculos))) {
			oos.writeObject(espectaculos);
			System.out.println("✅ Espectáculo guardado correctamente en "
					+ "espectaculos.dat");
		} catch (IOException e) {
			System.out.println(
					"❌ Error al guardar el espectáculo: " + e.getMessage());
		}
	}

	public static void mostrarEspectaculos() {
		System.out.println("ESPECTÁCULOS:");

		if (!ficheroEspectaculos.exists() || ficheroEspectaculos.length() == 0) {
			System.out.println("No hay espectáculos registrados.");
			return;
		}

		try (ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream(ficheroEspectaculos))) {
			// Leemos la lista completa
			List<Espectaculo> espectaculos = (List<Espectaculo>) ois
					.readObject();

			if (espectaculos.isEmpty()) {
				System.out.println("No hay espectáculos registrados.");
			} else {
				for (Espectaculo e : espectaculos) {
					// Método que muestra solo lo que puede ver un invitado
					System.out.println(e.espectaculoParaInvitados());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Fallo al cargar el archivo");
		}
	}

	public static void gestionarPersonas() {
		String opcionMenu = "";

		do {
			System.out.println(
					"Hola " + sesion.getPerfil() + ", selecciona una opción");
			System.out.println("1-Introducir una persona");
			System.out.println("2-Salir de la gestión de Personas");
			opcionMenu = sc.nextLine();

			switch (opcionMenu) {
			case "1":

				break;
			case "2":
				System.out.println("Saliendo de gestión de Personas");
				break;

			default:
				System.out.println("Opción no valida, vuelve a intentarlo");
				break;
			}
		} while (!opcionMenu.contentEquals("2")
				&& sesion.getPerfil().equals(Perfiles.ADMIN));
	}

	public static void logOut() {
		System.out.println("Cerrando sesion");
		sesion.setCredenciales(null);
		sesion.setPerfil(Perfiles.INVITADO);
	}

	public static long generarNuevoIdEspectaculo() {
		long idEspectaculo = 0;
		
		if (ficheroEspectaculos.exists() && ficheroEspectaculos.length() > 0) {
			try (ObjectInputStream ois = new ObjectInputStream(
					new FileInputStream(ficheroEspectaculos))) {
				List<Espectaculo> espectaculos = (List<Espectaculo>) ois
						.readObject();
				for (Espectaculo espectaculo : espectaculos) {
					if (espectaculo.getId() > idEspectaculo) {
						idEspectaculo = espectaculo.getId();
					}
				}
			} catch (Exception e) {
				System.out.println(
						"⚠️ Error leyendo espectaculos.dat: " + e.getMessage());
			}
		}
		return idEspectaculo + 1;
	}

	public static long generarNuevoIdPersona() {
		long idPersona = 0;
		try (BufferedReader br = new BufferedReader(
				new FileReader("ficheros/credenciales.txt"))) {
			String linea;
			while ((linea = br.readLine()) != null) {
				String[] partes = linea.split("\\|");
				long id = Long.parseLong(partes[0].trim());
				if (id > idPersona) {
					idPersona = id;
				}
			}
		} catch (IOException e) {
			System.out.println(
					"⚠️ Error leyendo credenciales.txt: " + e.getMessage());
		}
		return idPersona + 1;
	}

	public void cargarArchivo() {
		
	}
}
